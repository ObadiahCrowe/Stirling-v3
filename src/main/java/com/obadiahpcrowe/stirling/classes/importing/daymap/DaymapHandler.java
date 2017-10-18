package com.obadiahpcrowe.stirling.classes.importing.daymap;

import com.obadiahpcrowe.stirling.classes.importing.ImportAccount;
import com.obadiahpcrowe.stirling.classes.importing.enums.ImportSource;
import com.obadiahpcrowe.stirling.classes.importing.interfaces.GenericHandler;
import com.obadiahpcrowe.stirling.classes.importing.obj.ImportCredential;
import com.obadiahpcrowe.stirling.classes.importing.obj.ImportableClass;

import java.io.IOException;
import java.util.List;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 13/10/17 at 2:54 PM
 * Project: Stirling-v3
 * Package: com.obadiahpcrowe.stirling.classes.importing.daymap
 * Copyright (c) Obadiah Crowe 2017
 */
public class DaymapHandler implements GenericHandler {

    private static DaymapHandler instance;

    public static DaymapHandler getInstance() {
        if (instance == null)
            instance = new DaymapHandler();
        return instance;
    }

    @Override
    public List<ImportableClass> getAllCourses(ImportAccount account) {
        ImportCredential credential = account.getCredentials().get(ImportSource.DAYMAP);
        try {
            return DaymapScraper.getInstance().getCourses(credential.getUsername(), credential.getPassword());
        } catch (IOException e) {
            //e.printStackTrace();
            return null;
        }
    }

    @Override
    public void addCourseToUser(ImportAccount account, List<? extends ImportableClass> classes) {
        if (classes.size() != 1) {
            return;
        }

        //
    }

    @Override
    public String getCourseAsJson(ImportAccount account, String id) {
        //ImportCredential credential =
        return "";
    }
}
