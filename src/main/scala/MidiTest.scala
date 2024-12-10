import javax.sound.midi._

object MidiTest extends App {
  MidiSystem.getMidiDeviceInfo.foreach(info => println(s"Device: ${info.getName}"))
}