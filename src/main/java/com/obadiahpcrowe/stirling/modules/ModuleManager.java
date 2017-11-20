package com.obadiahpcrowe.stirling.modules;

import com.google.common.collect.Lists;
import com.obadiahpcrowe.stirling.api.obj.APIManager;
import com.obadiahpcrowe.stirling.modules.interfaces.StirlingModule;
import com.obadiahpcrowe.stirling.util.UtilFile;
import com.obadiahpcrowe.stirling.util.UtilFilter;
import com.obadiahpcrowe.stirling.util.UtilLog;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.jar.Manifest;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 8/9/17 at 6:05 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.modules
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public class ModuleManager {

    private static ModuleManager instance;
    private List<StirlingModule> modules;
    private UtilLog utilLog = UtilLog.getInstance();

    private ModuleManager() {
        this.modules = Lists.newArrayList();
    }

    public void registerModules() {
        try {
            for (File file : new File(UtilFile.getInstance().getStorageLoc() + File.separator + "Modules")
              .listFiles(UtilFilter.endsWithFilter(".jar"))) {
                loadModule(file);
            }
        } catch (NullPointerException ignored) { }
    }

    public void unregisterModules() {
        modules.forEach(module -> {
            module.unload();
            utilLog.log("Unloaded module: " + module.getName());
        });
    }

    public void reloadModule(String moduleName) {
        modules.forEach(module -> {
            if (module.getName().equals(moduleName)) {
                module.unload();
                registerModules();
            }
        });
    }

    public void reloadAllModules() {
        modules.forEach(module -> {
            reloadModule(module.getName());
        });
    }

    public void loadModule(File file) {
        try {
            URL manifestURL = new URL("jar:file:" + file.getAbsolutePath() + "!/META-INF/MANIFEST.MF");
            JarURLConnection connection = (JarURLConnection) manifestURL.openConnection();
            InputStream in = connection.getInputStream();

            ClassLoader loader = URLClassLoader.newInstance(new URL[] { file.toURL() });
            Manifest manifest = new Manifest(in);

            StirlingModule module = (StirlingModule) loader.loadClass(manifest.getMainAttributes()
              .getValue("Main-Class")).newInstance();

            if (!modules.contains(module)) {
                modules.add(module);
                module.load();
                utilLog.log("Loaded module: " + module.getName());
            }
        } catch (IOException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void unloadModule(String moduleName) {
        modules.stream().filter(module -> module.getName().equalsIgnoreCase(moduleName)).forEach(module -> {
            module.unload();
            utilLog.log("Unloaded module: " + module.getName());
        });
    }

    public void registerAPICalls() {
        modules.forEach(module -> module.getAPICalls().forEach(clazz -> APIManager.getInstance().registerModuleAPI(clazz)));
    }

    public static ModuleManager getInstance() {
        if (instance == null)
            instance = new ModuleManager();
        return instance;
    }
}
