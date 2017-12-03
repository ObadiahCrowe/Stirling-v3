package com.obadiahpcrowe.stirling;

import com.obadiahpcrowe.stirling.api.*;
import com.obadiahpcrowe.stirling.api.obj.APIManager;
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
    private StirlingVersion version = new StirlingVersion(VersionType.RELEASE, 3.0, 0);

    // v3.1 onwards
    // TODO: 17/10/17 Generate report, aggregate, and predicted grades from Stirling results
    // TODO: 27/10/17 Junit

    // TODO: 2/12/17 create daily classes object that uses the actual class uuid rather than a lesson uuid

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
