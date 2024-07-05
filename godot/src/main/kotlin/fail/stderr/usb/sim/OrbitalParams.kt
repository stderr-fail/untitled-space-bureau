package fail.stderr.usb.sim

import org.hipparchus.geometry.euclidean.threed.Vector3D
import org.orekit.frames.FramesFactory
import org.orekit.time.AbsoluteDate
import org.orekit.time.TimeScalesFactory
import org.orekit.utils.TimeStampedPVCoordinates

object OrbitalParams {

  // https://en.wikipedia.org/wiki/Standard_gravitational_parameter

  val EARTH_MU_JPL = 3.9860043550702275E14
  val EARTH_MU_WIKIPEDIA = 3.9860044188E14

  val EARTH_MU = EARTH_MU_JPL

  val SUN_MU_JPL = 1.3271244004127946E20
  val SUN_MU = SUN_MU_JPL

  val COMMON_START_DATE = AbsoluteDate(2024, 6, 25, TimeScalesFactory.getUTC())

  val INERTIAL_FRAME = FramesFactory.getICRF()


  val MOON_JPL_GCRF_TIME_PV_COORDINATES_2024_06_25 = TimeStampedPVCoordinates(
    COMMON_START_DATE,
    Vector3D(247103127.84545514, -241931894.9419103, -135441258.07642883), // position
    Vector3D(776.8232298171, 640.2091587524, 330.3361481133), // velocity
    Vector3D(-0.0019535205, 0.0018845977, 0.0010588576), // acceleration
  )

  val MOON_JPL_ICRF_TIME_PV_COORDINATES_2024_06_25 = TimeStampedPVCoordinates(
    COMMON_START_DATE,
    Vector3D(8760275483.31514, -140042963592.87878, -60704613138.73316), // position
    Vector3D(30023.4843225478, 2239.3337942594, 1022.5505695745), // velocity
    Vector3D(-0.0022906534, 0.007117507, 0.0033243123), // acceleration
  )

  val EARTH_JPL_ICRF_TIME_PV_COORDINATES_2024_06_25 = TimeStampedPVCoordinates(
    COMMON_START_DATE,
    Vector3D(8513172355.469685, -139801031697.93686, -60569171880.65673), // position
    Vector3D(29246.6610927307, 1599.1246355069, 692.2144214612), // velocity
    Vector3D(-0.000337133, 0.0052329093, 0.0022654546), // acceleration
  )

  val MERCURY_JPL_ICRF_TIME_PV_COORDINATES_2024_06_25 = TimeStampedPVCoordinates(
    COMMON_START_DATE,
    Vector3D(-42337981451.681984, 24275622669.451183, 17336871705.19406), // position
    Vector3D(-38428.6595856764, -34596.466464372, -14496.7189574325), // velocity
    Vector3D(0.0406300842, -0.0244417523, -0.0172679229), // acceleration
  )

  val MERCURY_JPL_GCRF_TIME_PV_COORDINATES_2024_06_25 = TimeStampedPVCoordinates(
    COMMON_START_DATE,
    Vector3D(-50851153807.15167, 164076654367.38803, 77906043585.8508), // position
    Vector3D(-67675.3206784071, -36195.5910998789, -15188.9333788937), // velocity
    Vector3D(0.0409672172, -0.0296746616, -0.0195333775), // acceleration
  )

  val VENUS_JPL_ICRF_TIME_PV_COORDINATES_2024_06_25 = TimeStampedPVCoordinates(
    COMMON_START_DATE,
    Vector3D(-32594018899.44412, 92434238596.97969, 43629819041.64222), // position
    Vector3D(-33584.9816769385, -10331.5022981742, -2523.343447071), // velocity
    Vector3D(0.0033660315, -0.009922016, -0.0046775271), // acceleration
  )

  val MARS_JPL_ICRF_TIME_PV_COORDINATES_2024_06_25 = TimeStampedPVCoordinates(
    COMMON_START_DATE,
    Vector3D(206756911977.87845, 21764310217.263783, 4422898108.2833605), // position
    Vector3D(-1652.0693155645, 23757.6663812706, 10942.032088407), // velocity
    Vector3D(-0.0030185281, -0.0003240948, -0.000067224), // acceleration
  )

}