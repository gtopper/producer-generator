package io.iguaz.grab.etl

object Configuration {
  val table: String = sys.props.getOrElse("table", "/grab-simulated-data")
}
