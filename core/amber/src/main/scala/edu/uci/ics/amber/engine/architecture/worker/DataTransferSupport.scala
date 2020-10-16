package edu.uci.ics.amber.engine.architecture.worker

import edu.uci.ics.amber.engine.architecture.sendsemantics.datatransferpolicy.DataTransferPolicy
import edu.uci.ics.amber.engine.architecture.sendsemantics.routees.BaseRoutee
import edu.uci.ics.amber.engine.common.amberexception.BreakpointException
import edu.uci.ics.amber.engine.common.ambertag.{LayerTag, LinkTag}
import edu.uci.ics.amber.engine.common.tuple.ITuple
import akka.actor.{ActorContext, ActorRef}
import akka.event.LoggingAdapter
import akka.util.Timeout

import scala.collection.mutable
import scala.concurrent.ExecutionContext
import scala.util.control.Breaks

trait DataTransferSupport extends BreakpointSupport {
  var output = new Array[DataTransferPolicy](0)
  var skippedInputTuples = new mutable.HashSet[ITuple]
  var skippedOutputTuples = new mutable.HashSet[ITuple]

  def pauseDataTransfer(): Unit = {
    var i = 0
    while (i < output.length) {
      output(i).pause()
      i += 1
    }
  }

  def resumeDataTransfer()(implicit sender: ActorRef): Unit = {
    var i = 0
    while (i < output.length) {
      output(i).resume()
      i += 1
    }
  }

  def endDataTransfer()(implicit sender: ActorRef): Unit = {
    var i = 0
    while (i < output.length) {
      output(i).noMore()
      i += 1
    }
  }

  def cleanUpDataTransfer()(implicit sender: ActorRef): Unit = {
    var i = 0
    while (i < output.length) {
      output(i).dispose()
      i += 1
    }
    //clear all data transfer policies
    output = Array()
  }

  def transferTuple(tuple: ITuple, tupleId: Long)(implicit sender: ActorRef): Unit = {
    if (tuple != null && !skippedOutputTuples.contains(tuple)) {
      var i = 1
      var breakpointTriggered = false
      var needUserFix = false
      while (i < breakpoints.length) {
        breakpoints(i).accept(tuple)
        if (breakpoints(i).isTriggered) {
          breakpoints(i).triggeredTuple = tuple
          breakpoints(i).triggeredTupleId = tupleId
        }
        breakpointTriggered |= breakpoints(i).isTriggered
        needUserFix |= breakpoints(i).needUserFix
        i += 1
      }
      i = 0
      if (!needUserFix) {
        while (i < output.length) {
          output(i).accept(tuple)
          i += 1
        }
      }
      if (breakpointTriggered) {
        throw new BreakpointException()
      }
    }
  }

  def updateOutput(policy: DataTransferPolicy, tag: LinkTag, receivers: Array[BaseRoutee])(implicit
      ac: ActorContext,
      sender: ActorRef,
      timeout: Timeout,
      ec: ExecutionContext,
      log: LoggingAdapter
  ): Unit = {
    var i = 0
    policy.initialize(tag, receivers)
    Breaks.breakable {
      while (i < output.length) {
        if (output(i).tag == policy.tag) {
          output(i).dispose()
          output(i) = policy
          Breaks.break()
        }
        i += 1
      }
      output = output :+ policy
    }
  }

  def resetOutput(): Unit = {
    output.foreach {
      _.reset()
    }
  }

}
