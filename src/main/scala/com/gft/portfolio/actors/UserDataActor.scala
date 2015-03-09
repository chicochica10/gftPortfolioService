package com.gft.portfolio.actors

import akka.actor.{ ActorLogging, Actor }

import com.gft._

class UserDataActor extends Actor with ActorLogging {
  import com.gft.portfolio.actors.PortfolioUserActor._
  import com.gft.portfolio.actors.UserDataActor._
  
  def receive = {
    case UserBasicDataMessage(id, firstName, lastName, email) => {
      log.debug(s"(ACTOR#3) HELLO USER PARTIAL DATA RETRIEVER:  ${id}")

      /*
        TEST PURPOSES ONLY!!!!!!!!!!!!!
          Read json stub
          create UserPartialDataMessage
          return to sender
       */
      val address: String = "Calle Estrecha sn"
      val clientType: String = "VIP"
      val job: String = "Private Investigator"
      val transferCodes = List[Int](2, 3, 4)
      val groups = List[String]("Investor", "Conservative", "PrevilegedConsultor")

      log.debug(s"(ACTOR#3) IT'S GOING TO SEND USER PARTIAL DATA:  ${id}")
      sender ! new UserPartialDataMessage(id, firstName, lastName, email, address, clientType, job, transferCodes, groups)
    }
  }
}

object UserDataActor {
  import spray.json._
  import DefaultJsonProtocol._
  /**
   * Partial list of data for a user
   *
   * @param id the identity
   * @param firstName the first name
   * @param lastName the last name
   * @param email the email address
   * @param address current postal address
   * @param clientType type of bank client
   * @param job the current job of the user
   * @param transferCodes default transfer codes to be calculated
   * @param groups the list with names of the assigned groups to this user
   */
  case class UserPartialDataMessage(
    id: String,
    firstName: String,
    lastName: String,
    email: String,
    address: String,
    clientType: String,
    job: String,
    transferCodes: List[Int],
    groups: List[String])
   
   //JSON  
  object UserPartialDataMessage extends DefaultJsonProtocol {
	  implicit val format = jsonFormat9 (UserPartialDataMessage.apply)
  }

}