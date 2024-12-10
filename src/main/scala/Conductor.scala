package upmc.akka.ppc

import akka.actor.{Actor, ActorRef}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object ConductorActor {
  case object StartGame
}

class ConductorActor(provider: ActorRef, player: ActorRef) extends Actor {
  import ConductorActor._
  import DataBaseActor._

  // gen random pour simuler le lancer de dés
  val random = new scala.util.Random
  // scheduler pour planifier l'exécution future des msgs
  val scheduler = context.system.scheduler
  // durée entre chaque game
  val TIME_BASE = 1800.milliseconds

  def rollDice(): Int = {
    random.nextInt(6) + 1 + random.nextInt(6) + 1
  }

  // react aux msgs reçus
  def receive: Receive = {
    case StartGame =>
      val diceResult = rollDice()
      println(s"Conductor rolled: $diceResult")
      // envoyer un msg au Provider avec la sum des dés
      provider ! GetMeasure(diceResult - 2)

    case Measure(chords) =>
      println("Conductor received a measure, sending it to the PlayerActor.")
      // transmettre la mesure au PlayerActor pour la jouer
      player ! Measure(chords)
      // planifier le prochain cycle du jeu après 1800ms
      scheduler.scheduleOnce(TIME_BASE, self, StartGame)
      println("ConductorActor scheduled the next game.")
  }
}
