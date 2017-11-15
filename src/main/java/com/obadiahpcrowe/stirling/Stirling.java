package com.obadiahpcrowe.stirling;

import com.obadiahpcrowe.stirling.accounts.AccountManager;
import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.api.*;
import com.obadiahpcrowe.stirling.api.debug.DebugAPI;
import com.obadiahpcrowe.stirling.api.obj.APIManager;
import com.obadiahpcrowe.stirling.classes.importing.obj.ImportableClass;
import com.obadiahpcrowe.stirling.modules.ModuleManager;
import com.obadiahpcrowe.stirling.modules.events.EventManager;
import com.obadiahpcrowe.stirling.modules.handoff.HandoffManager;
import com.obadiahpcrowe.stirling.modules.importables.ImportManager;
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

    @Getter
    private StirlingVersion version = new StirlingVersion(VersionType.DEVELOPMENT_BUILD, 3.0, 0);

    // TODO: 24/9/17 Daymap, Moodle, Gclassroom imports (Use some of the shit from v2) (Maybe SACE integration)
    // TODO: 17/10/17 Add map null checks
    // TODO: 17/10/17 Add user linkers to class through their import account. On init class, check user import account then readd.
    // TODO: 17/10/17 Scrape every morning
    // TODO: 17/10/17 Remove profanity from code
    // TODO: 17/10/17 Make sure the ImportableClass shit doesn't fuck up
    // TODO: 17/10/17 Import from daymap through the daymapId, then store the daymap shit against a user account and update it through a non-account method for all users.
    // TODO: 27/10/17 Junit

    // AFTER RELEASE
    // TODO: 17/10/17 Generate report and predicted grades from Stirling results

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
          //new CalendarAPI(),
          new ClassesAPI(),
          new CloudAPI(),
          new FeedbackAPI(),
          new ImportAPI(),
          new InfoAPI(),
          new NotesAPI(),
          new SaceAPI(),
          //new SessionAPI(),
          new SignInAPI(),
          new SurveyAPI()
        );

        // Only for development builds.
        if (getInstance().getVersion().getType() == VersionType.DEVELOPMENT_BUILD) {
            APIManager.getInstance().registerCall(DebugAPI.class, true);
        }

        utilLog.log("Loading modules..");
        ModuleManager.getInstance().registerModules();

        utilLog.log("Registering module event handlers..");
        EventManager.getInstance().init();

        utilLog.log("Registering module handoffs..");
        HandoffManager.getInstance().init();

        utilLog.log("Registering module import handlers..");
        ImportManager.getInstance().init();

        utilLog.log("Registering module API calls..");
        ModuleManager.getInstance().registerAPICalls();

        utilLog.log("Starting REST API service..");
        SpringApplication.run(Stirling.class, args);

        //Stage 2 Psychology - Johnson 1895
        //Ms Sawrey: Year 12 English 2100

        StirlingAccount account = AccountManager.getInstance().getAccount("ObadiahCrowe");
        com.obadiahpcrowe.stirling.classes.importing.ImportManager mgr = com.obadiahpcrowe.stirling.classes.importing.ImportManager.getInstance();
        System.out.println(mgr.getMoodleClass(account, new ImportableClass("Stage 2 Psychology - Johnson", "1895")));

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            utilLog.log("Beginning shutdown procedure..");

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
