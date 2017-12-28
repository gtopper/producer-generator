package io.iguaz.grab.etl

import scala.io.Source

import org.json4s.jackson.JsonMethods.parse

case class GeneratorSchema(key: GeneratorField, `time-field`: GeneratorField, columns: List[GeneratorField])

object GeneratorSchema {

  private implicit val formats = org.json4s.DefaultFormats

  def fromResource(resource: String): GeneratorSchema = {
    val json = parse(Source.fromInputStream(getClass.getResourceAsStream(resource)).getLines().mkString)
    json.extract[GeneratorSchema]
  }
}

case class GeneratorField(name: String, `type`: String, nullable: Boolean = false)
