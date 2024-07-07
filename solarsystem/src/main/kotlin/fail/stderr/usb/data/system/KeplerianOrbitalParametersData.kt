package fail.stderr.usb.data.system

@JvmRecord
data class KeplerianOrbitalParametersData(
  val semiMajorAxis: Double,
  val eccentricity: Double,
  val inclination: Double,
  val perigreeArgument: Double,
  val raan: Double,
  val anomaly: Double
)
