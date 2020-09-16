package Engine.Operators.Visualization.WordCloud;

import Engine.Common.AmberTag.LayerTag;
import Engine.Common.AmberTuple.Tuple;
import Engine.Common.TupleProcessor;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;

import java.io.StringReader;
import java.util.*;

public class WordCloudLocalTupleProcessor implements TupleProcessor {
    private final int textColumn;
    private final String luceneAnalyzerName;
    private List<String> textList;
    private Iterator<Tuple> iterator = null;

    public WordCloudLocalTupleProcessor(int textColumn, String luceneAnalyzerName) {
        this.textColumn = textColumn;
        this.luceneAnalyzerName = luceneAnalyzerName;
    }

    @Override
    public void accept(Tuple tuple) throws Exception {
        textList.add(tuple.getString(textColumn));
    }

    @Override
    public void onUpstreamChanged(LayerTag from) {

    }

    @Override
    public void onUpstreamExhausted(LayerTag from) {

    }

    @Override
    public void noMore() {
        try {
            List<Tuple> termFrequencyResults = calculateWordCount(textList, luceneAnalyzerName);
            iterator = termFrequencyResults.iterator();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize() throws Exception {
        textList = new ArrayList<>();
    }

    @Override
    public boolean hasNext() throws Exception {
        return iterator != null && iterator.hasNext();
    }

    @Override
    public Tuple next() throws Exception {
        return iterator.next();
    }

    @Override
    public void dispose() throws Exception {

    }

    private static List<Tuple> calculateWordCount(List<String> texts, String analyzerName) throws Exception {
        HashMap<String, Integer> termFreqMap = new HashMap<>();

        for (String text : texts) {
            Analyzer luceneAnalyzer = LuceneAnalyzerConstants.getLuceneAnalyzer(analyzerName);
            TokenStream tokenStream = luceneAnalyzer.tokenStream(null, new StringReader(text));
            OffsetAttribute offsetAttribute = tokenStream.addAttribute(OffsetAttribute.class);

            tokenStream.reset();
            while (tokenStream.incrementToken()) {
                int charStart = offsetAttribute.startOffset();
                int charEnd = offsetAttribute.endOffset();
                String termStr = text.substring(charStart, charEnd).toLowerCase();
                if (!StopAnalyzer.ENGLISH_STOP_WORDS_SET.contains(termStr))
                    termFreqMap.put(termStr, termFreqMap.get(termStr)==null ? 1 : termFreqMap.get(termStr) + 1);
            }
            tokenStream.close();
        }
        List<Tuple> termFreqTuples = new ArrayList<>();

        for (Map.Entry<String, Integer> e : termFreqMap.entrySet()) {
            termFreqTuples.add(Tuple.fromJavaList(Arrays.asList(e.getKey(), e.getValue())));
        }
        return termFreqTuples;
    }
}
