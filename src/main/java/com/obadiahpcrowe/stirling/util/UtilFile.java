package com.obadiahpcrowe.stirling.util;

import com.obadiahpcrowe.stirling.Stirling;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 5/9/17 at 7:02 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.util
 * Copyright (c) Obadiah Crowe 2017
 */
public class UtilFile {

    private static UtilFile instance;
    private File home = null;

    public void init() {
        File[] folders = new File[] {
          new File("Classes"),
          new File("Logs"),
          new File("Marketplace"),
          new File("Modules"),
          new File("UserData"),
          new File("Updates")
        };

        if (!home.exists()) {
            home.mkdir();
        }

        for (File file : folders) {
            File dirFile = new File(getStorageLoc() + File.separator + file);
            if (!dirFile.exists()) {
                dirFile.mkdir();
            }
        }

        try {
            copyInternalFile("client_secret.json", home);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createUserFiles(UUID uuid) {
        File userFolder = new File(home + File.separator + uuid.toString());
        if (!userFolder.exists()) {
            userFolder.mkdir();
        }

        File[] folders = new File[] {
          new File("Images"),
          new File("Classes"),
          new File("Cloud"),
          new File("Videos"),
        };

        for (File file : folders) {
            File dirFile = new File(home + File.separator + file);
            if (!dirFile.exists()) {
                dirFile.mkdir();
            }
        }

        copyDefaultAvatar(uuid.toString());
        copyDefaultBanner(uuid.toString());
    }

    public void deleteUserFiles(UUID uuid) {
        deleteDirectory(new File(home + File.separator + uuid.toString()));
    }

    private void copyDefaultAvatar(String uuid) {
        try {
            copyInternalFile("avatar.png", new File(home + File.separator + "UserData" +
              File.separator + uuid + File.separator + "Images"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void copyDefaultBanner(String uuid) {
        try {
            copyInternalFile("banner.jpg", new File(home + File.separator + "UserData" +
              File.separator + uuid + File.separator + "Images"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void copyInternalFile(String fileName, File directory) throws IOException {
        InputStream inputStream = Stirling.class.getClassLoader().getResourceAsStream(fileName);
        OutputStream outputStream = new FileOutputStream(new File(directory + File.separator + fileName));
        IOUtils.copy(inputStream, outputStream);
    }

    public List<File> getAllFiles(File directory) {
        List<File> files = new ArrayList<>();
        try {
            for (File file : directory.listFiles()) {
                if (file.isDirectory()) {
                    files.addAll(getAllFiles(file));
                } else {
                    files.add(file);
                }
            }
        } catch (NullPointerException ignored) {}
        return files;
    }

    public File getStorageLoc() {
        String userHome = System.getProperty("user.home");
        switch (UtilSystem.getInstance().getOS()) {
            case MACOS:
                home = new File(userHome + File.separator + "Stirling");
                break;
            case WINDOWS:
                home = new File(userHome + File.separator + "AppData" + File.separator +
                  "Roaming" + File.separator + "Stirling");
                break;
            case LINUX:
                home = new File(userHome + File.separator + ".Stirling");
                break;
        }
        return home;
    }

    public void deleteDirectory(File file) {
        try {
            FileUtils.deleteDirectory(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static UtilFile getInstance() {
        if (instance == null)
            instance = new UtilFile();
        return instance;
    }
}
