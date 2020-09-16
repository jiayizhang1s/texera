package Engine.Operators.Visualization.WordCloud;

import Engine.Common.AmberTag.LayerTag;
import Engine.Common.AmberTuple.Tuple;
import Engine.Common.TupleProcessor;

import java.util.*;

public class WordCloudGlobalTupleProcessor implements TupleProcessor {
    private final int MAX_FONT_SIZE = 200;
    private final int MIN_FONT_SIZE = 50;
    private HashMap<String, Integer> termFreqMap;
    private Iterator<Tuple> iterator = null;

    @Override
    public void accept(Tuple tuple) throws Exception {
        String term = tuple.getString(0);
        int frequency = tuple.getInt(1);
        termFreqMap.put(term, termFreqMap.get(term)==null ? frequency : termFreqMap.get(term) + frequency);
    }

    @Override
    public void onUpstreamChanged(LayerTag from) {

    }

    @Override
    public void onUpstreamExhausted(LayerTag from) {

    }

    @Override
    public void noMore() {
        double minValue = Double.MAX_VALUE;
        double maxValue = Double.MIN_VALUE;

        for (Map.Entry<String, Integer> e : termFreqMap.entrySet()) {
            int frequency = e.getValue();
            minValue = Math.min(minValue, frequency);
            maxValue = Math.max(maxValue, frequency);
        }

        // normalize the font size for wordcloud js
        // https://github.com/timdream/wordcloud2.js/issues/53
        List<Tuple> termFreqTuples = new ArrayList<>();
        for (Map.Entry<String, Integer> e : termFreqMap.entrySet()) {
            termFreqTuples.add(Tuple.fromJavaList(
                    Arrays.asList(
                            e.getKey(),
                            (int) ((e.getValue() - minValue) / (maxValue - minValue) *
                                    (this.MAX_FONT_SIZE - this.MIN_FONT_SIZE) + this.MIN_FONT_SIZE)
                    )
            ));
        }
        iterator = termFreqTuples.iterator();
    }

    @Override
    public void initialize() throws Exception {
        this.termFreqMap = new HashMap<>();
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
        termFreqMap = null;
    }
}
