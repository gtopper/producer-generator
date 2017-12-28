package io.iguaz.grab.etl

import java.nio.file.Paths
import java.text.DateFormat
import java.util.{Date, Random}

import scala.collection.breakOut

import com.fasterxml.jackson.databind.util.ISO8601DateFormat

import io.iguaz.grab.etl.Configuration.table
import io.iguaz.v3io.kv.Filters.{Exists, GreaterThan, Not, Or}
import io.iguaz.v3io.kv.{OverwriteMode, SimpleRow, UpdateEntry}

object Generator {

  private implicit val formats = org.json4s.DefaultFormats

  private val random = new Random()

  private def generateString(): String = random.nextInt().toBinaryString.map {
    case '0' => 'X'
    case '1' => 'Y'
  }

  private val timestampFormat: DateFormat = new ISO8601DateFormat

  private def nowTimestamp(): String = timestampFormat.format(new Date())

  private def generateFromType(generatorType: String): Any = generatorType match {
    case "string" => generateString()
    case "integer" => random.nextLong()
    case "time" => nowTimestamp()
  }

  def generate(generatorSchema: GeneratorSchema): UpdateEntry = {

    val otherFields: Map[String, Any] = generatorSchema.columns.flatMap {
      case GeneratorField(name, t, nullable) =>
        if (!nullable | random.nextBoolean()) Some(name -> generateString()) else None
    }(breakOut)

    val key = generateFromType(generatorSchema.key.`type`)
    val timestamp = nowTimestamp()
    val row = SimpleRow(
      key.toString,
      otherFields ++ List(
        generatorSchema.key.name -> key,
        generatorSchema.`time-field`.name -> timestamp
      )
    )

    UpdateEntry(
      Paths.get(table),
      row,
      OverwriteMode.OVERWRITE,
      Or(Not(Exists(generatorSchema.`time-field`.name)), GreaterThan(generatorSchema.`time-field`.name, timestamp))
    )
  }
}
