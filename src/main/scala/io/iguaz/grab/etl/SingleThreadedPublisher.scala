package io.iguaz.grab.etl

import org.reactivestreams.{Publisher, Subscriber, Subscription}

/** A reactive streams publisher to work around SPARK-19476 by only using Spark's iterator from the original thread. */
private class SingleThreadedPublisher[T] extends Publisher[T] {

  private var cancelled = false
  private var demand = 0L
  private var subscriber: Subscriber[_ >: T] = _
  private val waitForDemandObject = new Object

  override def subscribe(s: Subscriber[_ >: T]): Unit = {
    this.subscriber = s
    val subscription = new Subscription {
      override def cancel(): Unit = waitForDemandObject.synchronized {
        cancelled = true
        waitForDemandObject.notify()
      }

      override def request(n: Long): Unit = {
        waitForDemandObject.synchronized {
          demand += n
          waitForDemandObject.notify()
        }
      }
    }
    s.onSubscribe(subscription)
  }

  private def produce(element: T): Unit = {
    demand -= 1
    subscriber.onNext(element)
  }

  def push(iterator: Iterator[T]): Unit = {
    iterator.takeWhile(_ => !cancelled).foreach { element =>
      waitForDemandObject.synchronized {
        if (!cancelled) {
          if (demand > 0L) {
            produce(element)
          } else {
            waitForDemandObject.wait()
            produce(element)
          }
        }
      }
    }
    if (!cancelled) {
      subscriber.onComplete()
    }
  }
}
