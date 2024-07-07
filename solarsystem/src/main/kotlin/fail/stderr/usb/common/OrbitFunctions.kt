package fail.stderr.usb.common

import org.orekit.frames.Frame
import org.orekit.orbits.KeplerianOrbit
import org.orekit.orbits.PositionAngleType
import org.orekit.time.AbsoluteDate

fun createKeplerianOrbit(
  params: KeplerianOrbitParameters,
  parentFrame: Frame,
  refDate: AbsoluteDate,
  bodyMU: Double
): KeplerianOrbit =
  KeplerianOrbit(
    params.semiMajorAxis,
    params.eccentricity,
    params.inclination,
    params.pa,
    params.raan,
    params.anomaly,
    PositionAngleType.MEAN,
    parentFrame,
    refDate,
    bodyMU,
  )
