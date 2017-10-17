package com.obadiahpcrowe.stirling.classes.importing.interfaces;

import com.obadiahpcrowe.stirling.classes.importing.ImportAccount;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 17/10/17 at 8:58 PM
 * Project: Stirling-v3
 * Package: com.obadiahpcrowe.stirling.classes.importing.interfaces
 * Copyright (c) Obadiah Crowe 2017
 */
public interface GenericHandler {

    String getAllCourses(ImportAccount account);

    void addCourseToUser(ImportAccount account, Object data);

    String getCourseAsJson(ImportAccount account, String id);
}
