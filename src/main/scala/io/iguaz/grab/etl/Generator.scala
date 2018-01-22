package io.iguaz.grab.etl

import java.nio.file.Paths

import io.iguaz.grab.etl.Configuration.table
import io.iguaz.v3io.kv.{OverwriteMode, SimpleRow, UpdateEntry}

object Generator {

  private var count = 0L

  def generate(generatorSchema: GeneratorSchema): UpdateEntry = {

    val path = Paths.get(table)

    val row = SimpleRow(count.toString, Map("count" -> count))

    count += 1

    UpdateEntry(
      path,
      row,
      OverwriteMode.OVERWRITE
    )
  }
}
