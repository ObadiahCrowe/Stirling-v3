package com.obadiahpcrowe.stirling.api;

import com.obadiahpcrowe.stirling.api.obj.APIController;
import com.obadiahpcrowe.stirling.api.obj.APIManager;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 9/9/17 at 4:47 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.api
 * Copyright (c) Obadiah Crowe 2017
 */
@RestController
public class ModuleAPI implements APIController {

    @RequestMapping(value = "/stirling/v3/{var1}/{var2}", method = RequestMethod.GET)
    public String handleRequest(@PathVariable String var1, @PathVariable String var2) {
        return APIManager.getInstance().handleWildCall(var1, var2);
    }
}
