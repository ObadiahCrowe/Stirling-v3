package com.obadiahpcrowe.stirling.modules.interfaces;

import com.obadiahpcrowe.stirling.database.obj.StirlingDatabase;
import com.obadiahpcrowe.stirling.modules.events.EventListener;
import com.obadiahpcrowe.stirling.util.StirlingVersion;

import java.util.List;
import java.util.Map;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 8/9/17 at 10:24 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.modules.interfaces
 * Copyright (c) Obadiah Crowe 2017
 */
public interface StirlingModule {

    String getName();

    int getVersion();

    String getDesc();

    String getAuthor();

    StirlingVersion getStirlingVer();

    String[] getRequiredModules();

    List<Class<? extends EventListener>> getListeners();

    Map<String, StirlingDatabase> getDatabases();

    List<Class> getAPICalls();

    void load();

    void unload();
}
