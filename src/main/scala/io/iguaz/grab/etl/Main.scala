package io.iguaz.grab.etl

import scala.io.Source

object Main {

  def main(args: Array[String]): Unit = {
    val lineIterator = Source.stdin.getLines()
    Push(lineIterator)
  }
}
