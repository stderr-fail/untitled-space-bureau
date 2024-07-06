package fail.stderr.usb.kerbol

import fail.stderr.usb.common.CommonConstants
import fail.stderr.usb.common.CustomCelestialBody
import org.hipparchus.CalculusFieldElement
import org.orekit.bodies.CelestialBody
import org.orekit.frames.FieldTransform
import org.orekit.frames.Frame
import org.orekit.frames.Transform
import org.orekit.frames.TransformProvider
import org.orekit.orbits.KeplerianOrbit
import org.orekit.orbits.PositionAngleType
import org.orekit.time.AbsoluteDate
import org.orekit.time.FieldAbsoluteDate
import org.orekit.utils.FieldPVCoordinates
import org.orekit.utils.PVCoordinates


fun buildKerbinOrbit(): KeplerianOrbit {
  // https://wiki.kerbalspaceprogram.com/wiki/Kerbin

  // semi-major axis [m]
  val a = 13599840256.0
  // eccentricity
  val e = 0.0
  // inclination [rad]
  val i = 0.0
  // perigee argument / ω / argument of periapsis [rad]
  val pa = 0.0
  // right ascension of ascending node / Ω / raan [rad]
  // aka Longitude of the Ascending Node / lan [rad]
  val raan = 0.0
  // anomaly - mean, eccentric or true anomaly [rad]
  val anomaly = 3.14 // ksp wiki says this

  return KeplerianOrbit(
    a,
    e,
    i,
    pa,
    raan,
    anomaly,
    PositionAngleType.MEAN,
    CommonConstants.REF_INERTIAL_FRAME,
    CommonConstants.REF_DATE,
    KerbolConstants.KERBOL_MU,
  )
}


fun buildMunOrbit(): KeplerianOrbit {
  // https://wiki.kerbalspaceprogram.com/wiki/Mun

  // semi-major axis [m]
  val a = 12000000.0
  // eccentricity
  val e = 0.0
  // inclination [rad]
  val i = 0.0
  // perigee argument / ω / argument of periapsis [rad]
  val pa = 0.0
  // right ascension of ascending node / Ω / raan [rad]
  // aka Longitude of the Ascending Node / lan [rad]
  val raan = 0.0
  // anomaly - mean, eccentric or true anomaly [rad]
  val anomaly = 1.7 // ksp wiki says this

  val kerbinFrame = createKerbinCenteredFrame()

  return KeplerianOrbit(
    a,
    e,
    i,
    pa,
    raan,
    anomaly,
    PositionAngleType.MEAN,
//    kerbinFrame,
    CommonConstants.REF_INERTIAL_FRAME,
    CommonConstants.REF_DATE,
    KerbolConstants.KERBIN_MU,
  )
}

fun createKerbolCelestialBody(): CelestialBody {
  return CustomCelestialBody(
    _name = "Kerbol",
    mu = KerbolConstants.KERBOL_MU,
    CommonConstants.REF_INERTIAL_FRAME,
    orbit = null,
  )
}

fun createKerbinCelestialBody(): CelestialBody {
  return CustomCelestialBody(
    _name = "Kerbin",
    mu = KerbolConstants.KERBIN_MU,
    CommonConstants.REF_INERTIAL_FRAME,
    orbit = buildKerbinOrbit(),
  )
}

fun createKerbinCenteredFrame(): Frame {

  // Build Kerbin's orbit to use it for the frame transformation
  val kerbinOrbit = buildKerbinOrbit()

  val transformProvider = object : TransformProvider {

    override fun getTransform(date: AbsoluteDate): Transform {
      val kerbinPV = kerbinOrbit.getPVCoordinates(date, kerbinOrbit.frame)
      return Transform(date, kerbinPV)

    }

    override fun <T : CalculusFieldElement<T>> getTransform(date: FieldAbsoluteDate<T>): FieldTransform<T> {
      val kerbinPV = kerbinOrbit.getPVCoordinates(date.toAbsoluteDate(), kerbinOrbit.frame)
      val fieldPV =
        FieldPVCoordinates(date.field, PVCoordinates(kerbinPV.position, kerbinPV.velocity, kerbinPV.acceleration))
      return FieldTransform(date, fieldPV)
    }

  }

  return Frame(CommonConstants.REF_INERTIAL_FRAME, transformProvider, "KerbinCenteredFrame", true)
}