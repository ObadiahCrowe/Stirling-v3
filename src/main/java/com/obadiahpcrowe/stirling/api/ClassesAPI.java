package com.obadiahpcrowe.stirling.api;

import com.obadiahpcrowe.stirling.api.obj.APIController;
import com.obadiahpcrowe.stirling.api.obj.CallableAPI;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 21/9/17 at 5:09 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.api
 * Copyright (c) Obadiah Crowe 2017
 */
@RestController
public class ClassesAPI implements APIController {

    @CallableAPI(fields = {"accountName", "password", "name", "desc", "room", "timeSlot"})
    @RequestMapping(value = "/stirling/v3/classes/create", method = RequestMethod.GET)
    public String createClass(@RequestParam("accountName") String accountName,
                              @RequestParam("password") String password,
                              @RequestParam("name") String name,
                              @RequestParam("desc") String desc,
                              @RequestParam("room") String room,
                              @RequestParam("timeSlot") String timeSlot) {
        return "";
    }

    @CallableAPI(fields = {"accountName", "password", "classUuid"})
    @RequestMapping(value = "/stirling/v3/classes/get/timetable", method = RequestMethod.GET)
    public String getTimeTable() {
        return "";
    }

    @CallableAPI(fields = {"accountName", "password", "classUuid"})
    @RequestMapping(value = "/stirling/v3/classes/delete", method = RequestMethod.GET)
    public String deleteClass() {
        return "";
    }

    @CallableAPI(fields = {"accountName", "password", "classUuid", "room"})
    @RequestMapping(value = "/stirling/v3/classes/set/room", method = RequestMethod.GET)
    public String setRoom() {
        return "";
    }
}
