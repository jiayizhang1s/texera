package texera.operators.mysqlsource;

import Engine.Operators.MysqlSource.MysqlSourceMetadata;
import Engine.Operators.OperatorMetadata;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import texera.common.schema.OperatorGroupConstants;
import texera.common.schema.TexeraOperatorDescription;
import texera.common.workflow.TexeraOperator;

public class TexeraMysqlSource extends TexeraOperator {
    @JsonProperty("host")
    @JsonPropertyDescription("mysql host IP address")
    public String host;

    @JsonProperty("port")
    @JsonPropertyDescription("mysql host port")
    public String port;

    @JsonProperty("database")
    @JsonPropertyDescription("mysql database name")
    public String database;

    @JsonProperty("table")
    @JsonPropertyDescription("mysql table name")
    public String table;

    @JsonProperty("username")
    @JsonPropertyDescription("mysql username")
    public String username;

    @JsonProperty("password")
    @JsonPropertyDescription("mysql user password")
    public String password;

    @JsonProperty("limit")
    @JsonPropertyDescription("query result count upper limit")
    public Integer limit;

    @JsonProperty("offset")
    @JsonPropertyDescription("query offset")
    public Integer offset;

    @JsonProperty("column name")
    @JsonPropertyDescription("the column to be keyword-searched")
    public String column;

    @JsonProperty("keywords")
    @JsonPropertyDescription("search terms in boolean expression")
    public String keywords;

    @Override
    public OperatorMetadata amberOperator() {
        return new MysqlSourceMetadata(this.amberOperatorTag(), 1,
                host, port, database, table, username, password, limit, offset, column, keywords);
    }

    @Override
    public TexeraOperatorDescription texeraOperatorDescription() {
        return new TexeraOperatorDescription(
                "Mysql Source",
                "Read data from a mysql instance",
                OperatorGroupConstants.SOURCE_GROUP(),
                0, 1);
    }
}
