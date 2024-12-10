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

  val random = new scala.util.Random
  val scheduler = context.system.scheduler
  val TIME_BASE = 1800.milliseconds

  def rollDice(): Int = {
    random.nextInt(6) + 1 + random.nextInt(6) + 1
  }

  def receive: Receive = {
    case StartGame =>
      val diceResult = rollDice()
      println(s"Conductor rolled: $diceResult")
      provider ! GetMeasure(diceResult - 2)

    case Measure(chords) =>
      println("Conductor received a measure, sending it to the PlayerActor.")
      player ! Measure(chords)
      scheduler.scheduleOnce(TIME_BASE, self, StartGame)
      println("ConductorActor scheduled the next game.")
  }
}
