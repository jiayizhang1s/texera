package edu.uci.ics.amber.engine.architecture.sendsemantics.datatransferpolicy

import edu.uci.ics.amber.engine.architecture.sendsemantics.routees.BaseRoutee
import edu.uci.ics.amber.engine.common.ambertag.LinkTag
import edu.uci.ics.amber.engine.common.tuple.ITuple
import akka.actor.{Actor, ActorContext, ActorRef}
import akka.event.LoggingAdapter
import akka.util.Timeout

import scala.concurrent.ExecutionContext

abstract class DataTransferPolicy(var batchSize: Int) extends Serializable {
  var tag: LinkTag = _

  def accept(tuple: ITuple)(implicit sender: ActorRef = Actor.noSender): Unit

  def noMore()(implicit sender: ActorRef = Actor.noSender): Unit

  def pause(): Unit

  def resume()(implicit sender: ActorRef): Unit

  def initialize(linkTag: LinkTag, next: Array[BaseRoutee])(implicit
      ac: ActorContext,
      sender: ActorRef,
      timeout: Timeout,
      ec: ExecutionContext,
      log: LoggingAdapter
  ): Unit = {
    this.tag = linkTag
    next.foreach(x => log.info("link: {}", x))
  }

  def dispose(): Unit

  def reset(): Unit

}
