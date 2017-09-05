package com.obadiahpcrowe.stirling.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    }

    private void loadLogs() {
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        BufferedReader reader = null;
        try {
            reader = Files.newBufferedReader(Paths.get(logFile.toURI()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        logs.addAll(reader.lines().collect(Collectors.toList()));
    }

    public void saveLogs() {
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Path path = Paths.get(logFile.toURI());
        try {
            Files.write(path, logs, Charset.forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static UtilLog getInstance() {
        if (instance == null)
            instance = new UtilLog();
        return instance;
    }
}
