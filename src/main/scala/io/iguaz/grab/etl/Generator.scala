package io.iguaz.grab.etl

import java.nio.file.Paths
import java.util.Random

import scala.collection.breakOut

import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat

import io.iguaz.grab.etl.Configuration.table
import io.iguaz.v3io.kv.Filters._
import io.iguaz.v3io.kv.{OverwriteMode, SimpleRow, UpdateEntry}

object Generator {

  private implicit val formats = org.json4s.DefaultFormats

  private val random = new Random()

  private val valueDomainSize = sys.props.getOrElse("value-domain-size", "1000000").toInt

  private val dateTimeFormatter = ISODateTimeFormat.dateTimeNoMillis()

  private def generateString(): String = random.nextInt(valueDomainSize).toBinaryString.map {
    case '0' => 'X'
    case '1' => 'Y'
  }

  private def generateFromType(generatorType: String, timestamp: String): Any = generatorType match {
    case "string" => generateString()
    case "integer" => random.nextInt(valueDomainSize)
    case "time" => timestamp
  }

  def generate(generatorSchema: GeneratorSchema): UpdateEntry = {

    val now = DateTime.now()
    val timestamp = now.toString(dateTimeFormatter)

    val otherFields: Map[String, Any] = generatorSchema.columns.flatMap {
      case GeneratorField(name, t, nullable) =>
        if (!nullable | random.nextBoolean()) Some(name -> generateFromType(t, timestamp)) else None
    }(breakOut)

    val key = generateFromType(generatorSchema.key.`type`, timestamp)
    val row = SimpleRow(
      key.toString,
      otherFields ++ List(
        generatorSchema.key.name -> key,
        generatorSchema.`time-field`.name -> timestamp
      )
    )

    val subdirs = List(
      s"year=${now.getYear}",
      s"month=${now.getMonthOfYear}",
      s"day=${now.getDayOfMonth}",
      s"hour=${now.getHourOfDay}"
    )
    val path = Paths.get(table, subdirs: _*)

    UpdateEntry(
      path,
      row,
      OverwriteMode.OVERWRITE,
      Or(Not(Exists(generatorSchema.`time-field`.name)), LessThanOrEqual(generatorSchema.`time-field`.name, timestamp))
    )
  }
}
