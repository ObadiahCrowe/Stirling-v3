package com.obadiahpcrowe.stirling.modules.interfaces;

import com.obadiahpcrowe.stirling.api.obj.APIController;
import com.obadiahpcrowe.stirling.modules.events.EventListener;
import com.obadiahpcrowe.stirling.modules.importables.ImportHandler;
import com.obadiahpcrowe.stirling.util.StirlingVersion;

import java.util.List;

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

    List<Class<? extends APIController>> getAPICalls();

    List<Class<? extends ImportHandler>> getImportHandlers();

    void load();

    void unload();
}
