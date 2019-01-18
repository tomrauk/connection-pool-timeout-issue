package connection.pool.timeout.issue

import com.github.tomakehurst.wiremock.junit.WireMockRule
import io.micronaut.context.ApplicationContext
import io.micronaut.http.client.HttpClient
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

  @Shared @AutoCleanup EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer)

  @Shared @AutoCleanup HttpClient client = HttpClient.create(embeddedServer.URL)

  def "the request works but wait for the timeout exception"() {
    setup:
    wireMockRule.stubFor(get(urlEqualTo('/test'))
      .willReturn(aResponse()
      .withHeader('Content-Type', 'text/plain')
      .withBody("this works but wait for the uncaught exception")))

    when:
    String response = client.toBlocking().retrieve('/test', String)
    sleep 10000

    then:
    response == 'this works but wait for the uncaught exception'

    /*
    Take a look at the logs.
    WARN  i.n.channel.DefaultChannelPipeline - An exceptionCaught() event was fired, and it reached at the tail of the pipeline. It usually means the last handler in the pipeline did not handle the exception.
io.netty.handler.timeout.ReadTimeoutException: null
     */
  }
}
