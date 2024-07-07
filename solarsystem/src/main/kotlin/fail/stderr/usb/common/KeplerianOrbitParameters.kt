package fail.stderr.usb.common

data class KeplerianOrbitParameters(

  /** semi-major axis in **meters** */
  val semiMajorAxis: Double,

  /** eccentricity in **dimensionless** */
  val eccentricity: Double,

  /** inclination in **radians** */
  val inclination: Double,

  /**
   * * ω
   * * perigee argument
   * * argument of periapsis
   *
   * in **radians**
   */
  val pa: Double,

  /**
   * * Ω
   * * right ascension of ascending node
   * * raan
   * * longitude of the ascending node
   * * lan
   *
   * in **radians**
   */
  val raan: Double,

  /**
   * * mean
   * * mean anomaly at epoch
   * * eccentric or true anomaly
   *
   * in **radians**
   */
  val anomaly: Double,

)
