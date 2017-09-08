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
    private List<EventListener> listeners = new ArrayList<>();

    public void init() {
        ModuleManager.getInstance().getModules().forEach(module -> listeners.addAll(module.getListeners()));
    }

    public void fireEvent(StirlingEvent event) {
        for (EventListener eventListener : listeners) {
            for (Method method : eventListener.getClass().getDeclaredMethods()) {
                if (method.isAnnotationPresent(EventHandler.class)) {
                    try {
                        method.invoke(event);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
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
