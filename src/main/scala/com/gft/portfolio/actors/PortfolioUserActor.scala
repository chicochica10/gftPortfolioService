package com.gft.portfolio.actors

import java.lang.ArithmeticException

import scala.concurrent.duration._
import scala.util.{ Success => ScalaSuccess }

import akka.actor._
import akka.actor.{ Props, ActorLogging, Actor }
import akka.actor.OneForOneStrategy
import akka.actor.SupervisorStrategy._
import akka.actor.ActorKilledException
import akka.actor.OneForOneStrategy
import akka.pattern.ask
import akka.util.Timeout

import com.gft._
import com.gft.portfolio.actors._

class PortfolioUserActor(remoteDictionary: ActorRef) extends Actor with ActorLogging {
  import PortfolioUserActor._

  implicit val timeout = Timeout(10 seconds)

  var nrUsers: Int = 0
  var nrOfResults: Int = 0
  var capturedSender: ActorRef = _
  var facts: List[Fact] = Nil

  def receive = {

    case UserSetDataMessage(userList) =>
      {
        capturedSender = sender
        nrUsers = userList.length
        log.info(s"---> TAMAÑO DE LA LISTA DE USUARIOS: " + nrUsers)
        userList.foreach { user =>

          context.actorOf(Props(classOf[UserActor], remoteDictionary)) ! user

        }
      }

    case Fact(userId, fact /*: List[NumCodeFact]*/ ) =>
      {
        nrOfResults += 1
        log.info(s"---> nrOfResults: ${nrOfResults} / nrUsers(fijo): ${nrUsers}  RECIBIDA LA LISTA DE FACTORIALES DEL USUARIO: ${userId} ===> ${fact}")

        facts = Fact(userId, fact) :: facts

        if (nrOfResults == nrUsers) {
          log.info(s"---> LOS FACTORIALES DE TODOS LOS USUARIOS (${nrUsers}) YA ESTÁN COMPLETOS: ${facts}")
          capturedSender ! Facts(facts)
          self ! PoisonPill
        }
      }

    //    case UserCompleteDataMessage(userId, firstName, lastName, email, address, clientType, job, transferCodes, groups) => {
    //      //TODO: Complete user data handling
    //      log.debug(s" (ACTOR#1)******** COMPLETE CYCLE FOR USER****************: ${userId}")
    //      //sender ! Facts(facts.toList)
    //    }

  }

  /*
  SUPERVISION STRATEGY
  We don’t have to specify our own supervisor strategy in each and every actor.
  This means that the default supervisor strategy will take effect in every actor from this one.
  http://doc.akka.io/docs/akka/snapshot/general/supervision.html
   */
  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
      case _: ActorInitializationException => Stop
      case _: ActorKilledException => Stop
      case _: ArithmeticException => Resume
      case _: NullPointerException => Restart
      case _: IllegalArgumentException => Stop
      case _: Exception => Escalate
    }

  override def postRestart(reason: Throwable) {
    super.postRestart(reason)
    log.info(s"In master restart because of ${reason}")
  }

  override def preRestart(cause: Throwable, message: Option[Any]) {
    // do not kill all children, which is the default here
    log.info(s"In master restart: ${message}")
  }
}

object PortfolioUserActor {
  import spray.json._
  import DefaultJsonProtocol._

  /**
   * A "typical" user value containing its identity, name and email.
   *
   * @param id the identity
   * @param firstName the first name
   * @param lastName the last name
   * @param email the email address
   */
  case class UserBasicDataMessage(
    id: String,
    firstName: String,
    lastName: String,
    email: String)

  /**
   * A "typical" user list
   *
   * @param userSet the list of users
   */
  case class UserSetDataMessage(
    userSet: List[UserBasicDataMessage])

  case class UserCompleteDataMessage(userId: String,
    firstName: String,
    lastName: String,
    email: String,
    address: String,

    clientType: String,
    job: String,
    transferCodes: List[Int],
    groups: List[Group])

  case class Group(
    name: String,
    entities: List[String],
    transferCodes: List[Int],
    active: Boolean)

  case class UserFact(userId: String, num: Int, fac: BigInt)

  case class NumCodeFact(num: Int, fac: BigInt)

  case class Fact(userId: String, fact: List[NumCodeFact])

  case class Facts(facts: List[Fact])

  //JSON  
  object NumCodeFact extends DefaultJsonProtocol {
    implicit val format = jsonFormat2(NumCodeFact.apply)
  }

  object UserBasicDataMessage extends DefaultJsonProtocol {
    implicit val format = jsonFormat4(UserBasicDataMessage.apply)
  }
  object UserSetDataMessage extends DefaultJsonProtocol {

    implicit val format = jsonFormat1(UserSetDataMessage.apply)
  }
  object Group extends DefaultJsonProtocol {
    implicit val format = jsonFormat4(Group.apply)
  }

  object Fact extends DefaultJsonProtocol {
    implicit val format = jsonFormat2(Fact.apply)
  }

  object Facts extends DefaultJsonProtocol {
    implicit val format = jsonFormat1(Facts.apply)
  }

  //  
  //   object UserCompleteDataMessage extends DefaultJsonProtocol {
  //	 
  //	  implicit def jsonFormat[A :JsonFormat] = jsonFormat9(UserCompleteDataMessage.apply[A])
  //  }
  //  
}