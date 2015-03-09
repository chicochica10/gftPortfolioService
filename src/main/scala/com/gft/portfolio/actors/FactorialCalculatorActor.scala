package com.gft.portfolio.actors

import akka.actor.{ActorLogging, Actor}
import scala.annotation.tailrec
import com.gft.portfolio.actors.PortfolioUserActor._


class FactorialCalculatorActor extends Actor with ActorLogging {
  def receive = {
    
    case  UserFact (userId, transferCode, _) =>
     {
      log.debug(s"DEBUG-6---TRANSFER CODE TO BE CALCULATED: ${transferCode}")

      val number: BigInt = factor(transferCode)
      log.debug(s"DEBUG-6---CALCULATED FACTORIAL: ${number}")
      sender ! UserFact (userId, transferCode, number)
      //sender ! (transferCode, factor(transferCode))
    }
  }

  private def factor(transferCode: Int) = factorTail(transferCode, 1)

  @tailrec
  private def factorTail(num: Int, acc: BigInt): BigInt = {
    (num, acc) match {
      case (0, a) => a
      case (n, a) => factorTail(n - 1, n * a)
    }
  }
}
