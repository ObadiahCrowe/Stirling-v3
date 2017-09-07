package com.obadiahpcrowe.stirling.api.obj;

import lombok.Getter;

import java.util.Map;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 7/9/17 at 11:25 AM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.api.obj
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public class StirlingAPI {

    private String apiName;
    private Map<String, String> params;
    private String returnType;

    public StirlingAPI(String apiName, Map<String, String> params, String returnType) {
        this.apiName = apiName;
        this.params = params;
        this.returnType = returnType;
    }

}
