package texera.operators.visualization.wordCloud;

import Engine.Common.Constants;
import Engine.Operators.OperatorMetadata;
import Engine.Operators.Visualization.WordCloud.WordCloudMetadata;
import com.fasterxml.jackson.annotation.JsonProperty;
import texera.common.schema.OperatorGroupConstants;
import texera.common.schema.TexeraOperatorDescription;
import texera.operators.visualization.VisualizationConstants;
import texera.operators.visualization.VisualizationOperator;

public class TexeraWordCloud extends VisualizationOperator {
    @JsonProperty("text column")
    public String textColumn;

    @JsonProperty("lucene analyzer name")
    public String luceneAnalyzerName;

    @Override
    public OperatorMetadata amberOperator() {
        if (textColumn == null) {
            throw new RuntimeException("word cloud: text column is null");
        }
        if (luceneAnalyzerName == null) {
            throw new RuntimeException("word cloud: text column is null");
        }
        int textColumnIndex = this.context().fieldIndexMapping(textColumn);
        return new WordCloudMetadata(this.amberOperatorTag(), Constants.defaultNumWorkers(),
                textColumnIndex, luceneAnalyzerName);
    }

    @Override
    public TexeraOperatorDescription texeraOperatorDescription() {
        return new TexeraOperatorDescription(
                "Word Cloud",
                "Generate word cloud for result texts",
                OperatorGroupConstants.VISUALIZATION_GROUP(),
                1, 1);
    }

    @Override
    public String chartType() {
        return VisualizationConstants.WORD_CLOUD;
    }
}
