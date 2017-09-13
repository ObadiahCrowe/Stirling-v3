package com.obadiahpcrowe.stirling.notes;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.database.DatabaseManager;
import com.obadiahpcrowe.stirling.database.obj.StirlingCall;
import com.obadiahpcrowe.stirling.notes.obj.StirlingNote;
import com.obadiahpcrowe.stirling.resources.AttachableResource;
import com.obadiahpcrowe.stirling.util.msg.MsgTemplate;
import com.obadiahpcrowe.stirling.util.msg.StirlingMsg;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 9/9/17 at 5:33 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.notes
 * Copyright (c) Obadiah Crowe 2017
 */
public class NoteManager {

    private static NoteManager instance;
    private DatabaseManager databaseManager = DatabaseManager.getInstance();
    private Gson gson = new Gson();

    public String createNote(StirlingAccount account, String title, String content, List<AttachableResource> resources) {
        databaseManager.makeCall(new StirlingCall(databaseManager.getNotesDB()).insert(
          new StirlingNote(account, title, content, resources)));

        return gson.toJson(new StirlingMsg(MsgTemplate.NOTE_CREATED, account.getLocale(), title));
    }

    public String deleteNote(StirlingAccount account, UUID uuid) {
        String title = getNote(account, uuid).getTitle();
        databaseManager.makeCall(new StirlingCall(databaseManager.getNotesDB()).remove(new HashMap<String, Object>() {{
            put("owner", account.getAccountName());
            put("uuid", uuid.toString());
        }}));
        return gson.toJson(new StirlingMsg(MsgTemplate.NOTE_DELETED, account.getLocale(), title));
    }

    public StirlingNote getNote(StirlingAccount account, UUID uuid) {
        return (StirlingNote) databaseManager.makeCall(new StirlingCall(databaseManager.getNotesDB()).get(new HashMap<String, Object>() {{
            put("owner", account.getAccountName());
            put("uuid", uuid.toString());
        }}, StirlingNote.class));
    }

    public List<StirlingNote> getNotes(StirlingAccount account) {
        String json = (String) databaseManager.makeCall(new StirlingCall(databaseManager.getNotesDB())
          .get(new HashMap<>(), StirlingNote.class));

        Type type = new TypeToken<List<StirlingNote>>(){}.getType();
        List<StirlingNote> notes = gson.fromJson(json, type);

        List<StirlingNote> finalNotes = new ArrayList<>();
        notes.stream().filter(note -> note.getOwner().equals(account.getAccountName())).forEach(finalNotes::add);
        return finalNotes;
    }

    public String attachFiles(StirlingAccount account, UUID uuid, List<AttachableResource> resources) {
        StirlingNote note = getNote(account, uuid);
        note.getResources().addAll(resources);

        databaseManager.makeCall(new StirlingCall(databaseManager.getNotesDB()).replace(new HashMap<String, Object>() {{
            put("owner", account.getAccountName());
            put("uuid", uuid.toString());
        }}, note));

        return gson.toJson(new StirlingMsg(MsgTemplate.NOTE_EDITED, account.getLocale(), note.getTitle()));
    }

    public String removeFiles(StirlingAccount account, UUID uuid, List<AttachableResource> resources) {
        StirlingNote note = getNote(account, uuid);
        note.getResources().removeAll(resources);

        databaseManager.makeCall(new StirlingCall(databaseManager.getNotesDB()).replace(new HashMap<String, Object>() {{
            put("owner", account.getAccountName());
            put("uuid", uuid.toString());
        }}, note));

        return gson.toJson(new StirlingMsg(MsgTemplate.NOTE_EDITED, account.getLocale(), note.getTitle()));
    }

    public String editTitle(StirlingAccount account, UUID uuid, String title) {
        StirlingNote note = getNote(account, uuid);
        note.setTitle(title);

        databaseManager.makeCall(new StirlingCall(databaseManager.getNotesDB()).replace(new HashMap<String, Object>() {{
            put("owner", account.getAccountName());
            put("uuid", uuid.toString());
        }}, note));

        return gson.toJson(new StirlingMsg(MsgTemplate.NOTE_EDITED, account.getLocale(), note.getTitle()));
    }

    public String editContent(StirlingAccount account, UUID uuid, String content) {
        StirlingNote note = getNote(account, uuid);
        note.setContent(content);

        databaseManager.makeCall(new StirlingCall(databaseManager.getNotesDB()).replace(new HashMap<String, Object>() {{
            put("owner", account.getAccountName());
            put("uuid", uuid.toString());
        }}, note));

        return gson.toJson(new StirlingMsg(MsgTemplate.NOTE_EDITED, account.getLocale(), note.getTitle()));
    }

    public static NoteManager getInstance() {
        if (instance == null)
            instance = new NoteManager();
        return instance;
    }
}
