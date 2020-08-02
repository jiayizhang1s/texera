package edu.uci.ics.texera.dataflow.sink.piechart;

import edu.uci.ics.texera.api.constants.ErrorMessages;
import edu.uci.ics.texera.api.dataflow.IOperator;
import edu.uci.ics.texera.api.exception.DataflowException;
import edu.uci.ics.texera.api.exception.TexeraException;
import edu.uci.ics.texera.api.field.DoubleField;
import edu.uci.ics.texera.api.field.IField;
import edu.uci.ics.texera.api.field.IntegerField;
import edu.uci.ics.texera.api.field.StringField;
import edu.uci.ics.texera.api.field.TextField;
import edu.uci.ics.texera.api.schema.Attribute;
import edu.uci.ics.texera.api.schema.AttributeType;
import edu.uci.ics.texera.api.schema.Schema;
import edu.uci.ics.texera.api.tuple.Tuple;
import edu.uci.ics.texera.dataflow.sink.VisualizationOperator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
/**
 * PieChartSink is a sink that can be used by to get tuples for pie chart.
 * PieChartSink returns tuples with name of data (String) and a number (Integer or Double).
 * @author Mingji Han
 *
 */
public class PieChartSink extends VisualizationOperator {

    private PieChartSinkPredicate predicate;


    public PieChartSink(PieChartSinkPredicate predicate) {
        super(predicate.getPieChartEnum().getChartStyle());
        this.predicate = predicate;

    }

    @Override
    public void open() throws TexeraException {
        if (cursor != CLOSED) {
            return;
        }
        if (inputOperator == null) {
            throw new TexeraException(ErrorMessages.INPUT_OPERATOR_NOT_SPECIFIED);
        }
        inputOperator.open();

        Schema schema = inputOperator.getOutputSchema();

        Attribute nameColumn =  schema.getAttribute(predicate.getNameColumn());
        AttributeType nameColumnType = nameColumn.getType();
        if (!nameColumnType.equals(AttributeType.STRING) && !nameColumnType.equals(AttributeType.TEXT)) {
            throw new DataflowException("Type of name column should be string or text.");
        }

        Attribute dataColumn =  schema.getAttribute(predicate.getDataColumn());
        AttributeType dataColumnType = dataColumn.getType();
        if (!dataColumnType.equals(AttributeType.DOUBLE) && !dataColumnType.equals(AttributeType.INTEGER)) {
            throw new DataflowException(("Type of data column should be integer or double."));
        }

        Double ratio = predicate.getPruneRatio();
        if ( ratio < 0 || ratio > 1) {
            throw new DataflowException("Ratio should be in (0, 1).");
        }

        outputSchema = new Schema.Builder().add(nameColumn, dataColumn).build();
        cursor = OPENED;
    }

    public void setInputOperator(IOperator inputOperator) {
        if (cursor != CLOSED) {
            throw new TexeraException(ErrorMessages.INPUT_OPERATOR_CHANGED_AFTER_OPEN);
        }
        this.inputOperator = inputOperator;
    }


    @Override
    public Schema transformToOutputSchema(Schema... inputSchema) {
        throw new TexeraException(ErrorMessages.INVALID_OUTPUT_SCHEMA_FOR_SINK);
    }

    @Override
    public void processTuples() throws TexeraException {
        List<Tuple> list = new ArrayList<>();

        Tuple tuple;

        while ( (tuple = inputOperator.getNextTuple()) != null) {
            list.add(tuple);
        }
        list = list.stream()
            .map(e -> new Tuple(outputSchema, e.getField(predicate.getNameColumn()), e.getField(predicate.getDataColumn())))
            .collect(Collectors.toList());
        // sort all tuples in the descending order

        // calculate sum of data column
        double sum = 0.0;
        HashMap<String, Double> tupleMap = new HashMap<>();
        for (Tuple t: list) {
            IField field = t.getField(predicate.getNameColumn());
            double value = VisualizationOperator.extractNumber(t.getField(predicate.getDataColumn()));
            String name = null;
            if (field instanceof StringField) {
                StringField stringField = (StringField)field;
                name = stringField.getValue();
            } else if (field instanceof  TextField) {
                TextField textField = (TextField)field;
                name = textField.getValue();
            }
            if (tupleMap.get(name) == null) {
                tupleMap.put(name, value);
            } else {
                tupleMap.put(name, tupleMap.get(name) + value);
            }
            sum += value;
        }

        List<Map.Entry<String, Double>> tupleList = new ArrayList<>(tupleMap.entrySet());

        tupleList.sort((e1, e2) -> {
            double leftValue = e1.getValue();
            double rightValue = e2.getValue();
            return Double.compare(rightValue, leftValue);
        });
        // process the sorted rows, if the cumulative sum is greater than ratio * sum.
        // stop adding tuples, add new row called "Other" instead.
        double total = 0.0;
        for (Map.Entry<String, Double> t: tupleList) {
            total += t.getValue();
            result.add(new Tuple(outputSchema, buildNameField(t.getKey()), buildDataField(t.getValue())));

            if (total / sum > predicate.getPruneRatio()) {
                IField nameField =  buildNameField("Other");
                IField dataField =  buildDataField(sum - total);
                result.add(new Tuple(outputSchema, nameField, dataField));
                return;
            }
        }



    }
    private IField buildNameField(String string) {


        Attribute nameColumn =   inputOperator.getOutputSchema().getAttribute(predicate.getNameColumn());
        AttributeType nameColumnType = nameColumn.getType();
        if (nameColumnType.equals(AttributeType.STRING)) {
            return new StringField(string);
        }
        return new TextField(string);
    }

    private IField buildDataField(double value) {
        Attribute dataColumn =   inputOperator.getOutputSchema().getAttribute(predicate.getDataColumn());
        AttributeType  dataColumnType = dataColumn.getType();

        if (dataColumnType.equals(AttributeType.INTEGER)) {
            return new IntegerField((int)value);
        }
        return new DoubleField(value);

    }


}