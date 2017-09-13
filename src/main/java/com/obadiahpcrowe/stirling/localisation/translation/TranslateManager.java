package com.obadiahpcrowe.stirling.localisation.translation;

import com.obadiahpcrowe.stirling.localisation.StirlingLocale;
import com.obadiahpcrowe.stirling.util.UtilFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 11/9/17 at 1:05 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.localisation.translation
 * Copyright (c) Obadiah Crowe 2017
 */
public class TranslateManager {

    private static TranslateManager instance;

    public String translate(String text, StirlingLocale input, StirlingLocale output) {
        File main = new File(UtilFile.getInstance().getStorageLoc() + File.separator + "Translator" +
          File.separator + "main.js");
        StringBuilder builder = new StringBuilder();
        try {
            Process process = Runtime.getRuntime().exec("node " + main.getPath() + " " +
              output.getLocaleCode() + " " + text);
            process.waitFor();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = "";

            while ((line = reader.readLine()) != null) {
                builder.append(line).append(" ");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return builder.toString();
    }

    public static TranslateManager getInstance() {
        if (instance == null)
            instance = new TranslateManager();
        return instance;
    }
}
