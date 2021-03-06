package edu.uci.ics.texera.workflow.operators.aggregate

import java.io.Serializable

import com.fasterxml.jackson.annotation.{JsonProperty, JsonPropertyDescription}
import edu.uci.ics.texera.workflow.common.metadata.{OperatorGroupConstants, OperatorInfo}
import edu.uci.ics.texera.workflow.common.operators.aggregate.{AggregateOpDesc, AggregateOpExecConfig, DistributedAggregation}
import edu.uci.ics.texera.workflow.common.tuple.Tuple
import edu.uci.ics.texera.workflow.common.tuple.schema.{AttributeType, Schema}

case class SumPartialObj(sum :Double, count: Double) extends Serializable {}
class SumOpDesc extends AggregateOpDesc {
  @JsonProperty(value = "attribute", required = true)
  @JsonPropertyDescription("column to calculate sum")
  var attribute: String = _

  @JsonProperty(value = "result attribute", required = true)
  @JsonPropertyDescription("column name of sum result")
  var resultAttribute: String = _

  @JsonProperty("group by keys")
  @JsonPropertyDescription("group by columns")
  var groupByKeys: List[String] = _

  override def operatorExecutor: AggregateOpExecConfig[SumPartialObj] = {
    val aggregation = new DistributedAggregation[SumPartialObj](
      () => SumPartialObj(0,0),
      (partial, tuple) => {
        val value = tuple.getField(attribute).toString.toDouble
        SumPartialObj(partial.sum+value,partial.count + 1)
      },
      (partial1, partial2) =>
        SumPartialObj(partial1.sum + partial2.sum,partial1.count + partial2.count),
      partial => {
        val value =  if (partial.count == 0) null else partial.sum
        Tuple.newBuilder.add(resultAttribute, AttributeType.DOUBLE, value).build
      },
      if (this.groupByKeys == null) null
      else
        tuple => {
          val builder = Tuple.newBuilder()
          groupByKeys.foreach(key =>
            builder.add(tuple.getSchema.getAttribute(key), tuple.getField(key))
          )
          builder.build()
        }
    )
    new AggregateOpExecConfig[SumPartialObj](
      operatorIdentifier,
      aggregation
    )
  }

  override def operatorInfo: OperatorInfo =
    OperatorInfo(
      "Sum",
      "calculate the Sum value of a column",
      OperatorGroupConstants.UTILITY_GROUP,
      1,
      1
    )

  override def getOutputSchema(schemas: Array[Schema]): Schema = {
    if (resultAttribute == null || resultAttribute.trim.isEmpty) {
      return null
    }
    if (groupByKeys == null) {
      groupByKeys = List()
    }
    Schema
      .newBuilder()
      .add(
        groupByKeys.map(key => schemas(0).getAttribute(key)).toArray: _*
      )
      .add(resultAttribute, AttributeType.DOUBLE)
      .build()
  }
}
