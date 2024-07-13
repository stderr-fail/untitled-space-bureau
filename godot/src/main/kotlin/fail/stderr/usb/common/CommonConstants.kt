package fail.stderr.usb.common

import fail.stderr.usb.time.CustomTimeScale
import org.orekit.frames.Frame
import org.orekit.time.AbsoluteDate
import org.orekit.time.TimeScale
import org.orekit.time.TimeScalesFactory

class CommonConstants {
  companion object {
    val TIME_SCALE: TimeScale = CustomTimeScale("ALEXTIME", 0.0)
    val REF_INERTIAL_FRAME: Frame = CustomInertialFrame.create()
    val REF_DATE: AbsoluteDate = AbsoluteDate(1983, 7, 14, TimeScalesFactory.getTAI())
  }

}