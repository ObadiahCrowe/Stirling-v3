package com.obadiahpcrowe.stirling;

import com.obadiahpcrowe.stirling.accounts.AccountManager;
import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.api.*;
import com.obadiahpcrowe.stirling.api.obj.APIManager;
import com.obadiahpcrowe.stirling.classes.importing.ImportAccount;
import com.obadiahpcrowe.stirling.classes.importing.daymap.DaymapScraper;
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
    private StirlingVersion version = new StirlingVersion(VersionType.RELEASE, 3.0);

    // v3.1 onwards
    // TODO: 17/10/17 Generate report, aggregate, and predicted grades from Stirling results
    // TODO: 27/10/17 Junit

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
          new ClassesAPI(),
          new CloudAPI(),
          new FeedbackAPI(),
          new ImportAPI(),
          new InfoAPI(),
          new NotesAPI(),
          new SaceAPI(),
          new SignInAPI()
        );

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

        StirlingAccount account = AccountManager.getInstance().getAccount("ObadiahCrowe");
        ImportAccount importAccount = com.obadiahpcrowe.stirling.classes.importing.ImportManager.getInstance().getByUuid(account.getUuid());
        DaymapScraper.getInstance().getFullCourse(importAccount, new ImportableClass("12 EnglishFB", "3755"), false);
        /*
        ClassManager.getInstance().getAllClasses(account).forEach(c -> {
            c.getStudentResults().get(account.getUuid()).forEach(r -> {
                System.out.println("RESULT - START");
                System.out.println(r.getComments());
                System.out.println(r.getGrade());
                System.out.println(r.getMaxMarks());
                System.out.println(r.getReceivedMarks());
                System.out.println(r.getWeighting());
                System.out.println("RESULT - END");
            });

            c.getStudentAssignments().get(account.getUuid()).forEach(a -> {
                System.out.println(a.getTitle());
                System.out.println(a.getResult().getComments());
            });
        });*/

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
