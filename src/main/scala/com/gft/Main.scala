package com.gft

import akka.actor._
import akka.io.IO
 
import spray.can.Http
import spray.can.Http.Bind

import spray.can.server._
import com.typesafe.config.ConfigFactory

object Main extends App {
  
  val config = ConfigFactory.load ("frontend")
  
  val host = config.getString ("http.host")
  val port = config.getInt ("http.port")
  
  val system = ActorSystem ("portfolioActorSystem", config)
  
  val restInterface = system.actorOf(Props[RestInterface], "restInterface")
  Http (system).manager ! Bind(listener = restInterface, interface = host, port = port)
  
}

object DictionaryServiceMain extends App {

  val config = ConfigFactory.load("backend")
  val system = ActorSystem("dictionaryActorSystem", config)

  system.actorOf(Props[Dispatcher], "dispatcher")

}
