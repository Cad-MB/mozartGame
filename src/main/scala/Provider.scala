package upmc.akka.ppc

import akka.actor.{Actor, ActorRef}

object ProviderActor {
  case class GetMeasure(num: Int)
}

class ProviderActor(database: ActorRef, conductor: ActorRef) extends Actor {
  import DataBaseActor._
  import ProviderActor._

  var compteur = 0
  val MAX_COLUMNS = 8

  def receive: Receive = {
    case ProviderActor.GetMeasure(num) =>
      val validNum = math.max(0, math.min(num, partie1.length - 1))

      val column =
        if (compteur < MAX_COLUMNS) partie1(validNum)(compteur)
        else partie2(validNum)(compteur % MAX_COLUMNS)

      database ! DataBaseActor.GetMeasure(column)

      compteur = (compteur + 1) % (2 * MAX_COLUMNS)

    case Measure(chords) =>
      println(s"ProviderActor received a measure, sending to ConductorActor...")
      conductor ! Measure(chords)
  }

  val partie1: Array[Array[Int]] = Array(
    Array(96, 22, 141, 41, 105, 122, 11, 30),
    Array(32, 6, 128, 63, 146, 46, 134, 81),
    Array(90, 45, 120, 142, 36, 147, 121, 80),
    Array(148, 74, 140, 136, 158, 159, 101, 114),
    Array(170, 55, 106, 144, 153, 139, 161, 190),
    Array(152, 77, 137, 154, 68, 118, 91, 87),
    Array(133, 162, 159, 152, 163, 164, 156, 178),
    Array(98, 140, 42, 106, 156, 122, 143, 89),
    Array(54, 130, 15, 33, 36, 37, 165, 33)
  )

  val partie2: Array[Array[Int]] = Array(
    Array(70, 121, 26, 19, 112, 49, 109, 14),
    Array(139, 126, 56, 13, 146, 135, 79, 83),
    Array(66, 139, 15, 132, 73, 134, 154, 79),
    Array(96, 176, 3, 64, 67, 166, 52, 170),
    Array(25, 143, 64, 225, 42, 50, 62, 101),
    Array(135, 155, 57, 175, 43, 163, 132, 151),
    Array(70, 77, 89, 182, 127, 138, 149, 81),
    Array(33, 121, 164, 144, 54, 139, 140, 63),
    Array(135, 20, 108, 92, 12, 124, 44, 131)
  )
}
