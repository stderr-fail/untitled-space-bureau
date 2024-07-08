package fail.stderr.usb.data.system

@JvmRecord
data class KeplerianOrbitalParametersData(

  /** semi-major axis in **meters** */
  @JvmField
  val semiMajorAxis: Double,

  /** eccentricity in **dimensionless** */
  @JvmField
  val eccentricity: Double,

  /** inclination in **radians** */
  @JvmField
  val inclination: Double,

  /**
   *  * ω
   *  * perigee argument
   *  * argument of periapsis
   *
   * in **radians**
   */
  @JvmField
  val perigreeArgument: Double,

  /**
   *  * Ω
   *  * right ascension of ascending node
   *  * raan
   *  * longitude of the ascending node
   *  * lan
   *
   * in **radians**
   */
  @JvmField
  val raan: Double,

  /**
   *  * mean
   *  * mean anomaly at epoch
   *  * eccentric or true anomaly
   *
   * in **radians**
   */
  @JvmField
  val anomaly: Double
)
