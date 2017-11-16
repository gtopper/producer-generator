package io.iguaz.grab.etl

import java.nio.file.Paths
import java.util.Properties

import scala.io.Source

import com.typesafe.config.ConfigFactory

import io.iguaz.v3io.container.DoInContainer
import io.iguaz.v3io.kv.Filters._
import io.iguaz.v3io.kv.{KeyValueOperations, OverwriteMode, UpdateEntry}

object Main {

  private val config = ConfigFactory.load()
  private val host = config.getString("host")
  private val port = config.getInt("port")
  private val table = config.getString("table")
  private val computeParallelism = config.getInt("compute-parallelism")
  private val printPeriod = config.getInt("print-period")

  val timeAttribute = "updated_at"

  def main(args: Array[String]): Unit = {
    val lineIterator = Source.stdin.getLines()
    val updateEntryIterator = lineIterator.map { line =>
      val row = Converter(line)
      val updatedAt = row.getValue[String](timeAttribute)
      UpdateEntry(
        Paths.get(table),
        row,
        OverwriteMode.OVERWRITE,
        Or(Not(Exists(timeAttribute)), GreaterThan(timeAttribute, updatedAt))
      )
    }
    DoInContainer(new Properties) { container =>
      val kvOps = KeyValueOperations(container)
      kvOps.updateMultiple(updateEntryIterator)
    }
  }
}
