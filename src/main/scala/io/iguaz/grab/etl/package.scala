package io.iguaz.grab

import com.typesafe.config.ConfigFactory

package object etl {
  private val config = ConfigFactory.load()

  val table = config.getString("table")
  val timeField = config.getString("time-field")
}
