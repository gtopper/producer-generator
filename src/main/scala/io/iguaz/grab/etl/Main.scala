package io.iguaz.grab.etl

import scala.concurrent.Await
import scala.concurrent.duration.Duration

import io.iguaz.v3io.api.container.ContainerID
import io.iguaz.v3io.kv.KeyValueOperations

object Main {

  def main(args: Array[String]): Unit = {
    val schema = GeneratorSchema.fromResource("/schema.json")
    val updateEntryIterator = PrintPeriodIterator.create().map(_ => Generator.generate(schema))
    val kvOps = KeyValueOperations(ContainerID(1), sys.props.filterKeys(_.startsWith("v3io")).toMap)
    val updateFuture = kvOps.updateMultiple(updateEntryIterator)
    Await.result(updateFuture, Duration.Inf)
  }
}
