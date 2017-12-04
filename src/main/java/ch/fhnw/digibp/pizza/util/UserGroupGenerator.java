/*
 * Copyright (c) 2017. University of Applied Sciences and Arts Northwestern Switzerland FHNW.
 * All rights reserved.
 */

package ch.fhnw.digibp.pizza.util;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;

import org.camunda.bpm.engine.AuthorizationService;
import org.camunda.bpm.engine.FilterService;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.authorization.Authorization;

import org.camunda.bpm.engine.authorization.Permissions;

import static org.camunda.bpm.engine.authorization.Authorization.AUTH_TYPE_GRANT;
import static org.camunda.bpm.engine.authorization.Permissions.ACCESS;
import static org.camunda.bpm.engine.authorization.Permissions.READ;

import org.camunda.bpm.engine.authorization.Resources;

import static org.camunda.bpm.engine.authorization.Resources.APPLICATION;
import static org.camunda.bpm.engine.authorization.Resources.FILTER;

import org.camunda.bpm.engine.filter.Filter;
import org.camunda.bpm.engine.identity.Group;
import org.camunda.bpm.engine.identity.User;
import org.camunda.bpm.engine.task.TaskQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author andreas.martin
 */
@Component
public class UserGroupGenerator {

    @Autowired
    private IdentityService identityService;

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private FilterService filterService;

    @Autowired
    private TaskService taskService;

    private final static Logger LOGGER = Logger.getLogger(UserGroupGenerator.class.getName());

    @PostConstruct
    public void createUsers() {

        if(identityService.createGroupQuery().groupId("pizzaiolo").singleResult() != null){
            LOGGER.info("Not creating any demo users and groups.");
            return;
        }

        Group newGroup = null;

        newGroup = identityService.newGroup("pizzaiolo");
        newGroup.setName("Pizzaiolo");
        newGroup.setType("WORKFLOW");
        identityService.saveGroup(newGroup);

        newGroup = identityService.newGroup("courier");
        newGroup.setName("Courier");
        newGroup.setType("WORKFLOW");
        identityService.saveGroup(newGroup);

        User newUser = null;

        newUser = identityService.newUser("matteo");
        newUser.setFirstName("Matteo");
        newUser.setLastName("Renzi");
        newUser.setPassword("password");
        newUser.setEmail("mail@example.com");
        identityService.saveUser(newUser);
        identityService.createMembership("matteo", "pizzaiolo");

        newUser = identityService.newUser("silvio");
        newUser.setFirstName("Silvio");
        newUser.setLastName("Berlusconi");
        newUser.setPassword("password");
        newUser.setEmail("mail@example.com");
        identityService.saveUser(newUser);
        identityService.createMembership("silvio", "courier");

        Authorization tasklistAuth = null;

        tasklistAuth = authorizationService.createNewAuthorization(AUTH_TYPE_GRANT);
        tasklistAuth.setGroupId("pizzaiolo");
        tasklistAuth.addPermission(ACCESS);
        tasklistAuth.setResourceId("tasklist");
        tasklistAuth.setResource(APPLICATION);
        authorizationService.saveAuthorization(tasklistAuth);

        tasklistAuth = authorizationService.createNewAuthorization(AUTH_TYPE_GRANT);
        tasklistAuth.setGroupId("courier");
        tasklistAuth.addPermission(ACCESS);
        tasklistAuth.setResourceId("tasklist");
        tasklistAuth.setResource(APPLICATION);
        authorizationService.saveAuthorization(tasklistAuth);

        Authorization readProcessDefinition = null;

        readProcessDefinition = authorizationService.createNewAuthorization(AUTH_TYPE_GRANT);
        readProcessDefinition.setGroupId("pizzaiolo");
        readProcessDefinition.addPermission(Permissions.READ);
        readProcessDefinition.addPermission(Permissions.READ_HISTORY);
        readProcessDefinition.setResource(Resources.PROCESS_DEFINITION);
        readProcessDefinition.setResourceId("*");
        authorizationService.saveAuthorization(readProcessDefinition);

        readProcessDefinition = authorizationService.createNewAuthorization(AUTH_TYPE_GRANT);
        readProcessDefinition.setGroupId("courier");
        readProcessDefinition.addPermission(Permissions.READ);
        readProcessDefinition.addPermission(Permissions.READ_HISTORY);
        readProcessDefinition.setResource(Resources.PROCESS_DEFINITION);
        readProcessDefinition.setResourceId("*");
        authorizationService.saveAuthorization(readProcessDefinition);


        // create default filters
        if (filterService.getFilter("My Tasks") == null) {
            LOGGER.info("Generating default filters");
            Map<String, Object> filterProperties = new HashMap<String, Object>();

            filterProperties.put("description", "Tasks assigned to me and my groups");
            filterProperties.put("priority", 1);
            filterProperties.put("refresh", true);
            Filter myTasksFilter = filterService.newTaskFilter().setName("Group and My Tasks");

            TaskQuery query = taskService.createTaskQuery().taskAssigneeExpression("${currentUser()}");
            myTasksFilter.setProperties(filterProperties).setOwner("demo").setQuery(query);
            filterService.saveFilter(myTasksFilter);
            query = taskService.createTaskQuery().taskCandidateGroupInExpression("${currentUserGroups()}").taskUnassigned();
            myTasksFilter.setProperties(filterProperties).setOwner("demo").setQuery(query);
            filterService.saveFilter(myTasksFilter);

            filterProperties.clear();

            filterProperties.put("description", "Tasks assigned to me");
            filterProperties.put("priority", 2);
            filterProperties.put("refresh", true);
            query = taskService.createTaskQuery().taskAssigneeExpression("${currentUser()}");
            myTasksFilter = filterService.newTaskFilter().setName("My Tasks").setProperties(filterProperties).setOwner("demo").setQuery(query);
            filterService.saveFilter(myTasksFilter);

            filterProperties.clear();

            filterProperties.put("description", "Tasks assigned to my groups");
            filterProperties.put("priority", 3);
            filterProperties.put("refresh", true);
            query = taskService.createTaskQuery().taskCandidateGroupInExpression("${currentUserGroups()}").taskUnassigned();
            Filter groupTasksFilter = filterService.newTaskFilter().setName("My Group Tasks").setProperties(filterProperties).setOwner("demo").setQuery(query);
            filterService.saveFilter(groupTasksFilter);

            // global read authorizations for these filters
            Authorization globalMyTaskFilterRead = authorizationService.createNewAuthorization(Authorization.AUTH_TYPE_GLOBAL);
            globalMyTaskFilterRead.setResource(FILTER);
            globalMyTaskFilterRead.setResourceId(myTasksFilter.getId());
            globalMyTaskFilterRead.addPermission(READ);
            authorizationService.saveAuthorization(globalMyTaskFilterRead);

            Authorization globalGroupFilterRead = authorizationService.createNewAuthorization(Authorization.AUTH_TYPE_GLOBAL);
            globalGroupFilterRead.setResource(FILTER);
            globalGroupFilterRead.setResourceId(groupTasksFilter.getId());
            globalGroupFilterRead.addPermission(READ);
            authorizationService.saveAuthorization(globalGroupFilterRead);
        }
    }

}