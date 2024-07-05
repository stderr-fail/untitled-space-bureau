package fail.stderr.usb.sim

import org.orekit.frames.FramesFactory
import org.orekit.orbits.KeplerianOrbit
import org.orekit.orbits.Orbit
import org.orekit.propagation.analytical.KeplerianPropagator


fun buildEarthInitialOrbit(): Orbit {

  val orbit = KeplerianOrbit(
    OrbitalParams.EARTH_JPL_ICRF_TIME_PV_COORDINATES_2024_06_25,
    OrbitalParams.INERTIAL_FRAME,
    OrbitalParams.SUN_MU,
  )

  return orbit
}

fun buildMoonInitialOrbit(): Orbit {


  val orbit = KeplerianOrbit(
//    OrbitalParams.MOON_JPL_ICRF_TIME_PV_COORDINATES_2024_06_25,
    OrbitalParams.MOON_JPL_GCRF_TIME_PV_COORDINATES_2024_06_25,
    FramesFactory.getGCRF(),
    OrbitalParams.EARTH_MU,
  )

  return orbit
}

fun buildMercuryInitialOrbit(): Orbit {

  val orbit = KeplerianOrbit(
    OrbitalParams.MERCURY_JPL_ICRF_TIME_PV_COORDINATES_2024_06_25,
    OrbitalParams.INERTIAL_FRAME,
    OrbitalParams.SUN_MU,
  )

  return orbit
}

fun buildVenusInitialOrbit(): Orbit {

  val orbit = KeplerianOrbit(
    OrbitalParams.VENUS_JPL_ICRF_TIME_PV_COORDINATES_2024_06_25,
    OrbitalParams.INERTIAL_FRAME,
    OrbitalParams.SUN_MU,
  )

  return orbit
}

fun buildMarsInitialOrbit(): Orbit {

  val orbit = KeplerianOrbit(
    OrbitalParams.MARS_JPL_ICRF_TIME_PV_COORDINATES_2024_06_25,
    OrbitalParams.INERTIAL_FRAME,
    OrbitalParams.SUN_MU,
  )

  return orbit
}

fun buildEarthPropagator(): KeplerianPropagator {

  val initialOrbit = buildEarthInitialOrbit()
  val propagator = buildBasePropagator(initialOrbit)

  return propagator
}

fun buildMoonPropagator(): KeplerianPropagator {

  val initialOrbit = buildMoonInitialOrbit()
  val propagator = buildBasePropagator(initialOrbit)

  return propagator
}

fun buildMercuryPropagator(): KeplerianPropagator {

  val initialOrbit = buildMercuryInitialOrbit()
  val propagator = buildBasePropagator(initialOrbit)

  return propagator
}

fun buildVenusPropagator(): KeplerianPropagator {

  val initialOrbit = buildVenusInitialOrbit()
  val propagator = buildBasePropagator(initialOrbit)

  return propagator
}

fun buildMarsPropagator(): KeplerianPropagator {

  val initialOrbit = buildMarsInitialOrbit()
  val propagator = buildBasePropagator(initialOrbit)

  return propagator
}

fun buildBasePropagator(initialOrbit: Orbit): KeplerianPropagator {
  val propagator = KeplerianPropagator(initialOrbit)
  return propagator
}