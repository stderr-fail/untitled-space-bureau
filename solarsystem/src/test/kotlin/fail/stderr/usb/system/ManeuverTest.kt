package fail.stderr.usb.system

import org.hipparchus.geometry.euclidean.threed.Vector3D
import org.hipparchus.ode.events.Action
import org.junit.jupiter.api.Test
import org.orekit.attitudes.LofOffset
import org.orekit.bodies.BodyShape
import org.orekit.bodies.OneAxisEllipsoid
import org.orekit.forces.maneuvers.ImpulseManeuver
import org.orekit.frames.LOFType
import org.orekit.orbits.KeplerianOrbit
import org.orekit.orbits.PositionAngleType
import org.orekit.propagation.analytical.KeplerianPropagator
import org.orekit.propagation.events.AltitudeDetector
import org.orekit.propagation.events.DateDetector
import org.orekit.propagation.sampling.OrekitStepInterpolator
import org.orekit.utils.Constants
import java.util.concurrent.TimeUnit


class ManeuverTest {


  @Test
  fun testManeuver2() {

  }

  @Test
  fun testManeuver() {
    val system = buildSystem()

    val planetA = system.allCelestialBodies.get("Planet A")!!
    val planetB = system.allCelestialBodies.get("Planet B")!!

    val initialOrbit = KeplerianOrbit(
      Constants.WGS84_EARTH_EQUATORIAL_RADIUS + 200_000.0,
      0.0,
      0.0,
      0.0,
      0.0,
      0.0,
      PositionAngleType.MEAN,
      planetA.frame,
      system.refDate,
      planetA.mu,
    )

    // TODO: FIXME
    val planetABodyShape: BodyShape = OneAxisEllipsoid(
      Constants.WGS84_EARTH_EQUATORIAL_RADIUS,
      Constants.WGS84_EARTH_FLATTENING,
      planetA.frame
    )

//    6,378,137
    val altitudeDetector = AltitudeDetector(
      AltitudeDetector.DEFAULT_MAXCHECK * 2,
      AltitudeDetector.DEFAULT_THRESHOLD / 10,
      1_010_000.0,
      planetABodyShape,
    )
      .withMaxIter(AltitudeDetector.DEFAULT_MAX_ITER * 10)
      .withHandler { state, detector, b ->
//      109,803.03542399406
//      return@withHandler Action.STOP

        val norm = state.pvCoordinates.position.norm
        val altitude = norm - Constants.WGS84_EARTH_EQUATORIAL_RADIUS

        System.out.printf(
          "${state.date} :: !!!ALTITUDE!!! norm=%5.2f alt=%5.1f e=%5.3f %n",
          norm,
          altitude,
          state.orbit.e
        )

        return@withHandler Action.STOP
      }


    val maneuverStartDate = system.refDate.shiftedBy(1L, TimeUnit.DAYS)


    val trigger = DateDetector(maneuverStartDate)

    // escape deltaV is about 866.3 @ isp 700 (e=1.001)
    val deltaV = Vector3D(0.0, 0.0, 97.5)
    val isp = 700.0

    // create the maneuver, using ascending node detector as a trigger
    val maneuver = ImpulseManeuver(trigger, deltaV, isp);

    // the maneuver will be defined in spacecraft frame
    // we need to ensure the Z axis is aligned with orbital momentum
    // so we select an attitude aligned with LVLH Local Orbital frame
    val attitudeProvider = LofOffset(system.refFrame, LOFType.LVLH)
    val propagator = KeplerianPropagator(initialOrbit, attitudeProvider)

    propagator.addEventDetector(altitudeDetector)
    propagator.addEventDetector(maneuver)

    // progress monitoring: we should see inclination remain constant as we
    // cross descending nodes (i.e. switch from Northern to Southern
    // hemisphere), and change as we cross the first three ascending nodes

    var i = 0

    val orbitPeriodSeconds = initialOrbit.keplerianPeriod

//    propagator.multiplexer.add(floor(orbitPeriodSeconds / 2000.0)) { state: SpacecraftState ->
    propagator.multiplexer.add { interpolator: OrekitStepInterpolator ->

      val state = interpolator.currentState


      val norm = state.pvCoordinates.position.norm
      val altitude = norm - Constants.WGS84_EARTH_EQUATORIAL_RADIUS

      System.out.printf("${state.date} :: norm=%5.2f alt=%5.1f e=%5.3f %n", norm, altitude, state.orbit.e)

      if (++i > 150) {
        print("")
      }

//      val pos = state.getPVCoordinates(system.refFrame).position
//      System.out.format(
//        Locale.US, "%s %s hemisphere inclination = %5.3f mass=%5.1f%n",
//        state.date,
//        if (pos.z > 0) "Northern" else "Southern",
//        FastMath.toDegrees(state.orbit.i),
//        state.mass
//      )
//      print("")
    }


    // run simulation
//    propagator.propagate(initialOrbit.date.shiftedBy(10 * initialOrbit.keplerianPeriod))
    propagator.propagate(maneuverStartDate.shiftedBy(3L, TimeUnit.DAYS))


//    val stateBeforeManeuver = propagator.propagate(maneuverStartDate)
//
//    val velocityBeforeManeuver = stateBeforeManeuver.pvCoordinates.velocity
//    val velocityAfterManeuver = velocityBeforeManeuver.add(deltaV)
//
//    val pvAfterManeuver = PVCoordinates(
//      stateBeforeManeuver.pvCoordinates.position,
//      velocityAfterManeuver
//    )


    println("here")
  }
}

private fun buildSystem(): KeplerianSystem {
  val result = SystemDataUtils.fromYAML {
//    Thread.currentThread().contextClassLoader.getResourceAsStream("systems/dwarla/system.yaml")!!
    Thread.currentThread().contextClassLoader.getResourceAsStream("systems/maneuver/maneuver01.yaml")!!
  }

  val system = createGenericSystem(result.name, result)
  return system
}
