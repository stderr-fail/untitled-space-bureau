package fail.stderr.usb.common

import fail.stderr.usb.data.system.KeplerianOrbitalParametersData
import org.orekit.frames.Frame
import org.orekit.orbits.KeplerianOrbit
import org.orekit.orbits.PositionAngleType
import org.orekit.time.AbsoluteDate

class OrbitFunctions {
  companion object {
    fun createKeplerianOrbit(
      params: KeplerianOrbitalParametersData,
      parentFrame: Frame?,
      refDate: AbsoluteDate?,
      parentMU: Double
    ): KeplerianOrbit {
      return KeplerianOrbit(
        params.semiMajorAxis,
        params.eccentricity,
        params.inclination,
        params.perigreeArgument,
        params.raan,
        params.anomaly,
        PositionAngleType.MEAN,
        parentFrame,
        refDate,
        parentMU
      )
    }

  }
}