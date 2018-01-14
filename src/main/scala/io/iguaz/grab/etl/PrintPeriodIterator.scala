package io.iguaz.grab.etl

import com.typesafe.scalalogging.LazyLogging

object PrintPeriodIterator extends LazyLogging {

  val printPeriod = sys.props.getOrElse("print-period", "5000").toInt

  def create(): Iterator[Unit] = {
    val start = System.currentTimeMillis()
    var lastCycleStart = start
    var lastCount = 0L
    Iterator.from(1).map { count =>
      val cycleStart = System.currentTimeMillis()
      if (cycleStart >= lastCycleStart + printPeriod) {
        val secondPassed = (cycleStart - start) / 1000
        val millisSinceLastCycle = cycleStart - lastCycleStart
        val progress = count - lastCount
        val secondsRate = progress * 1000L / millisSinceLastCycle
        logger.info(s"[$secondsRate/sec] $count entries written after $secondPassed seconds...")
        lastCycleStart = cycleStart
        lastCount = count
      }
    }
  }
}
