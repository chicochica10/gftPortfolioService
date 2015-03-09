package com.gft

import akka.actor._

import spray.routing._
import spray.http.StatusCodes
import spray.httpx.SprayJsonSupport._
import spray.routing.RequestContext
import akka.util.Timeout
import scala.concurrent.duration._

class RestInterface extends HttpServiceActor with RestApi {
  def receive = runRoute(routes)
}

trait RestApi extends HttpService with RemoteDictionaryCreator with ActorLogging  {
  actor: Actor =>
  import context.dispatcher //para poder emplear pipe
 
  implicit val timeout = Timeout(10 seconds)
  import akka.pattern.ask
  import akka.pattern.pipe
  
  import com.gft.portfolio.actors.PortfolioUserActor
  import com.gft.portfolio.actors.PortfolioUserActor._
 
  
  val remoteDictionary = createRemoteDictionary
    
  def routes: Route =
    path("resthub" / "user") {
      post {
        entity(as[UserSetDataMessage]) { obj => requestContext =>
        val responder = createResponder (requestContext)
        val userRequestService = context.actorOf(Props(classOf[PortfolioUserActor],remoteDictionary)/*, "UserRequestService"*/)
        userRequestService.ask(obj).pipeTo(responder)
        }
      }
    } 
  
  def createResponder (requestContext: RequestContext) ={
    context.actorOf(Props(new Responder(requestContext)))
  }
}

class Responder (requestContext: RequestContext) extends Actor with ActorLogging {
  //import com.gft.portfolio.actors.PortfolioUserActor
  import com.gft.portfolio.actors.PortfolioUserActor._
  import spray.httpx.SprayJsonSupport._
   
  def receive = {
     case Facts(facts) =>
      requestContext.complete(StatusCodes.OK, facts)
     // self ! PoisonPill
  }
}