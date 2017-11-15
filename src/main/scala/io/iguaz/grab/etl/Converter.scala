package io.iguaz.grab.etl

import org.json4s.JsonAST.{JNull, JObject}
import org.json4s._
import org.json4s.jackson.JsonMethods.parse

/** Converts a binlog event to a table key and Iguazio HTTP update call body. */
object Converter {

  def apply(event: String): (String, JObject) = {
    implicit val formats = org.json4s.DefaultFormats
    val json = parse(event).extract[Map[String, JValue]]
    val afterDataString = json("after_data").extract[String]
    val afterData = parse(afterDataString).extract[Map[String, Map[String, String]]]
    val key = afterData("driver_id")("value")
    val updatedAt = afterData("updated_at")("value")
    val newData = afterData.mapValues { field =>
      val `type` = field("type") match {
        case "int" | "decimal" | "float" => "N"
        case "string" | "date" => "S"
      }
      val value = field("value") match {
        case "" => JNull
        case other => JString(other)
      }
      JObject(`type` -> value)
    }
    key -> JObject(
      "Item" -> JObject(newData.toList),
      "ConditionExpression" -> JString(s"""not(exists(updated_at)) or updated_at < "$updatedAt"""")
    )
  }
}
