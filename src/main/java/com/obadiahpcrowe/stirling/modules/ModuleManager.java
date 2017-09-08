package com.obadiahpcrowe.stirling.modules;

import com.obadiahpcrowe.stirling.modules.interfaces.StirlingModule;
import com.obadiahpcrowe.stirling.util.UtilFile;
import com.obadiahpcrowe.stirling.util.UtilFilter;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
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
    private List<StirlingModule> modules = new ArrayList<>();

    public void registerModules() {
        try {
            for (File file : new File(UtilFile.getInstance().getStorageLoc() + File.separator + "Modules")
              .listFiles(UtilFilter.endsWithFilter(".jar"))) {
                loadModule(file);
            }
        } catch (NullPointerException ignored) { }
    }

    public void unregisterModules() {
        modules.stream().forEach(module -> module.unload());
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

            modules.add(module);
            module.load();
        } catch (IOException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void unloadModule(String moduleName) {
        modules.stream().filter(module -> module.getName().equalsIgnoreCase(moduleName)).forEach(StirlingModule::unload);
    }

    public static ModuleManager getInstance() {
        if (instance == null)
            instance = new ModuleManager();
        return instance;
    }
}
