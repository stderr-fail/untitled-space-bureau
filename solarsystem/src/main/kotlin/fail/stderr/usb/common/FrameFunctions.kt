package fail.stderr.usb.common

import org.orekit.frames.Frame
import org.orekit.orbits.KeplerianOrbit

fun createRootFrame(name: String): Frame {
  // the root frame constructor is private, access it using reflection
  val c = Frame::class.java.getDeclaredConstructor(String::class.java, Boolean::class.java)
  c.isAccessible = true
  val frame = c.newInstance(name, true)
  return frame
}

fun createBodyFrame(bodyName: String, bodyOrbit: KeplerianOrbit, parentFrame: Frame): Frame {
  val transformProvider = BodyToParentTransformProvider(bodyOrbit, parentFrame)
  return Frame(parentFrame, transformProvider, "${bodyName}CenteredFrame", true)
}