package com.obadiahpcrowe.stirling.api;

import com.google.gson.Gson;
import com.obadiahpcrowe.stirling.Stirling;
import com.obadiahpcrowe.stirling.api.obj.APIController;
import com.obadiahpcrowe.stirling.api.obj.APIManager;
import com.obadiahpcrowe.stirling.api.obj.CallableAPI;
import com.obadiahpcrowe.stirling.localisation.StirlingLocale;
import com.obadiahpcrowe.stirling.modules.ModuleManager;
import com.obadiahpcrowe.stirling.schools.SchoolManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 7/9/17 at 11:37 AM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.api
 * Copyright (c) Obadiah Crowe 2017
 */
@RestController
public class InfoAPI implements APIController {

    private Gson gson = new Gson();

    @CallableAPI(fields = "")
    @RequestMapping(value = "/stirling/v3/supportedCalls", method = RequestMethod.GET)
    public String getSupportedCalls() {
        return gson.toJson(APIManager.getInstance().getApis());
    }

    @CallableAPI(fields = "")
    @RequestMapping(value = "/stirling/v3/version", method = RequestMethod.GET)
    public String getVersion() {
        return gson.toJson(Stirling.getInstance().getVersion());
    }

    @CallableAPI(fields = "")
    @RequestMapping(value = "/stirling/v3/loadedModules", method = RequestMethod.GET)
    public String getLoadedModules() {
        List<String> moduleNames = new ArrayList<>();
        ModuleManager.getInstance().getModules().stream().forEach(module -> moduleNames.add(module.getName()));
        return gson.toJson(moduleNames);
    }

    @CallableAPI(fields = "")
    @RequestMapping(value = "/stirling/v3/supportedLocales", method = RequestMethod.GET)
    public String getSupportedLocales() {
        return gson.toJson(StirlingLocale.values());
    }

    @CallableAPI(fields = "")
    @RequestMapping(value = "/stirling/v3/status", method = RequestMethod.GET)
    public String getStatus() {
        return gson.toJson("Stirling's API is live!");
    }

    @CallableAPI(fields = "")
    @RequestMapping(value = "/stirling/v3/isRegistered", method = RequestMethod.GET)
    public boolean isRegistered() {
        return SchoolManager.getInstance().getSchool() != null;
    }

    @CallableAPI(fields = "")
    @RequestMapping(value = "/stirling/v3/school", method = RequestMethod.GET)
    public String getSchool() {
        return gson.toJson(SchoolManager.getInstance().getSchool());
    }
}
