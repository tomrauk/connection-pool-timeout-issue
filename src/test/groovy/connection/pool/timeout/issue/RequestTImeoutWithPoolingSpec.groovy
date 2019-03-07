package connection.pool.timeout.issue

import com.github.tomakehurst.wiremock.junit.WireMockRule
import io.micronaut.context.ApplicationContext
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.runtime.server.EmbeddedServer
import org.junit.Rule
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

import static com.github.tomakehurst.wiremock.client.WireMock.*
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options

class RequestTImeoutWithPoolingSpec extends Specification {

  @Rule
  WireMockRule wireMockRule = new WireMockRule(options().port(9090))

  @Shared
  @AutoCleanup
  EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer)

  def "reuse of a conenction with readtimeout handler fails when requests combined time takes more than the timeout"() {
    setup:
    wireMockRule.stubFor(get(urlEqualTo('/test'))
      .willReturn(aResponse()
      .withHeader('Content-Type', 'text/plain')
      .withBody("this works but wait for the timeout on the next request")
      .withFixedDelay(3000)))

    expect: 'initial request works'
    new URL("${embeddedServer.URL}/test").openConnection().getInputStream().text == 'this works but wait for the timeout on the next request'

    when: 'we wait for more then the readtime out (5s) on the original request'
    sleep 2001
    HttpURLConnection openConnection = new URL("${embeddedServer.URL}/test").openConnection()

    then:
    openConnection.getResponseCode() == 500
  }
}
