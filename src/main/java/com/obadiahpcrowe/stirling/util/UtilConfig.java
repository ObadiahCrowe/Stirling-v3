package com.obadiahpcrowe.stirling.util;

import com.google.gson.Gson;
import com.obadiahpcrowe.stirling.localisation.StirlingLocale;
import com.obadiahpcrowe.stirling.util.enums.AusState;
import com.obadiahpcrowe.stirling.util.enums.VersionType;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 5/9/17 at 7:09 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.util
 * Copyright (c) Obadiah Crowe 2017
 */
public class UtilConfig {

    private static UtilConfig instance;
    private Gson gson = new Gson();
    private StirlingConfig config = null;
    private File home = UtilFile.getInstance().getStorageLoc();
    private File configFile = new File(home + File.separator + "config.json");

    public void init() {
        if (!home.exists()) {
            home.mkdir();
        }

        if (!configFile.exists()) {
            try {
                configFile.createNewFile();

                config = new StirlingConfig(VersionType.DEVELOPMENT_BUILD, AusState.SA, "UNREGISTERED", true,
                  true, false, StirlingLocale.ENGLISH);
                saveConfig();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadConfig() {
        BufferedReader reader = null;
        try {
            reader = Files.newBufferedReader(Paths.get(configFile.toURI()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<String> lines = reader.lines().collect(Collectors.toList());
        String finalStr = "";
        for (String s : lines) {
            finalStr = finalStr + s;
        }

        config = gson.fromJson(finalStr, StirlingConfig.class);
    }

    public void saveConfig() {
        List<String> list = Arrays.asList(gson.toJson(config));
        Path path = Paths.get(configFile.toURI());
        try {
            Files.write(path, list, Charset.forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public StirlingConfig getConfig() {
        if (config == null) {
            loadConfig();
        }
        return config;
    }

    public static UtilConfig getInstance() {
        if (instance == null)
            instance = new UtilConfig();
        return instance;
    }
}
