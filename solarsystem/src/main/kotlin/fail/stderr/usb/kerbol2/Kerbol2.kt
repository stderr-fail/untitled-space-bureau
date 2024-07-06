package fail.stderr.usb.kerbol2

import fail.stderr.usb.common.CustomCelestialBody
import org.hipparchus.CalculusFieldElement
import org.hipparchus.geometry.euclidean.threed.FieldVector3D
import org.orekit.frames.FieldTransform
import org.orekit.frames.Frame
import org.orekit.frames.Transform
import org.orekit.frames.TransformProvider
import org.orekit.orbits.KeplerianOrbit
import org.orekit.orbits.PositionAngleType
import org.orekit.time.AbsoluteDate
import org.orekit.time.FieldAbsoluteDate
import org.orekit.time.TimeScale
import org.orekit.time.createConstantOffsetTimeScale
import org.orekit.utils.FieldPVCoordinates
import java.util.concurrent.TimeUnit

class Kerbol2 {

  companion object {
    val KERBOL_MU = 1.1723328e18 // Gravitational parameter for Kerbol (m^3/s^2)
    val KERBIN_MU = 3.5316e12 // Gravitational parameter for Kerbin (m^3/s^2)
    val MUN_MU = 6.5138398e10 // Gravitational parameter for Mun (m^3/s^2)

    val TIME_SCALE: TimeScale = createConstantOffsetTimeScale("ALEXTIME", 0.0)

    val CUSTOM_REF_DATE = AbsoluteDate(1983, 7, 14, TIME_SCALE)


  }


  fun createRootFrame(): Frame {
    val c = Frame::class.java.getDeclaredConstructor(String::class.java, Boolean::class.java)
    c.isAccessible = true
    val kerbolFrame = c.newInstance("Kerbol", true)
    return kerbolFrame
  }

  fun buildKerbinOrbit(parentFrame: Frame): KeplerianOrbit {
    // Parameters from Kerbin's Wiki
    val a = 13599840256.0 // semi-major axis [m]
    val e = 0.0 // eccentricity
    val i = 0.0 // inclination [rad]
    val pa = 0.0 // argument of periapsis [rad]
    val raan = 0.0 // right ascension of ascending node [rad]
    val anomaly = 3.14 // mean anomaly at epoch [rad]

    return KeplerianOrbit(
      a,
      e,
      i,
      pa,
      raan,
      anomaly,
      PositionAngleType.MEAN,
      parentFrame,
      CUSTOM_REF_DATE,
      KERBOL_MU,
    )
  }

  fun createKerbinTransformationProvider(bodyOrbit: KeplerianOrbit, parentFrame: Frame): TransformProvider {

    val transformProvider: TransformProvider = object : TransformProvider {

      override fun getTransform(date: AbsoluteDate): Transform {
        val bodyPV = bodyOrbit.getPVCoordinates(date, parentFrame)
        return Transform(date, bodyPV)
      }

      override fun <T : CalculusFieldElement<T>> getTransform(date: FieldAbsoluteDate<T>): FieldTransform<T> {
        val bodyPV = bodyOrbit.getPVCoordinates(date.toAbsoluteDate(), parentFrame)
        val ft = FieldTransform(
          date, FieldPVCoordinates(
            FieldVector3D(date.field, bodyPV.position),
            FieldVector3D(date.field, bodyPV.velocity)
          )
        )
        return ft
      }

    }

    return transformProvider

  }


  fun createKerbinFrame(parentFrame: Frame, kerbinOrbit: KeplerianOrbit): Frame {
    val transformProvider = createKerbinTransformationProvider(kerbinOrbit, parentFrame)
    return Frame(parentFrame, transformProvider, "KerbinCenteredFrame", true)
  }

  fun buildMunOrbit(parentFrame: Frame): KeplerianOrbit {
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

    return KeplerianOrbit(
      a,
      e,
      i,
      pa,
      raan,
      anomaly,
      PositionAngleType.MEAN,
      parentFrame,
      CUSTOM_REF_DATE,
      KERBIN_MU,
    )
  }

  fun createMunTransformationProvider(bodyOrbit: KeplerianOrbit, parentFrame: Frame): TransformProvider {

    val transformProvider: TransformProvider = object : TransformProvider {

      override fun getTransform(date: AbsoluteDate): Transform {
        val bodyPV = bodyOrbit.getPVCoordinates(date, parentFrame)
        return Transform(date, bodyPV)
      }

      override fun <T : CalculusFieldElement<T>> getTransform(date: FieldAbsoluteDate<T>): FieldTransform<T> {
        val bodyPV = bodyOrbit.getPVCoordinates(date.toAbsoluteDate(), parentFrame)
        val ft = FieldTransform(
          date, FieldPVCoordinates(
            FieldVector3D(date.field, bodyPV.position),
            FieldVector3D(date.field, bodyPV.velocity)
          )
        )
        return ft
      }

    }

    return transformProvider

  }


  fun createMunFrame(parentFrame: Frame, bodyOrbit: KeplerianOrbit): Frame {
    val transformProvider = createMunTransformationProvider(bodyOrbit, parentFrame)
    return Frame(parentFrame, transformProvider, "MunCenteredFrame", true)
  }

}

fun main() {

  val k2 = Kerbol2()

  val kerbolFrame = k2.createRootFrame()

  // Create the orbit for Kerbin
  val kerbinOrbit = k2.buildKerbinOrbit(kerbolFrame)
  val kerbinFrame = k2.createKerbinFrame(kerbolFrame, kerbinOrbit)
  val kerbin = CustomCelestialBody("Kerbin", Kerbol2.KERBIN_MU, kerbinFrame, kerbinOrbit)

  // Create the orbit for Mun
  val munOrbit = k2.buildMunOrbit(kerbinFrame)
  val munFrame = k2.createMunFrame(kerbinFrame, munOrbit)
  val mun = CustomCelestialBody("Mun", Kerbol2.MUN_MU, munFrame, munOrbit)


  val testDates = arrayOf(
    Kerbol2.CUSTOM_REF_DATE.shiftedBy(0.0),
    Kerbol2.CUSTOM_REF_DATE.shiftedBy(1L, TimeUnit.DAYS),
    Kerbol2.CUSTOM_REF_DATE.shiftedBy(7L, TimeUnit.DAYS),
    Kerbol2.CUSTOM_REF_DATE.shiftedBy(28L, TimeUnit.DAYS),
    Kerbol2.CUSTOM_REF_DATE.shiftedBy(365L, TimeUnit.DAYS)
  )

  testDates.forEach { date ->

    val kerbinPV = kerbin.getPVCoordinates(date, kerbolFrame)
    println("Date: $date - Position (kerbol): vec=${kerbinPV.position} d=${kerbinPV.position.norm} m")

    val munPV = mun.getPVCoordinates(date, kerbinFrame)
    println("Date: $date - Position (mun): vec=${munPV.position} d=${munPV.position.norm} m")

  }


}

