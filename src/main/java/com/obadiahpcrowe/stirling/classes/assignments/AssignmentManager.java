package com.obadiahpcrowe.stirling.classes.assignments;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.obadiahpcrowe.stirling.classes.obj.StirlingResult;
import com.obadiahpcrowe.stirling.database.MorphiaService;
import com.obadiahpcrowe.stirling.database.dao.AssignmentDAOImpl;
import com.obadiahpcrowe.stirling.database.dao.interfaces.AssignmentDAO;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Created by: Obadiah Crowe
 * Creation Date / Time: 7/12/17 at 2:25 PM
 * Project: Stirling-v3
 * Package: com.obadiahpcrowe.stirling.classes.assignments
 * Copyright (c) Obadiah Crowe 2017
 */
public class AssignmentManager {

    private static AssignmentManager instance;

    private MorphiaService morphiaService;
    private AssignmentDAO assignmentDAO;
    private Gson gson;

    private AssignmentManager() {
        this.morphiaService = new MorphiaService();
        this.assignmentDAO = new AssignmentDAOImpl(AssignmentAccount.class, morphiaService.getDatastore());
        this.gson = new Gson();
    }

    public static AssignmentManager getInstance() {
        if (instance == null)
            instance = new AssignmentManager();
        return instance;
    }

    public String addAssignment(UUID accountUuid, StirlingAssignment assignment) {
        AssignmentAccount account = getByUuid(accountUuid);

        if (account == null) {
            account = new AssignmentAccount(accountUuid, Lists.newArrayList(assignment));
            assignmentDAO.save(account);
            return "null acc";
        }

        List<StirlingAssignment> assignments = Lists.newArrayList();

        try {
            assignments.addAll(account.getAssignments());
        } catch (NullPointerException ignored) {
        }

        assignments.add(assignment);

        assignmentDAO.updateField(accountUuid, "assignments", assignments);
        return "added ass";
    }

    public String removeAssignment(UUID accountUuid, UUID assignmentUuid) {
        AssignmentAccount account = getByUuid(accountUuid);

        if (account == null) {
            return "null acc";
        }

        List<StirlingAssignment> assignments = Lists.newArrayList();

        try {
            assignments.addAll(account.getAssignments());
        } catch (NullPointerException ignored) {
        }

        assignments.forEach(a -> {
            if (a.getUuid().equals(assignmentUuid)) {
                assignments.remove(a);
            }
        });

        assignmentDAO.updateField(accountUuid, "assignments", assignments);
        return "removed ass";
    }

    public String markAssignment(UUID accountUuid, UUID assignmentUuid, StirlingResult result) {
        AssignmentAccount account = getByUuid(accountUuid);

        if (account == null) {
            return "null account";
        }

        List<StirlingAssignment> assignments = Lists.newArrayList();

        try {
            assignments.addAll(account.getAssignments());
        } catch (NullPointerException ignored) {
        }

        CompletableFuture<StirlingAssignment> future = new CompletableFuture<>();
        assignments.forEach(a -> {
            if (a.getUuid().equals(assignmentUuid)) {
                future.complete(a);
                assignments.remove(a);
            }
        });

        StirlingAssignment assignment = future.getNow(null);
        if (assignment == null) {
            return "assignment no existo";
        }

        assignment.setResult(result);
        assignments.add(assignment);

        assignmentDAO.updateField(accountUuid, "assignments", assignments);
        return "done";
    }

    public AssignmentAccount getByUuid(UUID uuid) {
        return assignmentDAO.getByUuid(uuid);
    }
}
