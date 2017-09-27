package com.obadiahpcrowe.stirling;

import com.obadiahpcrowe.stirling.accounts.AccountManager;
import com.obadiahpcrowe.stirling.api.*;
import com.obadiahpcrowe.stirling.api.debug.DebugAPI;
import com.obadiahpcrowe.stirling.api.obj.APIManager;
import com.obadiahpcrowe.stirling.modules.ModuleManager;
import com.obadiahpcrowe.stirling.modules.events.EventManager;
import com.obadiahpcrowe.stirling.modules.handoff.HandoffManager;
import com.obadiahpcrowe.stirling.schools.SchoolManager;
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

    // TODO: 24/9/17 Daymap, Moodle, Gclassroom imports (Use some of the shit from v2) (Maybe SACE integration)

    public static void main(String[] args) {
        UtilLog utilLog = UtilLog.getInstance();
        System.out.println("Initialising Stirling..");
        UtilFile.getInstance().init();

        utilLog.init();
        utilLog.log("Initialising logger..");

        utilLog.log("Loading configuration..");
        UtilConfig.getInstance().init();

        utilLog.log("Initialising school preferences..");
        SchoolManager.getInstance().init();

        utilLog.log("Registering default API calls..");
        APIManager.getInstance().registerDefaultCalls(
          new AccountAPI(),
          new AnnouncementAPI(),
          new CalendarAPI(),
          new ClassesAPI(),
          new CloudAPI(),
          new FeedbackAPI(),
          new InfoAPI(),
          new ModuleAPI(),
          new NotesAPI(),
          new SaceAPI(),
          new SessionAPI(),
          new SignInAPI(),
          new SurveyAPI()
        );

        System.out.println(new AccountManager().deleteAccount("ObadiahCrowe2", "@nMV6dHRQmKac"));

        // Only for development builds.
        if (getInstance().getVersion().getType() == VersionType.DEVELOPMENT_BUILD) {
            APIManager.getInstance().registerCall(DebugAPI.class, true);
        }

        utilLog.log("Loading modules..");
        ModuleManager.getInstance().registerModules();

        utilLog.log("Registering module event handlers..");
        EventManager.getInstance().init();

        utilLog.log("Register module handoffs..");
        HandoffManager.getInstance().init();

        utilLog.log("Registering module API calls..");
        ModuleManager.getInstance().registerAPICalls();

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
