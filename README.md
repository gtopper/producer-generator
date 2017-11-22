Grab ETL use case
-------------------------

## Prepare building environment
* Make sure Java 7 is used.
* `mkdir lib`.
*  From Iguazio master node (`10.80.1.21` from Forti VPN, `10.150.1.101` from EC2), copy
  `/home/iguazio/hadoop/share/hadoop/hdfs/lib/v3io-hcfs.jar`, `/home/iguazio/spark2/lib/v3io-spark-streaming.jar`, and `/home/iguazio/spark2/lib/v3io-spark-object-dataframe.jar` to `lib`.

## Building
* Run `sbt package`.

## Compiling against
* Add the created `target/scala-2.11/grab-etl-v3io_2.11-0.1.jar` as an unmanaged dependency of your application.
* Call `io.iguaz.grab.etl.Push.apply` from your application code. See `io.iguaz.grab.etl.Main` for an example.
* Build your application JAR.

## Running your application
* Upload your application JAR.
* If your application is an uberjar, simply run using `spark-submit`.
* If your application is not an uberjar (`grab-etl-v3io_2.11-0.1.jar` is not built into it), you'll need to upload 
  `grab-etl-v3io_2.11-0.1.jar` as well, and pass `spark-submit` this additional parameter: `--jars /home/iguazio/hadoop/share/hadoop/hdfs/lib/v3io-hcfs.jar,/home/iguazio/spark2/lib/v3io-spark-streaming.jar,/home/iguazio/spark2/lib/v3io-spark-object-dataframe.jar,/home/iguazio/grab-etl-v3io_2.11-0.1.jar`.