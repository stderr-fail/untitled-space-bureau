package fail.stderr.usb.system

import fail.stderr.usb.QuickTest
import fail.stderr.usb.common.KCamera
import fail.stderr.usb.extensions.toGodotVectorStringFlipYZ
import fail.stderr.usb.system.model.DefaultCelestialBodyHolder
import org.joml.Matrix4f
import org.joml.Matrix4x3f
import org.joml.Vector3f
import org.joml.Vector4f
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

class OrbitCoordinatesTesselationTest {

  var log: Logger = LoggerFactory.getLogger(QuickTest::class.java)

  @Test
  fun propagateOneYear() {
// [X: (1, 0, -0), Y: (-0, 0.991445, -0.130526), Z: (0, 0.130526, 0.991445), O: (0, 3, 20)]

    // 1.30322527885437, 0, 0, 0
    //0, 1.30322527885437, 0, 0
    //0, 0, -1.00002503395081, -1
    //0, 0, -0.10000125318766, 0
    val camera = KCamera(
      projectionMatrix   = Matrix4f(
        Vector4f(1.30322527885437f, 0f, 0f, 0f),
        Vector4f(0f, 1.30322527885437f, 0f, 0f),
        Vector4f(0f, 0f, -1.00002503395081f, -1f),
        Vector4f(0f, 0f, -0.10000125318766f, 0f),
      ),
      cameraTransform = Matrix4x3f(
        Vector3f(1f, 0f, -0f),
        Vector3f(-0f, 0.991445f, -0.130526f),
        Vector3f(0f, 0.130526f, 0.991445f),
        Vector3f(0f, 3f, 20f),
      ),
      viewportW = 1200f,
      viewportH = 1200f,
    )

    val system = buildSystem()

    val body = system.allCelestialBodies.get("Dwar") as DefaultCelestialBodyHolder
    val frame = system.refFrame

    val segmentCount = 96
    val startDate = system.refDate

    val segmentTimeIncrement = Math.floor(body.orbit.keplerianPeriod / segmentCount).toLong() - 1L

    val vec2s = mutableListOf<String>()
    val vec3s = mutableListOf<String>()

    for (i in 0..segmentCount) {
      val date = startDate.shiftedBy(segmentTimeIncrement * i, TimeUnit.SECONDS)
      val pv = body.orbit.getPVCoordinates(date, frame)

//      10_000_000_000

      var v3 = Vector3f(pv.position.x.toFloat(), pv.position.z.toFloat(), pv.position.y.toFloat())
      var v3scaled = v3.div(10_000_000_000f)

      val pos2d = camera.unprojectPosition(v3scaled)

      vec2s.add("Vector2(${pos2d.x}, ${pos2d.y})")
      vec3s.add(pv.position.toGodotVectorStringFlipYZ())
    }

    println(vec2s.joinToString(",\n"))
    println(vec3s.joinToString(",\n"))


//    for (i in 1..numFullSeconds) {
//      val toDate = system.refDate.shiftedBy(i, TimeUnit.SECONDS)
//
//      system.nonRootCelestialBodies.forEach { body ->
//        val pv = body.orbit.getPVCoordinates(toDate, body.parent.frame)
////        log.info("position=${pv.position} d=${pv.position.norm}")
//        numPropagations++
//      }
//
//    }


//    val elapsed = end - start
//    println("here, timed ${numPropagations} propagations in ${elapsed} millis")


  }

  fun buildSystem(): KeplerianSystem {
    val result = SystemDataUtils.fromYAML {
      Thread.currentThread().contextClassLoader.getResourceAsStream("systems/dwarla/system.yaml")!!
    }

    val system = createGenericSystem("Dwarlis", result)
    return system
  }
}