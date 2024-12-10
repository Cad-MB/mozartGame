package upmc.akka.ppc

import akka.actor.{ActorRef, ActorSystem, Props}

object Concert extends App {
  import ConductorActor._
  import ProviderActor._

  println("Starting Mozart's game")

  val system = ActorSystem("MozartGame")

  val databaseActor: ActorRef = system.actorOf(Props[DataBaseActor], "DataBase")
  val playerActor: ActorRef = system.actorOf(Props[PlayerActor], "Player")
  val providerActor: ActorRef = system.actorOf(Props(new ProviderActor(databaseActor, null)), "Provider")
  val conductorActor: ActorRef = system.actorOf(Props(new ConductorActor(providerActor, playerActor)), "Conductor")

  providerActor ! UpdateConductor(conductorActor)

  conductorActor ! StartGame
}
