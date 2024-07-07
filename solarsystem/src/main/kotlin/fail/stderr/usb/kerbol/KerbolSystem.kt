package fail.stderr.usb.kerbol

import fail.stderr.usb.common.CustomCelestialBody
import org.orekit.time.AbsoluteDate
import org.orekit.time.TimeScale
import org.orekit.time.createConstantOffsetTimeScale

class KerbolSystem {

  companion object {

    val TIME_SCALE: TimeScale = createConstantOffsetTimeScale("KERBOLTIME", 0.0)

    val REF_DATE = AbsoluteDate(1983, 7, 14, TIME_SCALE)

  }

  val kerbolFrame = createKerbolFrame()

  val kerbinOrbit = createKerbinOrbit(kerbolFrame)
  val kerbinFrame = createKerbinFrame(kerbolFrame, kerbinOrbit)
  val kerbin = CustomCelestialBody("Kerbin", KerbolBodyConstants.KERBIN_MU, kerbinFrame, kerbinOrbit)

  // Create the orbit for Mun
  val munOrbit = createMunOrbit(kerbinFrame)
  val munFrame = createMunFrame(kerbinFrame, munOrbit)
  val mun = CustomCelestialBody("Mun", KerbolBodyConstants.MUN_MU, munFrame, munOrbit)

}