package connection.pool.timeout.issue

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get

import javax.inject.Inject

@Controller('/')
class TestController {

  @Inject
  TimeoutClient timeoutClient

  @Get('/test')
  String get() {
    timeoutClient.get()
  }
}
