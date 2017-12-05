package com.obadiahpcrowe.stirling.util;

import com.google.common.collect.Lists;
import com.obadiahpcrowe.stirling.Stirling;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

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
          new File("Announcements"),
          new File("Classes"),
          new File("IB"),
          new File("Logs"),
          new File("Marketplace"),
          new File("Modules"),
          new File("SACE"),
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

    public void extractZip(File file, File outputDir) throws IOException {
        ZipFile zipFile = new ZipFile(file);
        try {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                File dest = new File(outputDir, entry.getName());
                if (entry.isDirectory()) {
                    dest.mkdirs();
                } else {
                    dest.getParentFile().mkdirs();
                    InputStream in = zipFile.getInputStream(entry);
                    OutputStream out = new FileOutputStream(dest);
                    IOUtils.copy(in, out);
                    IOUtils.closeQuietly(in);
                    out.close();
                }
            }
        } finally {
            zipFile.close();
        }

        File garbage = new File(outputDir + File.separator + "__MACOSX");
        if (garbage.exists()) {
            FileUtils.deleteDirectory(garbage);
        }

        if (file.exists()) {
            file.delete();
        }
    }

    public void createUserFiles(UUID uuid) {
        File userFolder = getUserFolder(uuid);
        if (!userFolder.exists()) {
            userFolder.mkdir();
        }

        List<File> folders = Lists.newArrayList(
          new File("Images"),
          new File("Classes"),
          new File("Cloud"),
          new File("Videos")
        );

        folders.forEach(file -> {
            File dirFile = new File(userFolder + File.separator + file);
            if (!dirFile.exists()) {
                dirFile.mkdir();
            }
        });

        copyDefaultAvatar(uuid.toString());
        copyDefaultBanner(uuid.toString());
    }

    public void createClassFolder(UUID classUuid) {
        File classFolder = new File(home + File.separator + "Classes" + File.separator + classUuid.toString());

        if (!classFolder.exists()) {
            classFolder.mkdir();
        }

        List<File> folders = Lists.newArrayList(
          new File("Assignments"),
          new File("Resources")
        );

        folders.forEach(file -> {
            File dirFile = new File(classFolder + File.separator + file);
            if (!dirFile.exists()) {
                dirFile.mkdir();
            }
        });
    }

    public void deleteClassFolder(UUID classUuid) {
        deleteDirectory(new File(home + File.separator + "Classes" + File.separator + classUuid.toString()));
    }

    public File getUserFolder(UUID uuid) {
        return new File(home + File.separator + "UserData" + File.separator + uuid.toString());
    }

    public void deleteUserFiles(UUID uuid) {
        deleteDirectory(new File(home + File.separator + "UserData" + File.separator + uuid.toString()));
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

    public void copyFile(File file, File directory) {
        try {
            FileUtils.copyFileToDirectory(file, directory);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<File> getAllFiles(File directory) {
        List<File> files = Lists.newArrayList();
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
                home = new File("." + File.separator + "Stirling");
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
