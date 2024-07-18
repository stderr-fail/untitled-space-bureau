package fail.stderr.usb.system

import com.codahale.metrics.ConsoleReporter
import com.codahale.metrics.MetricRegistry
import com.codahale.metrics.Timer
import org.hipparchus.geometry.euclidean.threed.Vector3D
import org.junit.jupiter.api.Test
import java.time.Duration
import java.util.concurrent.TimeUnit

class SimpleBenchmarkTest {

  @Test
  fun test() {

    val registry = MetricRegistry()

    val reporter = ConsoleReporter.forRegistry(registry)
      .convertRatesTo(TimeUnit.SECONDS)
      .convertDurationsTo(TimeUnit.MILLISECONDS)
      .outputTo(System.err)
      .build()

    reporter.start(1, TimeUnit.SECONDS);


    val timer: Timer = registry.timer("thing-timer")

    val thing = ThingToBenchmark()

    for (i in 0..100_000) {

      val context = timer.time()

      thing.addVecs()

      context.stop();

    }


    Thread.sleep(Duration.ofSeconds(2L))

    println("here!")


  }
}

class ThingToBenchmark() {

  fun createVec1(): Vector3D {
    return Vector3D(1.0, 2.0, 3.0)
  }

  fun createVec2(): Vector3D {
    return Vector3D(1.0, 2.0, 3.0)
  }

  fun addVecs(): Vector3D {
    return createVec1().add(createVec2())
  }

}