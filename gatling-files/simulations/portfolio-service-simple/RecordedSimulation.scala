
import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class RecordedSimulation extends Simulation {

	val httpProtocol = http
		.baseURL("http://localhost:8878")
		.disableFollowRedirect
		.disableAutoReferer
		.acceptHeader("*/*")
		.acceptEncodingHeader("gzip, deflate")
		.acceptLanguageHeader("en-US,en;q=0.8")
		.contentTypeHeader("application/json")
		.userAgentHeader("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.35 Safari/537.36")

	val headers_0 = Map(
		"Cache-Control" -> "no-cache",
		"Origin" -> "chrome-extension://fdmmgilgnpjigdojojpjoooidkmcomcm")

    val uri1 = "localhost"

	val scn = scenario("RecordedSimulation")
		.exec(http("request_0")
			.post("/resthub/user")
			.headers(headers_0)
			.body(RawFileBody("RecordedSimulation_0000_request.txt")))

	setUp(scn.inject(atOnceUsers(1000))).protocols(httpProtocol)
  
	 //setUp(scn.inject(rampUsers(1000) over (30 seconds))).protocols(httpProtocol)

      
}