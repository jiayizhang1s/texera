/*
 * This file is generated by jOOQ.
 */
package edu.uci.ics.texera.dataflow.jooq.generated.tables;


import edu.uci.ics.texera.dataflow.jooq.generated.Indexes;
import edu.uci.ics.texera.dataflow.jooq.generated.Keys;
import edu.uci.ics.texera.dataflow.jooq.generated.Texera;
import edu.uci.ics.texera.dataflow.jooq.generated.tables.records.UserworkflowRecord;

import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row4;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;
import org.jooq.types.UInteger;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Userworkflow extends TableImpl<UserworkflowRecord> {

    private static final long serialVersionUID = 1872490894;

    /**
     * The reference instance of <code>texera.userworkflow</code>
     */
    public static final Userworkflow USERWORKFLOW = new Userworkflow();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<UserworkflowRecord> getRecordType() {
        return UserworkflowRecord.class;
    }

    /**
     * The column <code>texera.userworkflow.userID</code>.
     */
    public final TableField<UserworkflowRecord, UInteger> USERID = createField(DSL.name("userID"), org.jooq.impl.SQLDataType.INTEGERUNSIGNED.nullable(false), this, "");

    /**
     * The column <code>texera.userworkflow.name</code>.
     */
    public final TableField<UserworkflowRecord, String> NAME = createField(DSL.name("name"), org.jooq.impl.SQLDataType.VARCHAR(128).nullable(false), this, "");

    /**
     * The column <code>texera.userworkflow.workflowID</code>.
     */
    public final TableField<UserworkflowRecord, String> WORKFLOWID = createField(DSL.name("workflowID"), org.jooq.impl.SQLDataType.VARCHAR(128).nullable(false), this, "");

    /**
     * The column <code>texera.userworkflow.workflowBODY</code>.
     */
    public final TableField<UserworkflowRecord, String> WORKFLOWBODY = createField(DSL.name("workflowBODY"), org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * Create a <code>texera.userworkflow</code> table reference
     */
    public Userworkflow() {
        this(DSL.name("userworkflow"), null);
    }

    /**
     * Create an aliased <code>texera.userworkflow</code> table reference
     */
    public Userworkflow(String alias) {
        this(DSL.name(alias), USERWORKFLOW);
    }

    /**
     * Create an aliased <code>texera.userworkflow</code> table reference
     */
    public Userworkflow(Name alias) {
        this(alias, USERWORKFLOW);
    }

    private Userworkflow(Name alias, Table<UserworkflowRecord> aliased) {
        this(alias, aliased, null);
    }

    private Userworkflow(Name alias, Table<UserworkflowRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""));
    }

    public <O extends Record> Userworkflow(Table<O> child, ForeignKey<O, UserworkflowRecord> key) {
        super(child, key, USERWORKFLOW);
    }

    @Override
    public Schema getSchema() {
        return Texera.TEXERA;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.USERWORKFLOW_PRIMARY, Indexes.USERWORKFLOW_USERID);
    }

    @Override
    public UniqueKey<UserworkflowRecord> getPrimaryKey() {
        return Keys.KEY_USERWORKFLOW_PRIMARY;
    }

    @Override
    public List<UniqueKey<UserworkflowRecord>> getKeys() {
        return Arrays.<UniqueKey<UserworkflowRecord>>asList(Keys.KEY_USERWORKFLOW_USERID, Keys.KEY_USERWORKFLOW_PRIMARY);
    }

    @Override
    public List<ForeignKey<UserworkflowRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<UserworkflowRecord, ?>>asList(Keys.USERWORKFLOW_IBFK_1);
    }

    public Useraccount useraccount() {
        return new Useraccount(this, Keys.USERWORKFLOW_IBFK_1);
    }

    @Override
    public Userworkflow as(String alias) {
        return new Userworkflow(DSL.name(alias), this);
    }

    @Override
    public Userworkflow as(Name alias) {
        return new Userworkflow(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Userworkflow rename(String name) {
        return new Userworkflow(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Userworkflow rename(Name name) {
        return new Userworkflow(name, null);
    }

    // -------------------------------------------------------------------------
    // Row4 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row4<UInteger, String, String, String> fieldsRow() {
        return (Row4) super.fieldsRow();
    }
}
