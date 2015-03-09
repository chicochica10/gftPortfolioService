package com.gft

import akka.actor._
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._
import akka.actor.ActorSelection.toScala

object RemoteDictionaryCreator {
  val config = ConfigFactory.load("frontend").getConfig("backend")
  //"akka.tcp://backend@0.0.0.0:2551/user/simple"
  val protocol = config.getString("protocol")
  val systemName = config.getString("system")
  val host = config.getString("host")
  val port = config.getInt("port")
  val actorName = config.getString("actor")
}

trait RemoteDictionaryCreator   { actor: Actor => 
  import RemoteDictionaryCreator._
  def createPath: String = {

    s"$protocol://$systemName@$host:$port/$actorName"
  }

  def createRemoteDictionary = {
    
    val path = createPath
    println ("-----------------> path creado " + path)
    context.actorOf(Props(classOf[ RemoteLookup], path), "lookupDictionaryService")
  }
}


class RemoteLookup(path: String) extends Actor with ActorLogging {
  context.setReceiveTimeout(3 seconds)

  sendIdentifyRequest

  def sendIdentifyRequest(): Unit = {
    //como en el REPL
    log.info (s"---> accediendo al path remototo ${path}" )
    val selection = context.actorSelection(path)
    selection ! Identify(path) //
  }

  def receive = identify

  def identify: Receive = {
    /*
     * In Scala an identifier may also be formed by an arbitrary string between back-quotes (`). 
     * The identifier then is composed of all characters excluding the back-quotes themselves.
     */
    case ActorIdentity(`path`, Some(actor)) =>
      context.setReceiveTimeout(Duration.Undefined)
      log.info("switching to active state")
      context.become(active(actor))
      context.watch(actor)

    case ActorIdentity(`path`, None) =>
      log.error(s"Remote actor with path $path is not available.")

    case ReceiveTimeout =>
      sendIdentifyRequest()

    case msg: Any =>
      log.error(s"Ignoring message $msg, remote actor is not ready yet.")
     // sendIdentifyRequest()
  }

  def active (actor: ActorRef): Receive = {
    case Terminated(actorRef) =>
      log.info("Actor $actorRef terminated.")
      context.become(identify)
      log.info("switching to identify state")
      context.setReceiveTimeout(3 seconds)
      sendIdentifyRequest()
     
    case msg:Any => actor forward msg
  }
}
