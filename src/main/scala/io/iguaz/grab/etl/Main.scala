package io.iguaz.grab.etl

import java.util.Properties

import scala.concurrent.Await
import scala.concurrent.duration.Duration

import io.iguaz.v3io.container.DoInContainer
import io.iguaz.v3io.kv.KeyValueOperations

object Main {

  def main(args: Array[String]): Unit = {
    val schema = GeneratorSchema.fromResource("/schema.json")
    val updateEntryIterator = Iterator.continually(Generator.generate(schema))
    updateEntryIterator.foreach(println)
    DoInContainer(new Properties) { container =>
      val kvOps = KeyValueOperations(container)
      val updateFuture = kvOps.updateMultiple(updateEntryIterator)
      Await.result(updateFuture, Duration.Inf)
    }
  }
}
