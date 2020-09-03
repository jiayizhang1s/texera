package Engine.Operators.MysqlSource

import Engine.Architecture.Breakpoint.GlobalBreakpoint.GlobalBreakpoint
import Engine.Architecture.DeploySemantics.DeployStrategy.{OneOnEach}
import Engine.Architecture.DeploySemantics.DeploymentFilter.UseAll
import Engine.Architecture.DeploySemantics.Layer.{ActorLayer, GeneratorWorkerLayer}
import Engine.Architecture.Worker.WorkerState
import Engine.Common.AmberTag.{LayerTag, OperatorTag}
import Engine.Operators.OperatorMetadata
import akka.actor.ActorRef
import akka.event.LoggingAdapter
import akka.util.Timeout

import scala.collection.mutable
import scala.concurrent.ExecutionContext

class MysqlSourceMetadata(
    tag: OperatorTag,
    numWorkers: Int,
    host: String,
    port: String,
    database: String,
    table: String,
    username: String,
    password: String,
    limit: Integer,
    offset: Integer,
    column: String,
    keywords: String
) extends OperatorMetadata(tag){
  override lazy val topology: Topology = {
    new Topology(
      Array(
        new GeneratorWorkerLayer(
          LayerTag(tag, "main"),
          _ => {
            new MysqlSourceTupleProducer(
              host,
              port,
              database,
              table,
              username,
              password,
              limit,
              offset,
              column,
              keywords
            )
          },
          numWorkers,
          UseAll(), // it's source operator
          OneOnEach()
        )
      ),
      Array(),
      Map()
    )
  }

  override def assignBreakpoint(
      topology: Array[ActorLayer],
      states: mutable.AnyRefMap[ActorRef, WorkerState.Value],
      breakpoint: GlobalBreakpoint
  )(implicit timeout: Timeout, ec: ExecutionContext, log: LoggingAdapter): Unit = {
    breakpoint.partition(topology(0).layer.filter(states(_) != WorkerState.Completed))
  }
}
