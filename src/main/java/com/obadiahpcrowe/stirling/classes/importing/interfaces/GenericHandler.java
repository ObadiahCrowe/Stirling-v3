package com.obadiahpcrowe.stirling.classes.importing.interfaces;

import com.obadiahpcrowe.stirling.classes.importing.ImportAccount;
import com.obadiahpcrowe.stirling.classes.importing.obj.ImportableClass;

import java.util.List;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 17/10/17 at 8:58 PM
 * Project: Stirling-v3
 * Package: com.obadiahpcrowe.stirling.classes.importing.interfaces
 * Copyright (c) Obadiah Crowe 2017
 */
public interface GenericHandler {

    List<ImportableClass> getAllCourses(ImportAccount account);

    void addCourseToUser(ImportAccount account, List<? extends ImportableClass> classes);

    String getCourseAsJson(ImportAccount account, String id);
}
