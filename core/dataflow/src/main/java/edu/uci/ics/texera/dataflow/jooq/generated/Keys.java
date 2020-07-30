/*
 * This file is generated by jOOQ.
 */
package edu.uci.ics.texera.dataflow.jooq.generated;


import edu.uci.ics.texera.dataflow.jooq.generated.tables.Useraccount;
import edu.uci.ics.texera.dataflow.jooq.generated.tables.Userdict;
import edu.uci.ics.texera.dataflow.jooq.generated.tables.Userfile;
import edu.uci.ics.texera.dataflow.jooq.generated.tables.Userworkflow;
import edu.uci.ics.texera.dataflow.jooq.generated.tables.records.UseraccountRecord;
import edu.uci.ics.texera.dataflow.jooq.generated.tables.records.UserdictRecord;
import edu.uci.ics.texera.dataflow.jooq.generated.tables.records.UserfileRecord;
import edu.uci.ics.texera.dataflow.jooq.generated.tables.records.UserworkflowRecord;

import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.UniqueKey;
import org.jooq.impl.Internal;
import org.jooq.types.UInteger;


/**
 * A class modelling foreign key relationships and constraints of tables of 
 * the <code>texera</code> schema.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Keys {

    // -------------------------------------------------------------------------
    // IDENTITY definitions
    // -------------------------------------------------------------------------

    public static final Identity<UseraccountRecord, UInteger> IDENTITY_USERACCOUNT = Identities0.IDENTITY_USERACCOUNT;
    public static final Identity<UserdictRecord, UInteger> IDENTITY_USERDICT = Identities0.IDENTITY_USERDICT;
    public static final Identity<UserfileRecord, UInteger> IDENTITY_USERFILE = Identities0.IDENTITY_USERFILE;

    // -------------------------------------------------------------------------
    // UNIQUE and PRIMARY KEY definitions
    // -------------------------------------------------------------------------

    public static final UniqueKey<UseraccountRecord> KEY_USERACCOUNT_USERNAME = UniqueKeys0.KEY_USERACCOUNT_USERNAME;
    public static final UniqueKey<UseraccountRecord> KEY_USERACCOUNT_PRIMARY = UniqueKeys0.KEY_USERACCOUNT_PRIMARY;
    public static final UniqueKey<UserdictRecord> KEY_USERDICT_USERID = UniqueKeys0.KEY_USERDICT_USERID;
    public static final UniqueKey<UserdictRecord> KEY_USERDICT_PRIMARY = UniqueKeys0.KEY_USERDICT_PRIMARY;
    public static final UniqueKey<UserfileRecord> KEY_USERFILE_USERID = UniqueKeys0.KEY_USERFILE_USERID;
    public static final UniqueKey<UserfileRecord> KEY_USERFILE_PRIMARY = UniqueKeys0.KEY_USERFILE_PRIMARY;
    public static final UniqueKey<UserworkflowRecord> KEY_USERWORKFLOW_USERID = UniqueKeys0.KEY_USERWORKFLOW_USERID;
    public static final UniqueKey<UserworkflowRecord> KEY_USERWORKFLOW_PRIMARY = UniqueKeys0.KEY_USERWORKFLOW_PRIMARY;

    // -------------------------------------------------------------------------
    // FOREIGN KEY definitions
    // -------------------------------------------------------------------------

    public static final ForeignKey<UserdictRecord, UseraccountRecord> USERDICT_IBFK_1 = ForeignKeys0.USERDICT_IBFK_1;
    public static final ForeignKey<UserfileRecord, UseraccountRecord> USERFILE_IBFK_1 = ForeignKeys0.USERFILE_IBFK_1;
    public static final ForeignKey<UserworkflowRecord, UseraccountRecord> USERWORKFLOW_IBFK_1 = ForeignKeys0.USERWORKFLOW_IBFK_1;

    // -------------------------------------------------------------------------
    // [#1459] distribute members to avoid static initialisers > 64kb
    // -------------------------------------------------------------------------

    private static class Identities0 {
        public static Identity<UseraccountRecord, UInteger> IDENTITY_USERACCOUNT = Internal.createIdentity(Useraccount.USERACCOUNT, Useraccount.USERACCOUNT.USERID);
        public static Identity<UserdictRecord, UInteger> IDENTITY_USERDICT = Internal.createIdentity(Userdict.USERDICT, Userdict.USERDICT.DICTID);
        public static Identity<UserfileRecord, UInteger> IDENTITY_USERFILE = Internal.createIdentity(Userfile.USERFILE, Userfile.USERFILE.FILEID);
    }

    private static class UniqueKeys0 {
        public static final UniqueKey<UseraccountRecord> KEY_USERACCOUNT_USERNAME = Internal.createUniqueKey(Useraccount.USERACCOUNT, "KEY_useraccount_userName", Useraccount.USERACCOUNT.USERNAME);
        public static final UniqueKey<UseraccountRecord> KEY_USERACCOUNT_PRIMARY = Internal.createUniqueKey(Useraccount.USERACCOUNT, "KEY_useraccount_PRIMARY", Useraccount.USERACCOUNT.USERID);
        public static final UniqueKey<UserdictRecord> KEY_USERDICT_USERID = Internal.createUniqueKey(Userdict.USERDICT, "KEY_userdict_userID", Userdict.USERDICT.USERID, Userdict.USERDICT.NAME);
        public static final UniqueKey<UserdictRecord> KEY_USERDICT_PRIMARY = Internal.createUniqueKey(Userdict.USERDICT, "KEY_userdict_PRIMARY", Userdict.USERDICT.DICTID);
        public static final UniqueKey<UserfileRecord> KEY_USERFILE_USERID = Internal.createUniqueKey(Userfile.USERFILE, "KEY_userfile_userID", Userfile.USERFILE.USERID, Userfile.USERFILE.NAME);
        public static final UniqueKey<UserfileRecord> KEY_USERFILE_PRIMARY = Internal.createUniqueKey(Userfile.USERFILE, "KEY_userfile_PRIMARY", Userfile.USERFILE.FILEID);
        public static final UniqueKey<UserworkflowRecord> KEY_USERWORKFLOW_USERID = Internal.createUniqueKey(Userworkflow.USERWORKFLOW, "KEY_userworkflow_userID", Userworkflow.USERWORKFLOW.USERID, Userworkflow.USERWORKFLOW.NAME);
        public static final UniqueKey<UserworkflowRecord> KEY_USERWORKFLOW_PRIMARY = Internal.createUniqueKey(Userworkflow.USERWORKFLOW, "KEY_userworkflow_PRIMARY", Userworkflow.USERWORKFLOW.WORKFLOWID);
    }

    private static class ForeignKeys0 {
        public static final ForeignKey<UserdictRecord, UseraccountRecord> USERDICT_IBFK_1 = Internal.createForeignKey(edu.uci.ics.texera.dataflow.jooq.generated.Keys.KEY_USERACCOUNT_PRIMARY, Userdict.USERDICT, "userdict_ibfk_1", Userdict.USERDICT.USERID);
        public static final ForeignKey<UserfileRecord, UseraccountRecord> USERFILE_IBFK_1 = Internal.createForeignKey(edu.uci.ics.texera.dataflow.jooq.generated.Keys.KEY_USERACCOUNT_PRIMARY, Userfile.USERFILE, "userfile_ibfk_1", Userfile.USERFILE.USERID);
        public static final ForeignKey<UserworkflowRecord, UseraccountRecord> USERWORKFLOW_IBFK_1 = Internal.createForeignKey(edu.uci.ics.texera.dataflow.jooq.generated.Keys.KEY_USERACCOUNT_PRIMARY, Userworkflow.USERWORKFLOW, "userworkflow_ibfk_1", Userworkflow.USERWORKFLOW.USERID);
    }
}
