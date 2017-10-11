package com.obadiahpcrowe.stirling.modules.importables;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.obadiahpcrowe.stirling.localisation.StirlingLocale;
import com.obadiahpcrowe.stirling.modules.ModuleManager;
import com.obadiahpcrowe.stirling.util.msg.MsgTemplate;
import com.obadiahpcrowe.stirling.util.msg.StirlingMsg;

import java.lang.reflect.InvocationTargetException;
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
     * Calls an importable - What is this bullshit? Who knows? I wrote this 30m ago and I can't even remember. Oh well, let's hope it works in the morning.
     * @return Returns the imported data as GSON. Stirling should be expecting the returned data format.
     */
    public String callImportable(Method method, Object... params) {
        Gson gson = new Gson();
        try {
            method.setAccessible(true);
            String json = gson.toJson(method.invoke(method.getClass().newInstance(), params));

            return json;
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.UNEXPECTED_ERROR, StirlingLocale.ENGLISH, "importing data"));
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
