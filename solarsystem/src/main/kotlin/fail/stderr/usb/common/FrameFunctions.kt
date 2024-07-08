package fail.stderr.usb.common

import org.orekit.frames.Frame
import org.orekit.orbits.KeplerianOrbit

class FrameFunctions {

  companion object {

    @JvmStatic
    fun createRootFrame(name: String?): Frame {
      val c = Frame::class.java.getDeclaredConstructor(
        String::class.java, Boolean::class.javaPrimitiveType
      )
      c.isAccessible = true
      val frame = c.newInstance(name, true)
      return frame
    }

    @JvmStatic
    fun createBodyFrame(bodyName: String?, bodyOrbit: KeplerianOrbit?, parentFrame: Frame?): Frame {
      val transformProvider = BodyToParentTransformProvider(bodyOrbit!!, parentFrame!!)
      return Frame(parentFrame, transformProvider, "${bodyName}CenteredFrame", true)
    }

  }

}
