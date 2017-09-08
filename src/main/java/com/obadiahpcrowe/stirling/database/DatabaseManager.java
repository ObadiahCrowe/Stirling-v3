package com.obadiahpcrowe.stirling.database;

import com.google.gson.Gson;
import com.mongodb.*;
import com.obadiahpcrowe.stirling.database.obj.StirlingCall;
import com.obadiahpcrowe.stirling.database.obj.StirlingDatabase;
import lombok.Getter;

import java.util.*;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 6/9/17 at 5:04 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.database
 * Copyright (c) Obadiah Crowe 2017
 */
public class DatabaseManager {

    private static DatabaseManager instance;
    private ServerAddress address = new ServerAddress("localhost", 27017);
    private String dbName = "stirling";
    private Map<UUID, Object> data = new HashMap<>();

    @Getter
    private final StirlingDatabase accountDB = new StirlingDatabase(address, dbName, "accounts", null);

    @Getter
    private final StirlingDatabase announcementDB = new StirlingDatabase(address, dbName, "announcements", null);

    @Getter
    private final StirlingDatabase feedbackDB = new StirlingDatabase(address, dbName, "feedback", null);

    @Getter
    private final StirlingDatabase notesDB = new StirlingDatabase(address, dbName, "notes", null);

    @Getter
    private final StirlingDatabase podDB = new StirlingDatabase(address, dbName, "pod", null);

    @Getter
    private final StirlingDatabase calendarDB = new StirlingDatabase(address, dbName, "calendar", null);

    @Getter
    private final StirlingDatabase signInDB = new StirlingDatabase(address, dbName, "signin", null);

    @Getter
    private final StirlingDatabase messagingDB = new StirlingDatabase(address, dbName, "messaging", null);

    @Getter
    private final StirlingDatabase saceDB = new StirlingDatabase(address, dbName, "sace", null);

    @Getter
    private final StirlingDatabase tutorDB = new StirlingDatabase(address, dbName, "tutor", null);

    @Getter
    private final StirlingDatabase progressMarkerDB = new StirlingDatabase(address, dbName, "progmarkers", null);

    @Getter
    private final StirlingDatabase surveyDB = new StirlingDatabase(address, dbName, "surveys", null);

    @Getter
    private final StirlingDatabase blogDB = new StirlingDatabase(address, dbName, "blogs", null);

    @Getter
    private final StirlingDatabase attendanceDB = new StirlingDatabase(address, dbName, "attendance", null);

    @Getter
    private final StirlingDatabase classesDB = new StirlingDatabase(address, dbName, "classes", null);

    public Object makeCall(StirlingCall call) {
        UUID uuid = UUID.randomUUID();
        Thread t = new Thread(() -> {
            switch (call.getCallType()) {
                case GET:
                    data.put(uuid, getObject(call.getDatabase(), call.getIdentifiers(), call.getReturnableObject()));
                    break;
                case GET_FIELD:
                    data.put(uuid, getField(call.getDatabase(), call.getIdentifiers(), call.getReturnableObject(), call.getField()));
                    break;
                case INSERT:
                    insert(call.getDatabase(), call.getInsertableObject());
                    break;
                case REPLACE:
                    replaceObject(call.getDatabase(), call.getIdentifiers(), call.getInsertableObject());
                    break;
                case REPLACE_FIELD:
                    replaceField(call.getDatabase(), call.getIdentifiers(), call.getInsertableObject(), call.getField());
                    break;
                case REMOVE:
                    remove(call.getDatabase(), call.getIdentifiers());
                    break;
            }
        });
        t.start();

        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (data.containsKey(uuid)) {
            Object obj = data.get(uuid);
            data.remove(uuid);
            return obj;
        }

        return null;
    }

    private Object getObject(StirlingDatabase database, Map<String, Object> identifiers, Class returnableObject) {
        BasicDBObject query = new BasicDBObject();
        DBCursor cursor = null;
        Gson gson = new Gson();

        if (identifiers.size() <= 0) {
            cursor = database.getCollection().find();
            List<Object> objects = new ArrayList<>();
            while (cursor.hasNext()) {
                String json = gson.toJson(cursor.next());
                objects.add(gson.fromJson(json, returnableObject));
            }
            return gson.toJson(objects);
        } else {
            Iterator itr = identifiers.entrySet().iterator();
            while (itr.hasNext()) {
                Map.Entry next = (Map.Entry) itr.next();
                query.append(next.getKey().toString(), next.getValue());
            }
        }

        cursor = database.getCollection().find(query);
        if (cursor.hasNext()) {
            String json = gson.toJson(cursor.next());
            return gson.fromJson(json, returnableObject);
        }

        return null;
    }

    private Object getField(StirlingDatabase database, Map<String, Object> identifiers, Class returnableObject, String field) {
        BasicDBObject query = new BasicDBObject();
        DBCursor cursor = null;
        Gson gson = new Gson();

        if (identifiers.size() <= 0) {
            cursor = database.getCollection().find();
            Object returnedObj = null;
            while (cursor.hasNext()) {
                if (returnedObj != null) {
                    continue;
                }

                DBObject next = cursor.next();
                if (next.containsField(field)) {
                    returnedObj = next.get(field);
                }
            }

            String json = gson.toJson(returnableObject);
            return gson.fromJson(json, returnableObject);
        } else {
            Iterator itr = identifiers.entrySet().iterator();
            while (itr.hasNext()) {
                Map.Entry next = (Map.Entry) itr.next();
                query.append(next.getKey().toString(), next.getValue());
            }
        }

        cursor = database.getCollection().find(query);
        if (cursor.hasNext()) {
            DBObject next = cursor.next();
            if (next.containsField(field)) {
                String json = gson.toJson(next);
                return gson.fromJson(json, returnableObject);
            }
        }

        return null;
    }

    private void insert(StirlingDatabase database, Object insertableObject) {
        Gson gson = new Gson();
        database.getCollection().insert(BasicDBObject.parse(gson.toJson(insertableObject)));
    }

    private void replaceObject(StirlingDatabase database, Map<String, Object> identifiers, Object insertableObject) {
        Gson gson = new Gson();
        BasicDBObject object = BasicDBObject.parse(gson.toJson(insertableObject));
        BasicDBObject query = new BasicDBObject();

        Iterator itr = identifiers.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry next = (Map.Entry) itr.next();
            query.append(next.getKey().toString(), next.getValue());
        }

        database.getCollection().update(query, object);
    }

    private void replaceField(StirlingDatabase database, Map<String, Object> identifiers, Object insertableObject, String field) {
        Gson gson = new Gson();
        BasicDBObject object = new BasicDBObject();
        BasicDBObject query = new BasicDBObject();
        object.append("$set", new BasicDBObject().append(field, gson.toJson(insertableObject)));

        Iterator itr = identifiers.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry next = (Map.Entry) itr.next();
            query.append(next.getKey().toString(), next.getValue());
        }
        database.getCollection().update(query, object);
    }

    private void remove(StirlingDatabase database, Map<String, Object> identifiers) {
        BasicDBObject query = new BasicDBObject();
        Iterator itr = identifiers.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry next = (Map.Entry) itr.next();
            query.append(next.getKey().toString(), next.getValue());
        }
        database.getCollection().remove(query);
    }

    public static DatabaseManager getInstance() {
        if (instance == null)
            instance = new DatabaseManager();
        return instance;
    }
}
