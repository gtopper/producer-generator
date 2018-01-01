package io.iguaz.grab.etl

object PrintPeriodIterator {

  val printPeriod = sys.props.getOrElse("print-period", "100000").toInt

  def create(): Iterator[Unit] = {
    val start = System.currentTimeMillis()
    var lastCycleStart = start
    Iterator.from(1).map { count =>
      if (count % printPeriod == 0) {
        val cycleStart = System.currentTimeMillis()
        val secondPassed = (cycleStart - start) / 1000
        val millisSinceLastCycle = cycleStart - lastCycleStart
        val millisRate = printPeriod / millisSinceLastCycle
        val secondsRate = millisRate * 1000
        println(s"[$secondsRate/sec]\t$count entries written after $secondPassed seconds...")
        lastCycleStart = cycleStart
      }
    }
  }
}
