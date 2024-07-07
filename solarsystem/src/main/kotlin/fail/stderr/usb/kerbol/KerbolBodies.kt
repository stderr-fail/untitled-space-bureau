package fail.stderr.usb.kerbol

import fail.stderr.usb.common.*
import org.orekit.frames.Frame
import org.orekit.orbits.KeplerianOrbit

object KerbolBodyConstants {

  val KERBOL_MU = 1.1723328e18 // Gravitational parameter for Kerbol (m^3/s^2)
  val KERBIN_MU = 3.5316e12 // Gravitational parameter for Kerbin (m^3/s^2)
  val MUN_MU = 6.5138398e10 // Gravitational parameter for Mun (m^3/s^2)

}

fun createKerbolFrame(): Frame = createRootFrame("Kerbol")

fun createKerbinOrbit(parentFrame: Frame) = createKeplerianOrbit(
  params = KeplerianOrbitParameters(
    semiMajorAxis = 13599840256.0,
    eccentricity = 0.0,
    inclination = 0.0,
    pa = 0.0,
    raan = 0.0,
    anomaly = 3.14,
  ),
  parentFrame = parentFrame,
  refDate = KerbolSystem.REF_DATE,
  bodyMU = KerbolBodyConstants.KERBOL_MU,
)

fun createKerbinFrame(parentFrame: Frame, bodyOrbit: KeplerianOrbit): Frame
  = createBodyFrame("Kerbin", bodyOrbit, parentFrame)

fun createMunOrbit(parentFrame: Frame) = createKeplerianOrbit(
  params = KeplerianOrbitParameters(
    semiMajorAxis = 12000000.0,
    eccentricity = 0.0,
    inclination = 0.0,
    pa = 0.0,
    raan = 0.0,
    anomaly = 1.7,
  ),
  parentFrame = parentFrame,
  refDate = KerbolSystem.REF_DATE,
  bodyMU = KerbolBodyConstants.KERBIN_MU,
)

fun createMunFrame(parentFrame: Frame, bodyOrbit: KeplerianOrbit): Frame
    = createBodyFrame("Mun", bodyOrbit, parentFrame)
