package io.iguaz.grab

package object etl {
  val table = sys.props.getOrElse("table", "/grab-etl")
  val timeField = sys.props.getOrElse("time-field", "updated_at")
  val keyField = sys.props.getOrElse("key-field", "driver_id")
}
