package io.iguaz.grab.etl

import scala.io.Source

import org.scalatest.{FunSpec, Matchers}
import play.api.libs.json.Json

class ConverterTest extends FunSpec with Matchers {

  it("Sample event") {
    val event = Source.fromResource("sample-event.json").getLines().mkString("")
    val expected = Source.fromResource("expected-request-body.json").getLines().mkString("")
    val (rowKey, json) = Converter(event)
    rowKey shouldBe "123"
    json shouldBe Json.parse(expected)
  }
}
