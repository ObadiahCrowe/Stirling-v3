package com.obadiahpcrowe.stirling.notes;

import com.google.gson.Gson;
import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.database.DatabaseManager;
import com.obadiahpcrowe.stirling.database.obj.StirlingCall;
import com.obadiahpcrowe.stirling.notes.obj.StirlingNote;
import com.obadiahpcrowe.stirling.resources.AttachableResource;
import com.obadiahpcrowe.stirling.util.msg.MsgTemplate;
import com.obadiahpcrowe.stirling.util.msg.StirlingMsg;

import java.util.List;

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

    public String deleteNote() {
        return "";
    }

    public String attachFiles() {
        return "";
    }

    public String removeFiles() {
        return "";
    }

    public String editTitle() {
        return "";
    }

    public String editContent() {
        return "";
    }

    public static NoteManager getInstance() {
        if (instance == null)
            instance = new NoteManager();
        return instance;
    }
}
