package io.iguaz.grab.etl

import scala.io.Source.fromInputStream
import scala.io.{BufferedSource, Codec}

import org.json4s.jackson.JsonMethods._
import org.scalatest.{FunSpec, Matchers}

class ConverterTest extends FunSpec with Matchers {

  private implicit val formats = org.json4s.DefaultFormats

  it("Sample event") {
    val event = fromResource("sample-event.json").getLines().mkString("")
    val expected = fromResource("expected-row-data.json").getLines().mkString("")
    val row = Converter(event)
    row.getKey shouldBe "123"
    row.getFields shouldBe parse(expected).extract[Map[String, Any]]
  }

  /** From Scala 2.12 [[scala.io.Source]]. */
  private def fromResource(resource: String, classLoader: ClassLoader = Thread.currentThread().getContextClassLoader)
                          (implicit codec: Codec): BufferedSource =
    fromInputStream(classLoader.getResourceAsStream(resource))
}
