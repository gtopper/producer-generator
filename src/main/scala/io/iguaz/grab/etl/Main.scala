package io.iguaz.grab.etl

import java.io.PrintStream
import java.nio.file.Paths
import java.util.Properties

import scala.collection.JavaConverters._
import scala.concurrent.Await
import scala.concurrent.duration.Duration

import io.iguaz.grab.etl.Configuration._
import io.iguaz.v3io.api.Mode
import io.iguaz.v3io.api.container.ContainerAlias
import io.iguaz.v3io.container.DoInContainer
import io.iguaz.v3io.file.{FileOperations, FileOperationsFactory}
import io.iguaz.v3io.fs.client.api.FsPermission
import io.iguaz.v3io.kv.KeyValueOperations

object Main {

  def main(args: Array[String]): Unit = {
    val schema = GeneratorSchema.fromResource("/schema.json")
    val iguazioSchema = schema.toIguazioSchema
    val updateEntryIterator = PrintPeriodIterator.create().map(_ => Generator.generate(schema))
    val containerAlias = sys.props.getOrElse("container-alias", "bigdata")
    val newDaemon = sys.props.getOrElse("new-daemon", "true").toBoolean
    if (newDaemon) {
      val fileOps = FileOperationsFactory.create(ContainerAlias(containerAlias), new Properties)
      writeSchema(fileOps, iguazioSchema)
      val kvOps = KeyValueOperations(ContainerAlias(containerAlias), sys.props.filterKeys(_.startsWith("v3io")).toMap)
      val updateFuture = kvOps.updateMultiple(updateEntryIterator)
      Await.result(updateFuture, Duration.Inf)
    } else {
      DoInContainer(new Properties) { container =>
        val fileOps = FileOperationsFactory.create(container, new Properties)
        writeSchema(fileOps, iguazioSchema)
        val kvOps = KeyValueOperations(container)
        val updateFuture = kvOps.updateMultiple(updateEntryIterator)
        Await.result(updateFuture, Duration.Inf)
      }
    }
  }

  def writeSchema(fileOps: FileOperations, schema: String) = {
    val tablePath = Paths.get(table)
    fileOps.mkdirs(tablePath, FsPermission.getDefault)
    val schemaPath = tablePath.resolve(".#schema")
    val os = fileOps.openFileForCreate(schemaPath, 64 * 1024, true, 1024, Set(Mode.OWNER_ALL).asJavaCollection)
    val ps = new PrintStream(os)
    ps.println(schema)
    ps.flush()
  }
}
