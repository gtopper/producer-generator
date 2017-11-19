package io.iguaz.grab.etl

import java.nio.file.Paths
import java.util.Properties

import io.iguaz.v3io.container.DoInContainer
import io.iguaz.v3io.kv.Filters.{Exists, GreaterThan, Not, Or}
import io.iguaz.v3io.kv.{KeyValueOperations, OverwriteMode, UpdateEntry}

object Push {

  def apply(eventIterator: Iterator[String]): Unit = {
    val updateEntryIterator = eventIterator.map { line =>
      val row = Converter(line)
      val updatedAt = row.getValue[String](timeField)
      UpdateEntry(
        Paths.get(table),
        row,
        OverwriteMode.OVERWRITE,
        Or(Not(Exists(timeField)), GreaterThan(timeField, updatedAt))
      )
    }
    DoInContainer(new Properties) { container =>
      val kvOps = KeyValueOperations(container)
      kvOps.updateMultiple(updateEntryIterator)
    }
  }
}
