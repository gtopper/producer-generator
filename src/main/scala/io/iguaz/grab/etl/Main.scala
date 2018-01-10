package io.iguaz.grab.etl

import java.util.Properties

import scala.concurrent.Await
import scala.concurrent.duration.Duration

import io.iguaz.v3io.api.container.ContainerAlias
import io.iguaz.v3io.container.DoInContainer
import io.iguaz.v3io.kv.KeyValueOperations

object Main {

  def main(args: Array[String]): Unit = {
    val schema = GeneratorSchema.fromResource("/schema.json")
    val updateEntryIterator = PrintPeriodIterator.create().map(_ => Generator.generate(schema))
    val containerAlias = sys.props.getOrElse("container-alias", "bigdata")
    val newDaemon = sys.props.getOrElse("new-daemon", "true").toBoolean
    if (newDaemon) {
      val kvOps = KeyValueOperations(ContainerAlias(containerAlias), sys.props.filterKeys(_.startsWith("v3io")).toMap)
      val updateFuture = kvOps.updateMultiple(updateEntryIterator)
      Await.result(updateFuture, Duration.Inf)
    } else {
      DoInContainer(new Properties) { container =>
        val kvOps = KeyValueOperations(container)
        val updateFuture = kvOps.updateMultiple(updateEntryIterator)
        Await.result(updateFuture, Duration.Inf)
      }
    }
  }
}
