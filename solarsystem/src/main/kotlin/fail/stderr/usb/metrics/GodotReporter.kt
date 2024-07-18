package fail.stderr.usb.metrics

import com.codahale.metrics.*
import com.codahale.metrics.Timer
import godot.global.GD
import java.io.PrintStream
import java.text.DateFormat
import java.util.*
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit


/**
 * A reporter which outputs measurements to a [PrintStream], like `System.out`.
 */
class GodotReporter private constructor(
  registry: MetricRegistry,
  private val locale: Locale,
  private val clock: Clock,
  timeZone: TimeZone,
  rateUnit: TimeUnit,
  durationUnit: TimeUnit,
  filter: MetricFilter,
  executor: ScheduledExecutorService?,
  shutdownExecutorOnStop: Boolean,
  disabledMetricAttributes: Set<MetricAttribute>
) : ScheduledReporter(
  registry,
  "console-reporter",
  filter,
  rateUnit,
  durationUnit,
  executor,
  shutdownExecutorOnStop,
  disabledMetricAttributes
) {
  /**
   * A builder for [GodotReporter] instances. Defaults to using the default locale and
   * time zone, writing to `System.out`, converting rates to events/second, converting
   * durations to milliseconds, and not filtering metrics.
   */
  class Builder internal constructor(private val registry: MetricRegistry) {
    private var locale: Locale
    private var clock: Clock
    private var timeZone: TimeZone
    private var rateUnit: TimeUnit
    private var durationUnit: TimeUnit
    private var filter: MetricFilter
    private var executor: ScheduledExecutorService? = null
    private var shutdownExecutorOnStop = true
    private var disabledMetricAttributes: Set<MetricAttribute>

    init {
      this.locale = Locale.getDefault()
      this.clock = Clock.defaultClock()
      this.timeZone = TimeZone.getDefault()
      this.rateUnit = TimeUnit.SECONDS
      this.durationUnit = TimeUnit.MILLISECONDS
      this.filter = MetricFilter.ALL
      disabledMetricAttributes = emptySet()
    }

    /**
     * Specifies whether or not, the executor (used for reporting) will be stopped with same time with reporter.
     * Default value is true.
     * Setting this parameter to false, has the sense in combining with providing external managed executor via [.scheduleOn].
     *
     * @param shutdownExecutorOnStop if true, then executor will be stopped in same time with this reporter
     * @return `this`
     */
    fun shutdownExecutorOnStop(shutdownExecutorOnStop: Boolean): Builder {
      this.shutdownExecutorOnStop = shutdownExecutorOnStop
      return this
    }

    /**
     * Specifies the executor to use while scheduling reporting of metrics.
     * Default value is null.
     * Null value leads to executor will be auto created on start.
     *
     * @param executor the executor to use while scheduling reporting of metrics.
     * @return `this`
     */
    fun scheduleOn(executor: ScheduledExecutorService?): Builder {
      this.executor = executor
      return this
    }

    /**
     * Format numbers for the given [Locale].
     *
     * @param locale a [Locale]
     * @return `this`
     */
    fun formattedFor(locale: Locale): Builder {
      this.locale = locale
      return this
    }

    /**
     * Use the given [Clock] instance for the time.
     *
     * @param clock a [Clock] instance
     * @return `this`
     */
    fun withClock(clock: Clock): Builder {
      this.clock = clock
      return this
    }

    /**
     * Use the given [TimeZone] for the time.
     *
     * @param timeZone a [TimeZone]
     * @return `this`
     */
    fun formattedFor(timeZone: TimeZone): Builder {
      this.timeZone = timeZone
      return this
    }

    /**
     * Convert rates to the given time unit.
     *
     * @param rateUnit a unit of time
     * @return `this`
     */
    fun convertRatesTo(rateUnit: TimeUnit): Builder {
      this.rateUnit = rateUnit
      return this
    }

    /**
     * Convert durations to the given time unit.
     *
     * @param durationUnit a unit of time
     * @return `this`
     */
    fun convertDurationsTo(durationUnit: TimeUnit): Builder {
      this.durationUnit = durationUnit
      return this
    }

    /**
     * Only report metrics which match the given filter.
     *
     * @param filter a [MetricFilter]
     * @return `this`
     */
    fun filter(filter: MetricFilter): Builder {
      this.filter = filter
      return this
    }

    /**
     * Don't report the passed metric attributes for all metrics (e.g. "p999", "stddev" or "m15").
     * See [MetricAttribute].
     *
     * @param disabledMetricAttributes a [MetricFilter]
     * @return `this`
     */
    fun disabledMetricAttributes(disabledMetricAttributes: Set<MetricAttribute>): Builder {
      this.disabledMetricAttributes = disabledMetricAttributes
      return this
    }

    /**
     * Builds a [GodotReporter] with the given properties.
     *
     * @return a [GodotReporter]
     */
    fun build(): GodotReporter {
      return GodotReporter(
        registry,
        locale,
        clock,
        timeZone,
        rateUnit,
        durationUnit,
        filter,
        executor,
        shutdownExecutorOnStop,
        disabledMetricAttributes
      )
    }
  }

  private val dateFormat: DateFormat = DateFormat.getDateTimeInstance(
    DateFormat.SHORT,
    DateFormat.MEDIUM,
    locale
  )

  private val buf = StringBuilder()
  private val formatter = Formatter(buf, locale)

  init {
    dateFormat.timeZone = timeZone
  }


  override fun report(
    gauges: SortedMap<String?, Gauge<*>>,
    counters: SortedMap<String, Counter>,
    histograms: SortedMap<String, Histogram>,
    meters: SortedMap<String, Meter>,
    timers: SortedMap<String, Timer>
  ) {
    val dateTime = dateFormat.format(Date(clock.time))
    printWithBanner(dateTime, '=')

    buf.appendLine()

    if (!gauges.isEmpty()) {
      printWithBanner("-- Gauges", '-')
      for ((key, value) in gauges) {
        buf.appendLine(key)
        printGauge(value)
      }
      buf.appendLine()
    }

    if (!counters.isEmpty()) {
      printWithBanner("-- Counters", '-')
      for (entry in counters.entries) {
        buf.appendLine(entry.key)
        printCounter(entry)
      }
      buf.appendLine()
    }

    if (!histograms.isEmpty()) {
      printWithBanner("-- Histograms", '-')
      for ((key, value) in histograms) {
        buf.appendLine(key)
        printHistogram(value)
      }
      buf.appendLine()
    }

    if (!meters.isEmpty()) {
      printWithBanner("-- Meters", '-')
      for ((key, value) in meters) {
        buf.appendLine(key)
        printMeter(value)
      }
      buf.appendLine()
    }

    if (!timers.isEmpty()) {
      printWithBanner("-- Timers", '-')
      for ((key, value) in timers) {
        buf.appendLine(key)
        printTimer(value)
      }
      buf.appendLine()
    }

    buf.appendLine()
    GD.print(buf.toString())
    buf.clear()
  }

  private fun printMeter(meter: Meter) {
    printIfEnabled(MetricAttribute.COUNT, String.format(locale, "             count = %d", meter.count))
    printIfEnabled(
      MetricAttribute.MEAN_RATE, String.format(
        locale, "         mean rate = %2.2f events/%s", convertRate(meter.meanRate),
        rateUnit
      )
    )
    printIfEnabled(
      MetricAttribute.M1_RATE, String.format(
        locale, "     1-minute rate = %2.2f events/%s", convertRate(meter.oneMinuteRate),
        rateUnit
      )
    )
    printIfEnabled(
      MetricAttribute.M5_RATE, String.format(
        locale, "     5-minute rate = %2.2f events/%s", convertRate(meter.fiveMinuteRate),
        rateUnit
      )
    )
    printIfEnabled(
      MetricAttribute.M15_RATE, String.format(
        locale, "    15-minute rate = %2.2f events/%s", convertRate(meter.fifteenMinuteRate),
        rateUnit
      )
    )
  }

  private fun printCounter(entry: Map.Entry<String, Counter>) {
    formatter.format("             count = %d%n", entry.value.count)
  }

  private fun printGauge(gauge: Gauge<*>) {
    formatter.format("             value = %s%n", gauge.value)
  }

  private fun printHistogram(histogram: Histogram) {
    printIfEnabled(MetricAttribute.COUNT, String.format(locale, "             count = %d", histogram.count))
    val snapshot = histogram.snapshot
    printIfEnabled(MetricAttribute.MIN, String.format(locale, "               min = %d", snapshot.min))
    printIfEnabled(MetricAttribute.MAX, String.format(locale, "               max = %d", snapshot.max))
    printIfEnabled(MetricAttribute.MEAN, String.format(locale, "              mean = %2.2f", snapshot.mean))
    printIfEnabled(MetricAttribute.STDDEV, String.format(locale, "            stddev = %2.2f", snapshot.stdDev))
    printIfEnabled(MetricAttribute.P50, String.format(locale, "            median = %2.2f", snapshot.median))
    printIfEnabled(
      MetricAttribute.P75,
      String.format(locale, "              75%% <= %2.2f", snapshot.get75thPercentile())
    )
    printIfEnabled(
      MetricAttribute.P95,
      String.format(locale, "              95%% <= %2.2f", snapshot.get95thPercentile())
    )
    printIfEnabled(
      MetricAttribute.P98,
      String.format(locale, "              98%% <= %2.2f", snapshot.get98thPercentile())
    )
    printIfEnabled(
      MetricAttribute.P99,
      String.format(locale, "              99%% <= %2.2f", snapshot.get99thPercentile())
    )
    printIfEnabled(
      MetricAttribute.P999,
      String.format(locale, "            99.9%% <= %2.2f", snapshot.get999thPercentile())
    )
  }

  private fun printTimer(timer: Timer) {
    val snapshot = timer.snapshot
    printIfEnabled(MetricAttribute.COUNT, String.format(locale, "             count = %d", timer.count))
    printIfEnabled(
      MetricAttribute.MEAN_RATE, String.format(
        locale, "         mean rate = %2.2f calls/%s", convertRate(timer.meanRate),
        rateUnit
      )
    )
    printIfEnabled(
      MetricAttribute.M1_RATE, String.format(
        locale, "     1-minute rate = %2.2f calls/%s", convertRate(timer.oneMinuteRate),
        rateUnit
      )
    )
    printIfEnabled(
      MetricAttribute.M5_RATE, String.format(
        locale, "     5-minute rate = %2.2f calls/%s", convertRate(timer.fiveMinuteRate),
        rateUnit
      )
    )
    printIfEnabled(
      MetricAttribute.M15_RATE, String.format(
        locale, "    15-minute rate = %2.2f calls/%s", convertRate(timer.fifteenMinuteRate),
        rateUnit
      )
    )

    printIfEnabled(
      MetricAttribute.MIN, String.format(
        locale, "               min = %2.2f %s", convertDuration(snapshot.min.toDouble()),
        durationUnit
      )
    )
    printIfEnabled(
      MetricAttribute.MAX, String.format(
        locale, "               max = %2.2f %s", convertDuration(snapshot.max.toDouble()),
        durationUnit
      )
    )
    printIfEnabled(
      MetricAttribute.MEAN, String.format(
        locale, "              mean = %2.2f %s", convertDuration(snapshot.mean),
        durationUnit
      )
    )
    printIfEnabled(
      MetricAttribute.STDDEV, String.format(
        locale, "            stddev = %2.2f %s", convertDuration(snapshot.stdDev),
        durationUnit
      )
    )
    printIfEnabled(
      MetricAttribute.P50, String.format(
        locale, "            median = %2.2f %s", convertDuration(snapshot.median),
        durationUnit
      )
    )
    printIfEnabled(
      MetricAttribute.P75, String.format(
        locale, "              75%% <= %2.2f %s", convertDuration(snapshot.get75thPercentile()),
        durationUnit
      )
    )
    printIfEnabled(
      MetricAttribute.P95, String.format(
        locale, "              95%% <= %2.2f %s", convertDuration(snapshot.get95thPercentile()),
        durationUnit
      )
    )
    printIfEnabled(
      MetricAttribute.P98, String.format(
        locale, "              98%% <= %2.2f %s", convertDuration(snapshot.get98thPercentile()),
        durationUnit
      )
    )
    printIfEnabled(
      MetricAttribute.P99, String.format(
        locale, "              99%% <= %2.2f %s", convertDuration(snapshot.get99thPercentile()),
        durationUnit
      )
    )
    printIfEnabled(
      MetricAttribute.P999, String.format(
        locale, "            99.9%% <= %2.2f %s", convertDuration(snapshot.get999thPercentile()),
        durationUnit
      )
    )
  }

  private fun printWithBanner(s: String, c: Char) {
    buf.append(s)
    buf.append(' ')
    for (i in 0 until (CONSOLE_WIDTH - s.length - 1)) {
      buf.append(c)
    }
    buf.appendLine()
  }

  /**
   * Print only if the attribute is enabled
   *
   * @param type   Metric attribute
   * @param status Status to be logged
   */
  private fun printIfEnabled(type: MetricAttribute, status: String) {
    if (disabledMetricAttributes.contains(type)) {
      return
    }

    buf.appendLine(status)
  }

  companion object {
    /**
     * Returns a new [Builder] for [GodotReporter].
     *
     * @param registry the registry to report
     * @return a [Builder] instance for a [GodotReporter]
     */
    fun forRegistry(registry: MetricRegistry): Builder {
      return Builder(registry)
    }

    private const val CONSOLE_WIDTH = 80
  }
}
