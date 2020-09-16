package Engine.Operators.Visualization.WordCloud

import Engine.Architecture.Breakpoint.GlobalBreakpoint.GlobalBreakpoint
import Engine.Architecture.DeploySemantics.DeployStrategy.RoundRobinDeployment
import Engine.Architecture.DeploySemantics.DeploymentFilter.{FollowPrevious, UseAll}
import Engine.Architecture.DeploySemantics.Layer.{ActorLayer, ProcessorWorkerLayer}
import Engine.Architecture.LinkSemantics.HashBasedShuffle
import Engine.Architecture.Worker.WorkerState
import Engine.Common.AmberTag.{LayerTag, OperatorTag}
import Engine.Common.Constants
import Engine.Operators.OperatorMetadata
import akka.actor.ActorRef
import akka.event.LoggingAdapter
import akka.util.Timeout

import scala.collection.mutable
import scala.concurrent.ExecutionContext

class WordCloudMetadata (
                          tag: OperatorTag,
                          val numWorkers: Int,
                          val textColumn: Int,
                          val luceneAnalyzerName: String
                        ) extends OperatorMetadata(tag) {

  override lazy val topology: Topology = {
    val partialLayer = new ProcessorWorkerLayer(
      LayerTag(tag, "localPieChartProcessor"),
      _ => new WordCloudLocalTupleProcessor(textColumn, luceneAnalyzerName),
      numWorkers,
      UseAll(),
      RoundRobinDeployment()
    )
    val finalLayer = new ProcessorWorkerLayer(
      LayerTag(tag, "globalPieChartProcessor"),
      _ => new WordCloudGlobalTupleProcessor(),
      1,
      FollowPrevious(),
      RoundRobinDeployment()
    )
    new Topology(
      Array(
        partialLayer,
        finalLayer
      ),
      Array(
        new HashBasedShuffle(
          partialLayer,
          finalLayer,
          Constants.defaultBatchSize,
          x => x.get(0).hashCode()
        )
      ),
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
