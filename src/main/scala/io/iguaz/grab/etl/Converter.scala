package io.iguaz.grab.etl

import org.json4s._
import org.json4s.jackson.JsonMethods.parse

import io.iguaz.v3io.kv.{Row, SimpleRow}

/** Converts a binlog event to a table key and Iguazio HTTP update call body. */
object Converter {

  def apply(event: String): Row = {
    implicit val formats = org.json4s.DefaultFormats
    val afterData = parse(event).extract[Map[String, Map[String, String]]]
    val key = afterData(keyField)("value")
    val newData = afterData.mapValues { field =>
      field("value") match {
        case "" => null
        case other =>
          field("type") match {
            case "int" | "bigInt" => other.toLong
            case "decimal" | "float" => other.toDouble
            case _ => other
          }
      }
    }

    SimpleRow(key, newData)
  }
}
