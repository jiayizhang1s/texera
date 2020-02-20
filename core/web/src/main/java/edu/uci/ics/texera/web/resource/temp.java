package edu.uci.ics.texera.web.resource;

import static edu.uci.ics.texera.web.resource.generated.Tables.USERFILE;
import static org.jooq.impl.DSL.defaultValue;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Record3;
import org.jooq.Result;

import static edu.uci.ics.texera.web.resource.generated.Tables.*;
import static org.jooq.impl.DSL.*;

import edu.uci.ics.texera.dataflow.resource.dictionary.DictionaryManager;
import edu.uci.ics.texera.dataflow.resource.file.FileManager;
import edu.uci.ics.texera.web.TexeraWebException;
import edu.uci.ics.texera.web.resource.UserDictionaryResource.UserDictionary;
import edu.uci.ics.texera.web.resource.UserFileResource.UserFile;
import edu.uci.ics.texera.web.response.GenericWebResponse;


@Path("/users/dictionaries/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class temp {
    public static class UserDictionary {
        public double id;
        public String name;
        public List<String> items;
        public String description;

        public UserDictionary(double id, String name, List<String> items, String description) {
            this.id = id;
            this.name = name;
            this.items = items;
            this.description = description;
        }
    }
        
    public static class UserManualDictionary {
        public String name;
        public String content;
        public String separator;
        public String description;

        public UserManualDictionary() { }
        
        public String getStringItems() {
            return Stream.of(
                        content.trim().split(separator)
                    )
                    .distinct()
                    .collect(Collectors.joining(","));
        }
        
        public boolean isValid() {
            return name != null && name.length() != 0 &&
                    content != null && content.length() != 0 &&
                    separator != null && description != null;
        }
    }
    
    @PUT
    @Path("/upload-manual-dict/{userID}")
    public GenericWebResponse putManualDictionary(
            @PathParam("userID") String userID,
            UserManualDictionary userManualDictionary
    ) {
        if (userManualDictionary == null || !userManualDictionary.isValid()) {
            throw new TexeraWebException("Error occurred in user manual dictionary");
        }
        double userIDDouble = parseStringToDouble(userID);
        
        int result = insertDictionaryToDataBase(
                userManualDictionary.name, 
                userManualDictionary.getStringItems(),
                userManualDictionary.description,
                userIDDouble);
        
        if (result == 0) {
            throw new TexeraWebException("Error occurred while inserting dictionary to database");
        }
        
        return new GenericWebResponse(0, "success");
    }
    
    @GET
    @Path("/get-dictionary/{userID}")
    public List<UserDictionary> getDictionary(
            @PathParam("userID") String userID
    ) {
        double userIDDouble = parseStringToDouble(userID);
        
        Result<Record3<Double, String, String>> result = getUserDictionaryRecord(userIDDouble);
        
        if (result == null) return new ArrayList<>();
        
        List<UserDictionary> dictionaryList = result.stream()
                .map(
                    record -> new UserDictionary(
                            record.get(USERDICT.DICTID),
                            record.get(USERDICT.DICTNAME),
                            convertContentToList(record.get(USERDICT.DICTCONTENT)),
                            "" // TODO get description
                            )
                        ).collect(Collectors.toList());
        
        return dictionaryList;
    }
    
    @DELETE
    @Path("/delete-dictionary/{dictID}")
    public GenericWebResponse deleteDictionary(
            @PathParam("dictID") String dictID
    ) {
        double dictIDDouble = parseStringToDouble(dictID);
        
        int count = deleteInDatabase(dictIDDouble);
        if (count == 0) throw new TexeraWebException("delete dictionary " + dictIDDouble + " failed in database");
        
        return new GenericWebResponse(0, "success");
    }
    
    private int deleteInDatabase(double dictID) {
        // Connection is AutoCloseable so it will automatically close when it finishes.
        try (Connection conn = UserMysqlServer.getConnection()) {
            DSLContext create = UserMysqlServer.createDSLContext(conn);
            
            int count = create
                    .delete(USERDICT)
                    .where(USERDICT.DICTID.eq(dictID))
                    .execute();
            
            return count;
            
        } catch (Exception e) {
            throw new TexeraWebException(e);
        }
    }
    
    private List<String>convertContentToList(String content) {
        return Arrays.asList(content.split("\\s*,\\s*"));
    }
    
    private Result<Record3<Double, String, String>> getUserDictionaryRecord(double userID) {
        // Connection is AutoCloseable so it will automatically close when it finishes.
        try (Connection conn = UserMysqlServer.getConnection()) {
            DSLContext create = UserMysqlServer.createDSLContext(conn);
            
            Result<Record3<Double, String, String>> result = create
                    .select(USERDICT.DICTID, USERDICT.DICTNAME, USERDICT.DICTCONTENT) // TODO description
                    .from(USERDICT)
                    .where(USERDICT.USERID.equal(userID))
                    .fetch();
            
            return result;
            
        } catch (Exception e) {
            throw new TexeraWebException(e);
        }
    }
    
    private int insertDictionaryToDataBase(String name, String content, String description, double userID) {
        // Connection is AutoCloseable so it will automatically close when it finishes.
        try (Connection conn = UserMysqlServer.getConnection()) {
            DSLContext create = UserMysqlServer.createDSLContext(conn);
            
            int result = create.insertInto(USERDICT)
                    .set(USERDICT.USERID,userID)
                    .set(USERDICT.DICTID, defaultValue(USERDICT.DICTID))
                    .set(USERDICT.DICTNAME, name)
                    .set(USERDICT.DICTCONTENT, content)
                    //.set(USERDICT.DESCRIPTION, description)
                    .execute();
            
            return result;
            
        } catch (Exception e) {
            throw new TexeraWebException(e);
        }
    }
    
    private double parseStringToDouble(String userID) throws TexeraWebException {
        try {
            return Double.parseDouble(userID);
        } catch (NumberFormatException e) {
            throw new TexeraWebException("Incorrect String to double");
        }
    }
}
