package edu.uci.ics.texera.web.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.Select;
import org.jooq.impl.DSL;

import edu.uci.ics.texera.web.TexeraWebException;
import edu.uci.ics.texera.web.resource.generated.tables.records.UseraccountRecord;
import edu.uci.ics.texera.web.response.GenericWebResponse;

import static edu.uci.ics.texera.web.resource.generated.Tables.*;
import static org.jooq.impl.DSL.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;


@Path("/users/accounts/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserAccountResource {
    private final static String serverName = TexeraMysqlServerInfo.getServername();
    private final static String password = TexeraMysqlServerInfo.getPassword();
    private final static String url = TexeraMysqlServerInfo.getUrl();
    
    
    /**
     * Corresponds to `src/app/dashboard/type/user-account.ts`
     */
    public static class UserAccount {
        public String userName;
        public double userID;
        
        public static UserAccount generateErrorAccount() {
            return new UserAccount("", -1);
        }

        public UserAccount(String userName, double userID) {
            this.userName = userName;
            this.userID = userID;
        }
    }
    
    /**
     * Corresponds to `src/app/dashboard/type/user-account.ts`
     */
    public static class UserAccountResponse {
        public int code; // 0 represents success and 1 represents error
        public UserAccount userAccount;
        public String message;
        
        public static UserAccountResponse generateErrorResponse(String message) {
            return new UserAccountResponse(1, UserAccount.generateErrorAccount(), message);
        }
        
        public static UserAccountResponse generateSuccessResponse(UserAccount userAccount) {
            return new UserAccountResponse(0, userAccount, "");
        }

        public UserAccountResponse(int code, UserAccount userAccount, String message) {
            this.code = code;
            this.userAccount = userAccount;
            this.message = message;
        }
    }
    
    @POST
    @Path("/login")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public UserAccountResponse login(@FormDataParam("userName") String userName) {
        if (!checkUserInfoValid(userName)) {
            return UserAccountResponse.generateErrorResponse("The username or password is incorrect");
        }

        Condition loginCondition = USERACCOUNT.USERNAME.equal(userName); // TODO compare password
        Record1<Integer> result = getUserID(loginCondition);

        if (result == null) { // not found
            return UserAccountResponse.generateErrorResponse("The username or password is incorrect");
        } else {
            UserAccount account = new UserAccount(
                        userName,
                        result.get(USERACCOUNT.USERID));
            UserAccountResponse response = UserAccountResponse.generateSuccessResponse(account);
            return response;
        }

    }
    
    @POST
    @Path("/register")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public UserAccountResponse register(@FormDataParam("userName") String userName) {
        if (!checkUserInfoValid(userName)) {
            return UserAccountResponse.generateErrorResponse("The username or password is incorrect");
        }
        
        Condition registerCondition = USERACCOUNT.USERNAME.equal(userName);
        Record1<Integer> result = getUserID(registerCondition);
        
        if (result == null) { // not found and register is allowed, potential problem for concurrency
            UseraccountRecord returnID = insertUserAccount(userName);
            UserAccount account = new UserAccount(
                    userName,
                    returnID.get(USERACCOUNT.USERID));
            UserAccountResponse response = UserAccountResponse.generateSuccessResponse(account);
            return response;
        } else {
            return UserAccountResponse.generateErrorResponse("Username already exists");
        }
    }
    
    private Record1<Integer> getUserID(Condition condition) {
        try (Connection conn = DriverManager.getConnection(url, serverName, password)) {
            DSLContext create = DSL.using(conn, SQLDialect.MYSQL);
            Record1<Integer> result = create
                    .select(USERACCOUNT.USERID)
                    .from(USERACCOUNT)
                    .where(condition)
                    .fetchOne();
            return result;
        } catch (Exception e) {
            throw new TexeraWebException(e);
        }
    }
    
    private UseraccountRecord insertUserAccount(String userName) {
        try (Connection conn = DriverManager.getConnection(url, serverName, password)) {
            DSLContext create = DSL.using(conn, SQLDialect.MYSQL);
            
            UseraccountRecord result = create.insertInto(USERACCOUNT)
                    .set(USERACCOUNT.USERNAME, userName)
                    .set(USERACCOUNT.USERID, defaultValue(USERACCOUNT.USERID))
                    .returning(USERACCOUNT.USERID)
                    .fetchOne();
            
            return result;
            
        } catch (Exception e) {
            throw new TexeraWebException(e);
        }
    }
    
    private boolean checkUserInfoValid(String userName) {
        return userName != null && 
                userName.length() > 0;
    }
    
}
