package io.iguaz.grab.etl

import scala.io.Source.fromInputStream
import scala.io.{BufferedSource, Codec}

import org.json4s.jackson.JsonMethods._
import org.scalatest.{FunSpec, Matchers}

class ConverterTest extends FunSpec with Matchers {

  it("Sample event") {
    val event = fromResource("sample-event.json").getLines().mkString("")
    val expected = fromResource("expected-request-body.json").getLines().mkString("")
    val (rowKey, json) = Converter(event)
    rowKey shouldBe "123"
    json shouldBe parse(expected)
  }

  /** From Scala 2.12 [[scala.io.Source]]. */
  private def fromResource(resource: String, classLoader: ClassLoader = Thread.currentThread().getContextClassLoader)
                          (implicit codec: Codec): BufferedSource =
    fromInputStream(classLoader.getResourceAsStream(resource))
}
