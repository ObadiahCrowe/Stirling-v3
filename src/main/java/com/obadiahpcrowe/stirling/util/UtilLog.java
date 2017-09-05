package com.obadiahpcrowe.stirling.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 5/9/17 at 7:19 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.util
 * Copyright (c) Obadiah Crowe 2017
 */
public class UtilLog {

    private static UtilLog instance;
    private List<String> logs = new ArrayList<>();
    private File home = UtilFile.getInstance().getStorageLoc();
    private File logFile = new File(home + File.separator + "Logs" + File.separator +
      UtilTime.getInstance().getLogDate() + "-log.log");

    public void init() {
        loadLogs();
    }

    public void log(String message) {
        String output = "[" + UtilTime.getInstance().getLogTime() + "] " + message;
        System.out.println(output);

        logs.add(output);
        getInstance().saveLogs();
    }

    private void loadLogs() {

    }

    public void saveLogs() {

    }

    public static UtilLog getInstance() {
        if (instance == null)
            instance = new UtilLog();
        return instance;
    }
}
