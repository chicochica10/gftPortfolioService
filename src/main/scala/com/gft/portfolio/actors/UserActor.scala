package com.gft.portfolio.actors

import scala.concurrent.duration._
import scala.util.{ Success => ScalaSuccess }

import akka.actor._
import akka.actor.{ Props, ActorLogging, Actor }
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.ExecutionContext.Implicits.global

import com.gft._

class UserActor(remoteDictionary: ActorRef) extends Actor with ActorLogging {

  implicit val timeout = Timeout(10 seconds)
  import com.gft.portfolio.actors.PortfolioUserActor._
  import com.gft.portfolio.actors.UserDataActor._

  def getNumOfFacts(message: UserCompleteDataMessage) = {
    var res: Int = 0
    for (group <- message.groups) {
      if (group.active) {
        if (group.transferCodes.length > 0) {
          res += group.transferCodes.length
        } else {
          //If the group has not transfer codes use the default codes
          res += message.transferCodes.length
        }
      }
    }
    res
  }
  /*
  The list variable is initially set to an empty, or Nil list of BigInt numbers.
  This is where factorials results will be stored as they are ready.
   */
  var list: List[NumCodeFact] = Nil
  var nrOfFacts: Int = _
  var nrOfResults: Int = 0
  var capturedSender: ActorRef = _
  
  def receive = {

    case UserBasicDataMessage(id, firstName, lastName, email) => {
      // Assign the sender (ProtfolioUserActor instance) to a scope val
     // val _portfolioActor = sender;
      capturedSender = sender
      log.debug(s"-useractor- HELLO BASIC USER (ACTOR#2) ${id}")

      val future = (context.actorOf(Props[UserDataActor]) ? UserBasicDataMessage(id, firstName, lastName, email)).mapTo[UserPartialDataMessage]
      future.onComplete {
        case ScalaSuccess(result: UserPartialDataMessage) => {
          log.debug(s"(ACTOR#2) RECEIVED UserPartialDataMessage:  ${result.id}")
          val future =
            (remoteDictionary ?
              UserPartialDataMessage(result.id, result.firstName, result.lastName, result.email, result.address, result.clientType, result.job, result.transferCodes, result.groups))
              .mapTo[UserCompleteDataMessage]
          future.onComplete {
            case ScalaSuccess(result: UserCompleteDataMessage) => {
              log.debug(s"(ACTOR#2) RECEIVED UserCompleteDataMessage:  ${result.userId}")
              log.debug(s"DEBUG-1---LIST OF GROUPS: ${result.groups.length}")
              nrOfFacts = getNumOfFacts(result)
              log.debug (s"------------_> numoffacts: ${nrOfFacts}")
              for (group <- result.groups) {
                log.debug(s"DEBUG-2---IN LOOP")
                if (group.active) {
                  log.debug(s"DEBUG-3---ACTIVE GROUP: ${group.active}")
                  log.debug(s"DEBUG-4---LIST OF TRASNFERCODES: ${group.transferCodes.length}")
                  if (group.transferCodes.length > 0) {
                    for (transferCode <- group.transferCodes) {
                      log.debug(s"DEBUG-5---TRANSFER CODE TO BE CALCULATED: ${transferCode}")
                      context.actorOf(Props[FactorialCalculatorActor]) ! UserFact (result.userId, transferCode, 0)
                    }
                  } else {
                    //If the group has not transfer codes use the default codes
                    for (transferCode <- result.transferCodes) {
                      context.actorOf(Props[FactorialCalculatorActor]) ! UserFact (result.userId, transferCode, 0)
                    }
                  }
                }
              }
              /*
                    Returns complete data to the portfolio user actor
                     */
           //   log.debug(s"(ACTOR#2) IT'S gOING TO SEND UserCompleteDataMessage:  ${result.userId}")

            //  context.actorOf(Props(classOf[PortfolioUserActor], remoteDictionary)) ! UserCompleteDataMessage(result.userId, result.firstName, result.lastName, result.email, result.address, result.clientType, result.job, result.transferCodes, result.groups)
            }

          }
        }
      }

    }
    case UserFact (userId, transferCode, fac) => {
      
 
      list = NumCodeFact (transferCode, fac) :: list
      nrOfResults += 1
      log.info(s"---> nrOfResults: ${nrOfResults} / nrUsers(fijo): ${nrOfFacts} (ACTOR#2) RECEIVED factorial for ${userId} - ${transferCode}: ${fac}")
      if (nrOfFacts == nrOfResults){
        log.info (s" ---> RESUELTOS LOS ${nrOfFacts} transfersCode para el user: ${userId} ")
       //context.actorOf(Props(classOf[PortfolioUserActor], remoteDictionary)) ! Fact (userId, list)
       capturedSender ! Fact (userId,list)
      }
    }
  }
}


