package connection.pool.timeout.issue

import io.micronaut.http.annotation.Get
import io.micronaut.http.client.annotation.Client

@Client('http://localhost:9090')
interface TimeoutClient {

  @Get('/test')
  String get()
}
