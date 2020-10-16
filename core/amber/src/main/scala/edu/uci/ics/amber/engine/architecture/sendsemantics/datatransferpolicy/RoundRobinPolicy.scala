package edu.uci.ics.amber.engine.architecture.sendsemantics.datatransferpolicy

import edu.uci.ics.amber.engine.architecture.sendsemantics.routees.BaseRoutee
import edu.uci.ics.amber.engine.common.ambermessage.WorkerMessage.{DataMessage, EndSending}
import edu.uci.ics.amber.engine.common.ambertag.LinkTag
import edu.uci.ics.amber.engine.common.tuple.ITuple
import akka.actor.{Actor, ActorContext, ActorRef}
import akka.event.LoggingAdapter
import akka.util.Timeout

import scala.concurrent.ExecutionContext

class RoundRobinPolicy(batchSize: Int) extends DataTransferPolicy(batchSize) {
  var routees: Array[BaseRoutee] = _
  var sequenceNum: Array[Long] = _
  var roundRobinIndex = 0
  var batch: Array[ITuple] = _
  var currentSize = 0

  override def noMore()(implicit sender: ActorRef): Unit = {
    if (currentSize > 0) {
      routees(roundRobinIndex).schedule(
        DataMessage(sequenceNum(roundRobinIndex), batch.slice(0, currentSize))
      )
      sequenceNum(roundRobinIndex) += 1
    }
    var i = 0
    while (i < routees.length) {
      routees(i).schedule(EndSending(sequenceNum(i)))
      i += 1
    }
  }

  override def pause(): Unit = {
    for (i <- routees) {
      i.pause()
    }
  }

  override def resume()(implicit sender: ActorRef): Unit = {
    for (i <- routees) {
      i.resume()
    }
  }

  override def accept(tuple: ITuple)(implicit sender: ActorRef): Unit = {
    batch(currentSize) = tuple
    currentSize += 1
    if (currentSize == batchSize) {
      currentSize = 0
      routees(roundRobinIndex).schedule(DataMessage(sequenceNum(roundRobinIndex), batch))
      sequenceNum(roundRobinIndex) += 1
      roundRobinIndex = (roundRobinIndex + 1) % routees.length
      batch = new Array[ITuple](batchSize)
    }
  }

  override def initialize(tag: LinkTag, next: Array[BaseRoutee])(implicit
      ac: ActorContext,
      sender: ActorRef,
      timeout: Timeout,
      ec: ExecutionContext,
      log: LoggingAdapter
  ): Unit = {
    super.initialize(tag, next)
    assert(next != null)
    routees = next
    routees.foreach(_.initialize(tag))
    batch = new Array[ITuple](batchSize)
    sequenceNum = new Array[Long](routees.length)
  }

  override def dispose(): Unit = {
    routees.foreach(_.dispose())
  }

  override def reset(): Unit = {
    routees.foreach(_.reset())
    batch = new Array[ITuple](batchSize)
    sequenceNum = new Array[Long](routees.length)
    roundRobinIndex = 0
    currentSize = 0
  }
}
