package com.obadiahpcrowe.stirling.modules.events;

import com.obadiahpcrowe.stirling.modules.ModuleManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 8/9/17 at 1:38 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.modules.events
 * Copyright (c) Obadiah Crowe 2017
 */
public class EventManager {

    private static EventManager instance;
    private List<Class<? extends EventListener>> listeners = new ArrayList<>();

    public void init() {
        ModuleManager.getInstance().getModules().forEach(module -> listeners.addAll(module.getListeners()));
    }

    public void fireEvent(StirlingEvent event) {
        for (Class clazz : listeners) {
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(EventHandler.class)) {
                    EventHandler handler = method.getAnnotation(EventHandler.class);
                    if (handler.eventClass().equals(event.getClass())) {
                        try {
                            method.setAccessible(true);
                            method.invoke(clazz.newInstance(), event);
                        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public static EventManager getInstance() {
        if (instance == null)
            instance = new EventManager();
        return instance;
    }
}
