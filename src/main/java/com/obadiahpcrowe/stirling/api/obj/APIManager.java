package com.obadiahpcrowe.stirling.api.obj;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 7/9/17 at 11:24 AM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public class APIManager {

    private static APIManager instance;
    private List<StirlingAPI> apis;
    private Map<StirlingAPI, Map<Class, Method>> unregisteredApis;

    private APIManager() {
        apis = Lists.newArrayList();
        unregisteredApis = Maps.newHashMap();
    }

    public void registerDefaultCalls(APIController... apis) {
        for (APIController api : apis) {
            registerCall(api.getClass(), true);
        }
    }

    public void registerModuleAPI(Class clazz) {
        registerCall(clazz, false);
    }

    public String handleWildCall(String var1, String var2) {
        // TODO: 9/9/17 check if method has values
        Map<UUID, String> output = Maps.newHashMap();
        UUID uuid = UUID.randomUUID();
        unregisteredApis.forEach((key, value) -> value.forEach((key1, value1) -> {
            try {
                output.put(uuid, (String) value1.invoke(key1.newInstance()));
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }));
        return output.get(uuid);
    }

    public void registerCall(Class clazz, boolean registered) {
        for (Method method : clazz.getDeclaredMethods()) {
            if ((method.getAnnotation(RequestMapping.class) != null) && (method.getAnnotation(CallableAPI.class) != null)) {
                List<String> paramNames = Lists.newArrayList();
                List<String> paramValues = Lists.newArrayList();

                CallableAPI callableAPI = method.getAnnotation(CallableAPI.class);
                paramNames.addAll(Arrays.asList(callableAPI.fields()));

                RequestMapping mapping = method.getAnnotation(RequestMapping.class);
                String url = "";
                for (String s : mapping.value()) {
                    url = s;
                }

                for (Parameter parameter : method.getParameters()) {
                    String name = parameter.getType().getName();
                    if (name.startsWith("java.lang.String")) {
                        name = "String";
                    }

                    if (name.startsWith("[Ljava.lang.String")) {
                        name = "String[]";
                    }

                    if (name.contains("org.springframework.web.multipart.MultipartFile")) {
                        name = "MultipartFile";
                    }

                    if (name.contains("javax.servlet.http.HttpServletResponse")) {
                        continue;
                    }

                    if (name.contains("com.obadiahpcrowe.stirling.accounts.enums.AccountType")) {
                        name = "AccounType";
                    }

                    paramValues.add(name);
                }

                String returned = method.getReturnType().getName();
                if (returned.startsWith("java.lang.String")) {
                    returned = "JSON";
                }

                if (returned.startsWith("[B")) {
                    returned = "Image";
                }

                Map<String, String> params = Maps.newHashMap();
                try {
                    for (int i = 0; i < paramNames.size(); i++) {
                        params.put(paramNames.get(i), paramValues.get(i));
                    }
                } catch (IndexOutOfBoundsException ignored) {}

                StirlingAPI api = new StirlingAPI(url, params, returned);
                this.apis.add(api);
                if (!registered) {
                    unregisteredApis.put(api, new HashMap<Class, Method>() {{
                        put(clazz, method);
                    }});
                }
            }
        }
    }

    public static APIManager getInstance() {
        if (instance == null)
            instance = new APIManager();
        return instance;
    }
}
