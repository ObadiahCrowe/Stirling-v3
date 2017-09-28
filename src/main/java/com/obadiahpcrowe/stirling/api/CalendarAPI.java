package com.obadiahpcrowe.stirling.api;

import com.obadiahpcrowe.stirling.api.obj.APIController;
import com.obadiahpcrowe.stirling.api.obj.CallableAPI;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 21/9/17 at 5:07 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.api
 * Copyright (c) Obadiah Crowe 2017
 */
@RestController
public class CalendarAPI implements APIController {

    @CallableAPI(fields = { "accountName", "password" })
    @RequestMapping(value = "/stirling/v3/calendar/get", method = RequestMethod.GET)
    public String getCalendar(@RequestParam("accountName") String accountName,
                              @RequestParam("password") String password) {
        return "";
    }
}
