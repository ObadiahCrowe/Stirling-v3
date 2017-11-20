package com.obadiahpcrowe.stirling.notes;

import com.google.gson.Gson;
import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.database.MorphiaService;
import com.obadiahpcrowe.stirling.database.dao.NoteDAOImpl;
import com.obadiahpcrowe.stirling.database.dao.interfaces.NoteDAO;
import com.obadiahpcrowe.stirling.resources.AttachableResource;
import com.obadiahpcrowe.stirling.util.StirlingDate;
import com.obadiahpcrowe.stirling.util.msg.MsgTemplate;
import com.obadiahpcrowe.stirling.util.msg.StirlingMsg;

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

    private MorphiaService morphiaService;
    private NoteDAO noteDAO;
    private Gson gson = new Gson();

    public NoteManager() {
        this.morphiaService = new MorphiaService();
        this.noteDAO = new NoteDAOImpl(StirlingNote.class, morphiaService.getDatastore());
    }

    public String createNote(StirlingAccount account, String title, String content, List<AttachableResource> resources) {
        noteDAO.save(new StirlingNote(account, title, content, resources));
        return gson.toJson(new StirlingMsg(MsgTemplate.NOTE_CREATED, account.getLocale(), title));
    }

    public String deleteNote(StirlingAccount account, UUID uuid) {
        if (!noteExists(uuid)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.NOTE_DOES_NOT_EXIST, account.getLocale(), uuid.toString()));
        }

        StirlingNote note = getNote(uuid);
        if (!note.getOwner().equals(account.getAccountName())) {
            return gson.toJson(new StirlingMsg(MsgTemplate.NOT_OWNER_OF_NOTE, account.getLocale()));
        }

        noteDAO.delete(note);
        return gson.toJson(new StirlingMsg(MsgTemplate.NOTE_DELETED, account.getLocale(), note.getTitle()));
    }

    public StirlingNote getNote(UUID uuid) {
        return noteDAO.getByUuid(uuid);
    }

    public List<StirlingNote> getNotes(StirlingAccount account) {
        return noteDAO.getAll(account);
    }

    public String editTitle(StirlingAccount account, UUID uuid, String title) {
        if (!noteExists(uuid)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.NOTE_DOES_NOT_EXIST, account.getLocale(), uuid.toString()));
        }

        StirlingNote note = getNote(uuid);
        if (!note.getOwner().equals(account.getAccountName())) {
            return gson.toJson(new StirlingMsg(MsgTemplate.NOT_OWNER_OF_NOTE, account.getLocale()));
        }

        updateField(note, "title", title);
        updateField(note, "editDateTime", StirlingDate.getNow());
        return gson.toJson(new StirlingMsg(MsgTemplate.NOTE_EDITED, account.getLocale(), note.getTitle()));
    }

    public String editContent(StirlingAccount account, UUID uuid, String content) {
        if (!noteExists(uuid)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.NOTE_DOES_NOT_EXIST, account.getLocale(), uuid.toString()));
        }

        StirlingNote note = getNote(uuid);
        if (!note.getOwner().equals(account.getAccountName())) {
            return gson.toJson(new StirlingMsg(MsgTemplate.NOT_OWNER_OF_NOTE, account.getLocale()));
        }

        updateField(note, "content", content);
        updateField(note, "editDateTime", StirlingDate.getNow());
        return gson.toJson(new StirlingMsg(MsgTemplate.NOTE_EDITED, account.getLocale(), note.getTitle()));
    }

    public void updateField(StirlingNote note, String field, Object value) {
        noteDAO.updateField(note, field, value);
    }

    public boolean noteExists(UUID uuid) {
        return getNote(uuid) != null;
    }

    public static NoteManager getInstance() {
        if (instance == null)
            instance = new NoteManager();
        return instance;
    }
}
