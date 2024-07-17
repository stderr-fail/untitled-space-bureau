package fail.stderr.usb.data.system

@JvmRecord
data class RootCelestialBodyData(
  val name: String,
  val mu: Double,
  val type: CelestialBodyType,
  val children: Array<CelestialBodyData>,
)
