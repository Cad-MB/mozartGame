package upmc.akka.ppc

import akka.actor.{Actor, ActorRef}

import javax.sound.midi.ShortMessage._
import javax.sound.midi._
import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object PlayerActor {
  case class MidiNote(pitch: Int, vel: Int, dur: Int, at: Int)

  val info = MidiSystem.getMidiDeviceInfo().find(_.getName == "Gervill")
  val device = info.map(MidiSystem.getMidiDevice).getOrElse {
    throw new IllegalStateException("[ERROR] Could not find Gervill synthesizer.")
  }

  val rcvr = device.getReceiver()

  def note_on(pitch: Int, vel: Int, chan: Int): Unit = {
    val msg = new ShortMessage
    msg.setMessage(NOTE_ON, chan, pitch, vel)
    rcvr.send(msg, -1)
  }

  def note_off(pitch: Int, chan: Int): Unit = {
    val msg = new ShortMessage
    msg.setMessage(NOTE_ON, chan, pitch, 0)
    rcvr.send(msg, -1)
  }
}

class PlayerActor() extends Actor {
  import DataBaseActor._
  import PlayerActor._

  device.open()

  def receive: Receive = {
    case Measure(chords) =>
      println("PlayerActor received a measure, preparing to play...")
      chords.foreach { chord =>
        chord.notes.foreach { note =>
          println(s"Scheduling note: pitch=${note.pitch}, vel=${note.vol}, dur=${note.dur}, at=${chord.date}")
          self ! MidiNote(note.pitch, note.vol, note.dur, chord.date)
        }
      }

    case MidiNote(pitch, vel, dur, at) =>
      context.system.scheduler.scheduleOnce(at.milliseconds) {
        note_on(pitch, vel, 10)
      }
      context.system.scheduler.scheduleOnce((at + dur).milliseconds) {
        note_off(pitch, 10)
      }
  }
}
