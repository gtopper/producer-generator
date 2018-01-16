package io.iguaz.grab.etl

import scala.io.Source

import org.json4s._
import org.json4s.jackson.JsonMethods.{compact, parse}

case class GeneratorSchema(key: GeneratorField, `time-field`: GeneratorField, columns: List[GeneratorField]) {

  def toIguazioSchema: String = {
    compact(JObject("fields" ->
      JArray((key :: `time-field` :: columns).map {
        case GeneratorField(name, t, nullable) => JObject(
          "name" -> JString(name),
          "type" -> JString(t),
          "nullable" -> JBool(nullable)
        )
      })
    ))
  }
}

object GeneratorSchema {

  private implicit val formats = org.json4s.DefaultFormats

  def fromResource(resource: String): GeneratorSchema = {
    val json = parse(Source.fromInputStream(getClass.getResourceAsStream(resource)).getLines().mkString)
    json.extract[GeneratorSchema]
  }
}

case class GeneratorField(name: String, `type`: String, nullable: Boolean = false)
