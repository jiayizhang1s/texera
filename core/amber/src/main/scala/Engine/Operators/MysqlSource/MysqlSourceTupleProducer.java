package Engine.Operators.MysqlSource;

import Engine.Common.AmberTuple.Tuple;
import Engine.Common.TupleProducer;
import javafx.util.Pair;

import java.sql.*;
import java.sql.ResultSet;
import java.util.ArrayList;

public class MysqlSourceTupleProducer implements TupleProducer {

    private final String host;
    private final String port;
    private final String database;
    private final String table;
    private final String username;
    private final String password;
    private final Integer limit;
    private final Integer offset;
    private final String column;
    private final String keywords;

    private ArrayList<Pair<String, Integer>>outputSchema = new ArrayList<>();
    private Connection connection;
    private ResultSet rs;
    private boolean querySent = false;
    private boolean hasNext = true;

    MysqlSourceTupleProducer(String host, String port, String database, String table, String username,
                             String password, Integer limit, Integer offset, String column, String keywords){
        this.host = host.trim();
        this.port = port;
        this.database = database.trim();
        this.table = table.trim();
        this.username = username.trim();
        this.password = password;
        this.limit = limit == null ? Integer.MAX_VALUE : limit;
        this.offset = offset == null ? 0 : offset;
        this.column = column == null ? "" : column;
        this.keywords = keywords == null ? "" : keywords;

    }

    public String generateSqlQuery() {
        // in sql prepared statement, table name cannot be inserted using preparedstatement.setString
        // so it has to be inserted here during sql query generation
        String query =  "\n" + "select * from "+ this.table +" where 1 = 1 ";
        // in sql prepared statement, column name cannot be inserted using preparedstatement.setString either
        if(!this.column.isEmpty() && !this.keywords.isEmpty()) {
            query += " AND  MATCH( " + this.column + " )  AGAINST ( ? IN BOOLEAN MODE)";
        }
        if(this.limit != Integer.MAX_VALUE){
            query += " LIMIT ?";
        }
        if(this.offset != 0) {
            if(this.limit == Integer.MAX_VALUE) {
                // if there is no limit, for OFFSET to work, a arbitrary LARGE number
                // need to be manually provided
                query += "limit 999999999999999";
            }
            query += " OFFSET ?";
        }
        query+=";";
        return query;
    }

    @Override
    public void initialize() throws Exception {
        // JDBC connection
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            String url = "jdbc:mysql://" + this.host + ":" + this.port + "/"
                    + this.database + "?autoReconnect=true&useSSL=true";
            this.connection = DriverManager.getConnection(url, this.username, this.password);
            // set to readonly to improve efficiency
            connection.setReadOnly(true);
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            ResultSet columns = databaseMetaData.getColumns(null,null, this.table, null);
            while(columns.next()) {
                String columnName = columns.getString("COLUMN_NAME");
                int datatype = columns.getInt("DATA_TYPE");
                this.outputSchema.add(new Pair(columnName, datatype));
            }
        } catch (SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException | ClassCastException e) {
            throw new Exception("MysqlSource failed to connect to mysql database." + e.getMessage());
        }
        // have acquired table metadata, now send query request
        try {
            if (!querySent) {
                PreparedStatement ps = this.connection.prepareStatement(generateSqlQuery());
                int curIndex = 1;
                if (!this.column.isEmpty() && !this.keywords.isEmpty()) {
                    ps.setString(curIndex, this.keywords);
                    curIndex += 1;
                }
                if (this.limit != Integer.MAX_VALUE) {
                    ps.setObject(curIndex, this.limit, Types.INTEGER);
                    curIndex += 1;
                }
                if (this.offset != 0) {
                    ps.setObject(curIndex, this.offset, Types.INTEGER);
                }
                this.rs = ps.executeQuery();
                querySent = true;
            }
        } catch (Exception e) {
            throw new Exception("MysqlSource failed to connect to mysql database." + e.getMessage());
        }
    }

    @Override
    public boolean hasNext() throws Exception {
        return this.querySent && this.hasNext;
    }

    @Override
    public Tuple next() throws Exception {
        if (this.rs.next()) {
            ArrayList<Object> row = new ArrayList();
            for(Pair<String, Integer> column: this.outputSchema) {
                String columnName = column.getKey();
                int columnType = column.getValue();
                String value = rs.getString(columnName);
                if (value == null) {
                    row.add("");
                    continue;
                }

                switch (columnType) {
                    case Types.SMALLINT: //5 Types.SMALLINT
                    case Types.INTEGER: //4 Types.INTEGER
                    case Types.BINARY: //-2 Types.BINARY
                    case Types.TINYINT: //-6 Types.TINYINT
                        row.add(Integer.valueOf(value));
                        break;
                    case Types.FLOAT: //6 Types.FLOAT
                    case Types.REAL: //7 Types.REAL
                    case Types.DOUBLE: //8 Types.DOUBLE
                    case Types.NUMERIC: //3 Types.NUMERIC
                        row.add(Double.valueOf(value));
                        break;
                    case Types.DATE: //91 Types.DATE
                        row.add(Date.valueOf(value));
                        break;
                    case Types.BOOLEAN: //16 Types.BOOLEAN
                    case Types.BIT: //-7 Types.BIT
                        if (value.equals("0")) {
                            row.add("False");
                        } else {
                            row.add("True");
                        }
                        break;
                    case Types.TIME: //92 Types.TIME
                    case Types.TIMESTAMP:  //93 Types.TIMESTAMP
                    case Types.LONGVARCHAR: //-1 Types.LONGVARCHAR
                    case Types.BIGINT: //-5 Types.BIGINT
                    case Types.CHAR: //1 Types.CHAR
                    case Types.VARCHAR: //12 Types.VARCHAR
                    case Types.NULL: //0 Types.NULL
                    case Types.OTHER: //1111 Types.OTHER
                    default:
                        row.add(value);
                        break;
                }
            }
            try {
                return Tuple.fromJavaList(row);
            } catch (Exception e) {
                throw new Exception("MysqlSource failed to connect to mysql database." + e.getMessage());
            }

        } else {
            this.hasNext = false;
            return null;
        }
    }

    @Override
    public void dispose() throws Exception {
        try {
            connection.close();
        }catch (SQLException e) {
            throw new Exception("Mysql source fail to close. " + e.getMessage());
        }

    }
}
