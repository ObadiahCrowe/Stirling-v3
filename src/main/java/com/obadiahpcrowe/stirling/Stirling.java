package com.obadiahpcrowe.stirling;

import com.obadiahpcrowe.stirling.accounts.AccountManager;
import com.obadiahpcrowe.stirling.api.AccountAPI;
import com.obadiahpcrowe.stirling.api.InfoAPI;
import com.obadiahpcrowe.stirling.api.obj.APIManager;
import com.obadiahpcrowe.stirling.modules.ModuleManager;
import com.obadiahpcrowe.stirling.modules.events.EventManager;
import com.obadiahpcrowe.stirling.util.StirlingVersion;
import com.obadiahpcrowe.stirling.util.UtilConfig;
import com.obadiahpcrowe.stirling.util.UtilFile;
import com.obadiahpcrowe.stirling.util.UtilLog;
import com.obadiahpcrowe.stirling.util.enums.VersionType;
import lombok.Getter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 5/9/17 at 11:55 AM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling
 * Copyright (c) Obadiah Crowe 2017
 */
@SpringBootApplication
public class Stirling {

    private static Stirling instance;
    private @Getter StirlingVersion version = new StirlingVersion(VersionType.DEVELOPMENT_BUILD, 3.0, 0);

    public static void main(String[] args) {
        UtilLog utilLog = UtilLog.getInstance();
        System.out.println("Initialising Stirling..");
        UtilFile.getInstance().init();

        utilLog.init();
        utilLog.log("Initialising logger..");

        utilLog.log("Loading configuration..");
        UtilConfig.getInstance().init();

        utilLog.log("Registering default API calls..");
        APIManager.getInstance().registerDefaultCalls(
          new AccountAPI(),
          new InfoAPI()
        );

        utilLog.log("Loading modules..");
        ModuleManager.getInstance().registerModules();

        utilLog.log("Registering module event handlers..");
        EventManager.getInstance().init();

        utilLog.log("Registering module databases..");
        //

        utilLog.log("Registering module API calls..");

        utilLog.log("Starting REST API service..");
        SpringApplication.run(Stirling.class, args);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            utilLog.log("Beginning shutdown procedure..");
            utilLog.log("Saving config..");
            UtilConfig.getInstance().saveConfig();

            utilLog.log("Unloading modules..");
            ModuleManager.getInstance().unregisterModules();

            utilLog.log("Finishing shutdown..");
            utilLog.saveLogs();
        }));
    }

    public static Stirling getInstance() {
        if (instance == null)
            instance = new Stirling();
        return instance;
    }
}
