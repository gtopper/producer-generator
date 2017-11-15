package io.iguaz.grab.etl

import play.api.libs.json.Reads.mapReads
import play.api.libs.json._

/** Converts a binlog event to a table key and Iguazio HTTP update call body. */
object Converter {

  def apply(event: String): (String, JsObject) = {
    val json = Json.parse(event).as[Map[String, JsValue]]
    val afterDataString = json("after_data").as[String]
    val afterData = Json.parse(afterDataString).validate[Map[String, Map[String, String]]].get
    val key = afterData("driver_id")("value")
    val updatedAt = afterData("updated_at")("value")
    val newData = afterData.mapValues { field =>
      val `type` = field("type") match {
        case "int" | "decimal" | "float" => "N"
        case "string" | "date" => "S"
      }
      val value = field("value") match {
        case "" => JsNull
        case other => JsString(other)
      }
      Json.obj(`type` -> value)
    }
    key -> Json.obj(
      "Item" -> JsObject(newData),
      "ConditionExpression" -> JsString(s"""not(exists(updated_at)) or updated_at < "$updatedAt"""")
    )
  }
}
