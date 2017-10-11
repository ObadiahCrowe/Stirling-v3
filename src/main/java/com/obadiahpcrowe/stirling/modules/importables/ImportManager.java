package com.obadiahpcrowe.stirling.modules.importables;

import com.google.common.collect.Lists;
import com.obadiahpcrowe.stirling.modules.ModuleManager;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 11/10/17 at 9:49 PM
 * Project: Stirling-v3
 * Package: com.obadiahpcrowe.stirling.modules.importables
 * Copyright (c) Obadiah Crowe 2017
 */
public class ImportManager {

    private static ImportManager instance;
    private List<Method> importables = Lists.newArrayList();

    public void init() {
        ModuleManager.getInstance().getModules().forEach(module -> {
            module.getImportHandlers().forEach(handler -> {
                for (Method method : handler.getDeclaredMethods()) {
                    if (method.isAnnotationPresent(Importable.class)) {
                        importables.add(method);
                    }
                }
            });
        });
    }

    /***
     * Calls an importable
     * @return Returns the imported data as GSON. Stirling should be expecting the returned data format.
     */
    public String callImportable(Method method) {
        return "";
    }

    public List<Method> getImportablesBySource(String sourceName) {
        List<Method> methods = Lists.newArrayList();

        importables.forEach(importable -> {
            Importable annotation = importable.getAnnotation(Importable.class);
            if (annotation.sourceName().equals(sourceName)) {
                methods.add(importable);
            }
        });

        return methods;
    }

    public List<Method> getImportablesByType(ImportHandler handlerType) {
        List<Method> methods = Lists.newArrayList();

        importables.forEach(importable -> {
            Importable annotation = importable.getAnnotation(Importable.class);
            if (annotation.importableClass().equals(handlerType.getClass())) {
                methods.add(importable);
            }
        });

        return methods;
    }

    public static ImportManager getInstance() {
        if (instance == null)
            instance = new ImportManager();
        return instance;
    }
}
