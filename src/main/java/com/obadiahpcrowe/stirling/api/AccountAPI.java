package com.obadiahpcrowe.stirling.api;

import com.obadiahpcrowe.stirling.api.obj.APIController;
import com.obadiahpcrowe.stirling.api.obj.CallableAPI;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 6/9/17 at 4:58 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.api
 * Copyright (c) Obadiah Crowe 2017
 */
@RestController
public class AccountAPI implements APIController {

    @CallableAPI(fields = {"username", "emailAddress", "password"})
    @RequestMapping(value = "/stirling/v3/accounts/create", method = RequestMethod.GET)
    public String createAccount(@RequestParam("username") String username,
                                @RequestParam("emailAddress") String emailAddress,
                                @RequestParam("password") String password) {
        return "";
    }
}
