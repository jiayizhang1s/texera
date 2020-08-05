package edu.uci.ics.texera.web.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import edu.uci.ics.texera.dataflow.sqlServerInfo.UserSqlServer;
import edu.uci.ics.texera.web.TexeraWebException;
import edu.uci.ics.texera.web.response.GenericWebResponse;
import io.dropwizard.jersey.sessions.Session;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.jooq.*;
import org.jooq.types.UInteger;

import javax.servlet.http.HttpSession;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static edu.uci.ics.texera.dataflow.jooq.generated.Tables.USERACCOUNT;
import static edu.uci.ics.texera.dataflow.jooq.generated.Tables.USERWORKFLOW;

/**
 * This file handles various request related to saved-workflows.
 * It sends mysql queries to the MysqlDB regarding the UserWorkflow Table
 * The details of UserWorkflowTable can be found in /core/scripts/sql/texera_ddl.sql
 */

// uncomment and use below to give workflows the concept of ownership
// @Path("/user/workflow")
@Path("/workflow")
@Produces(MediaType.APPLICATION_JSON)
public class UserWorkflowResource {

    /**
     * Corresponds to interface SavedWorkflow in `src/app/workspace/service/save-workflow/save-workflow.service.ts`
     */
    public static class UserWorkflow {
        public String workflowID;
        public String workflowName;
        public ObjectNode workflowBody;

        public UserWorkflow(String id, String name, ObjectNode body) {
            this.workflowID = id;
            this.workflowName = name;
            this.workflowBody = body;
        }
    }

    public static class UserWorkflowListRequest {
        public String username;
    }

    public static class UserWorkflowDescription {
        public String id;
        public String name;

        public UserWorkflowDescription(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    public static class UserWorkflowAddRequest {
        public String username;
        public String workflowID;
        public String workflowBody;
    }

    @POST
    @Path("/add")
    public int addUserWorkflow(UserWorkflowAddRequest userWorkflowAddRequest, @Session HttpSession session) {

        UserResource.User user = UserResource.getUser(session);
        if (user == null) {
            throw new TexeraWebException("No access");
        }
        System.out.println(userWorkflowAddRequest.username);
        Record1<UInteger> userID = UserSqlServer.createDSLContext()
            .select(USERACCOUNT.USERID)
            .from(USERACCOUNT)
            .where(USERACCOUNT.USERNAME.equal(userWorkflowAddRequest.username))
            .fetchOne();
        return UserSqlServer.createDSLContext()
                            .insertInto(USERWORKFLOW)
                            .set(USERWORKFLOW.USERID, userID.value1())
                            .set(USERWORKFLOW.WORKFLOWID, userWorkflowAddRequest.workflowID)
                            .set(USERWORKFLOW.NAME, userWorkflowAddRequest.workflowID)
                            .set(USERWORKFLOW.WORKFLOWBODY, userWorkflowAddRequest.workflowBody)
                            .execute();
    }
    /**
     * This method handles the frontend's request to get all workflows of a specific user.
     *
     * @param UserWorkflowListRequest
     * @param session
     * @return a json string representing an list of projects which records workflow id and name.
     */
    @POST
    @Path("/workflow-list")
    public List<UserWorkflowDescription> getUserWorkflowList(UserWorkflowListRequest request, @Session HttpSession session) {

        UserResource.User user = UserResource.getUser(session);
        if (user == null) {
            throw new TexeraWebException("No access");
        }
        Record1<UInteger> userID = UserSqlServer.createDSLContext()
                                    .select(USERACCOUNT.USERID)
                                    .from(USERACCOUNT)
                                    .where(USERACCOUNT.USERNAME.equal(request.username))
                                    .fetchOne();

        Result<Record2<String, String>> projects = UserSqlServer.createDSLContext()
                            .select(USERWORKFLOW.WORKFLOWID, USERWORKFLOW.NAME)
                            .from(USERWORKFLOW)
                            .where(USERWORKFLOW.USERID.equal(userID.value1())).fetch();

        List<UserWorkflowDescription> projectList = new ArrayList<>();
        for (Record2<String, String> project: projects) {
            projectList.add(new UserWorkflowDescription(project.value1(), project.value2()));
        }

        return projectList;
    }
    /**
     * This method handles the frontend's request to get a specific workflow to be displayed
     * at current design, it only takes the workflowID and searches within the database for the matching workflow
     * for future design, it should also take userID as an parameter.
     * @param workflowID workflow id, which serves as the primary key in the UserWorkflow database
     * @param session
     * @return a json string representing an savedWorkflow
     */
    @GET
    @Path("/get/{workflowID}")
    public UserWorkflow getUserWorkflow(@PathParam("workflowID") String workflowID, @Session HttpSession session) {
        // uncomment below to link user with workflow
        // UInteger userID = UserResource.getUser(session).getUserID();
        Record3<String, String, String> result = getWorkflowFromDatabase(workflowID);

        if (result == null) {
            throw new TexeraWebException("Workflow with id: " + workflowID + " does not exit.");
        }

        try {
            // the json string stored in USERWORKFLOW.WORKFLOWBODY correspond to the interface savedWorkflowBody
            // in new-gui/src/app/workspace/service/save-workflow/save-workflow.service.ts
            ObjectNode savedWorkflowBody = new ObjectMapper().readValue(result.get(USERWORKFLOW.WORKFLOWBODY), ObjectNode.class);
            return new UserWorkflow(
                    result.get(USERWORKFLOW.WORKFLOWID),
                    result.get(USERWORKFLOW.NAME),
                    savedWorkflowBody
            );
        } catch (IOException e) {
            throw new TexeraWebException(e.getMessage());
        }
    }

    /**
     * this method handles the frontend's request to save a specific workflow
     * at current design, it takes a workflowID and a JSON string representing the new workflow
     * it updates the corresponding mysql record; throws an error if the workflow does not exist
     * for future design, it should also take userID as an parameter.
     * @param session
     * @param workflowID
     * @param workflowBody
     * @return
     */
    @POST
    @Path("/update-workflow")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public GenericWebResponse setUserWorkflow(
            @Session HttpSession session,
            @FormDataParam("workflowID") String workflowID,
            @FormDataParam("workflowBody") String workflowBody
    ) {
        int count = checkWorkflowExist(workflowID);
        if (count != 1) {
            return new GenericWebResponse(1,"workflow " + workflowID + " does not exist in the database");
        }
        int result = updateWorkflowInDataBase(workflowID,workflowBody);
        throwErrorWhenNotOne("Error occurred while updating workflow to database",result);
        return GenericWebResponse.generateSuccessResponse();
    }

    /**
     * select * from table userworkflow where workflowID is @param "workflowID"
     * @param workflowID
     * @return
     */
    private Record3<String, String, String> getWorkflowFromDatabase(String workflowID) {
        return UserSqlServer.createDSLContext()
                .select(USERWORKFLOW.WORKFLOWID, USERWORKFLOW.NAME, USERWORKFLOW.WORKFLOWBODY)
                .from(USERWORKFLOW)
                .where(USERWORKFLOW.WORKFLOWID.eq(workflowID))
                .fetchOne();
    }

    /**
     * update table userworkflow set workflowBody = @param "workflowBody" where workflowID = @param "workflowID"
     * @param workflowID
     * @param workflowBody
     * @return
     */
    private int updateWorkflowInDataBase(String workflowID, String workflowBody) {
        return UserSqlServer.createDSLContext().update(USERWORKFLOW)
                .set(USERWORKFLOW.WORKFLOWBODY, workflowBody)
                .where(USERWORKFLOW.WORKFLOWID.eq(workflowID))
                .execute();
    }

    /**
     * select count(*) from userworkflow where workflowID = @param "workflowID"
     * @param workflowID
     * @return
     */
    private int checkWorkflowExist(String workflowID) {
        return UserSqlServer.createDSLContext()
                .selectCount()
                .from(USERWORKFLOW)
                .where(USERWORKFLOW.WORKFLOWID.eq(workflowID))
                .fetchOne(0, int.class);
    }

    /**
     * This private method will be used to insert a non existing workflow into the database
     * There is no request handler that utilize this method yet.
     * @param userID
     * @param workflowID
     * @param workflowName
     * @param workflowBody
     * @return
     */
    private int insertWorkflowToDataBase(String userID, String workflowID, String workflowName, String workflowBody) {
        return UserSqlServer.createDSLContext().insertInto(USERWORKFLOW)
                // uncomment below to give workflows the concept of ownership
                // .set(USERWORKFLOW.USERID,userID)
                .set(USERWORKFLOW.WORKFLOWID, workflowID)
                .set(USERWORKFLOW.NAME, workflowName)
                .set(USERWORKFLOW.WORKFLOWBODY, workflowBody)
                .execute();
    }

    /**
     * Most the sql operation should only be executed once. eg. insertion, deletion.
     * this method will raise TexeraWebException when the input number is not one
     * @param errorMessage
     * @param count
     * @throws TexeraWebException
     */
    private void throwErrorWhenNotOne(String errorMessage, int count) throws TexeraWebException {
        if (count != 1) {
            throw new TexeraWebException(errorMessage);
        }
    }
}