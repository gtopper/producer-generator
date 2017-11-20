package io.iguaz.grab.etl

import org.apache.spark.sql.SparkSession

object Main {

  private val spark = SparkSession.builder().appName("Grab ETL").getOrCreate()

  import spark.implicits._

  def main(args: Array[String]): Unit = {

    val inputPaths = args

    val df = spark.read.load(inputPaths: _*).select("after_data").map(_.getAs[String]("after_data"))
    df.foreachPartition(Push(_))
  }
}
