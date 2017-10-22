package com.obadiahpcrowe.stirling.database.dao;

import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.database.dao.interfaces.NoteDAO;
import com.obadiahpcrowe.stirling.notes.obj.StirlingNote;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

import java.util.List;
import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 20/9/17 at 2:28 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.database.dao
 * Copyright (c) Obadiah Crowe 2017
 */
public class NoteDAOImpl extends BasicDAO<StirlingNote, ObjectId> implements NoteDAO {

    public NoteDAOImpl(Class<StirlingNote> stirlingNoteClass, Datastore datastore) {
        super(stirlingNoteClass, datastore);
    }

    @Override
    public StirlingNote getByUuid(UUID uuid) {
        Query<StirlingNote> note = createQuery()
          .field("uuid").equal(uuid);

        return note.get();
    }

    @Override
    public List<StirlingNote> getAll(StirlingAccount account) {
        Query<StirlingNote> note = createQuery()
          .field("owner").equal(account.getAccountName());

        return note.asList();
    }

    @Override
    public void updateField(StirlingNote note, String field, Object value) {
        Query<StirlingNote> query = createQuery().field("uuid").equal(note.getUuid());
        UpdateOperations<StirlingNote> updateOps = createUpdateOperations().set(field, value);

        update(query, updateOps);
    }
}
