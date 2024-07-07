package fail.stderr.usb.data.system

@JvmRecord
data class RootCelestialBodyData(
  val name: String,
  val mu: Double,
  val children: Array<CelestialBodyData>
)
