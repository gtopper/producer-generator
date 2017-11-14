package io.iguaz.grab.etl

import scala.concurrent.Future
import scala.io

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpHeader.ParsingResult.Ok
import akka.http.scaladsl.model.Uri.{Authority, Host}
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import com.typesafe.config.ConfigFactory

object Main {

  private val config = ConfigFactory.load()
  private val host = config.getString("host")
  private val port = config.getInt("port")
  private val table = config.getString("table")
  private val computeParallelism = config.getInt("compute-parallelism")
  private val printPeriod = config.getInt("print-period")

  def main(args: Array[String]): Unit = {
    implicit val actorSystem = ActorSystem()
    implicit val dispatcher = actorSystem.dispatcher
    implicit val actorMaterializer = ActorMaterializer()

    val headers = List(HttpHeader.parse("X-v3io-function", "PutItem").asInstanceOf[Ok].header)

    val lineSource = Source.fromIterator(() => io.Source.stdin.getLines()).zipWithIndex.map {
      case lineAndIndex@(_, i) =>
        if (i % printPeriod == 0) println(s"at line #$i...")
        lineAndIndex
    }
    val requestSource = lineSource.mapAsyncUnordered(computeParallelism) {
      case (line, index) => Future {
        val (key, json) = Converter(line)
        HttpRequest(
          HttpMethods.POST,
          Uri("http", Authority(Host(host), port), Uri.Path(table) / key),
          headers,
          json.toString
        ) -> index
      }
    }
    val httpFlow = Http().cachedHostConnectionPool[Long](host, port)
    val responseSource = requestSource.via(httpFlow)
    responseSource.runWith(Sink.ignore)
  }
}
