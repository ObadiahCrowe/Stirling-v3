package com.obadiahpcrowe.stirling.schools;

import com.obadiahpcrowe.stirling.schools.interfaces.RegisteredSchool;
import com.obadiahpcrowe.stirling.schools.types.GIHS;
import com.obadiahpcrowe.stirling.util.UtilConfig;
import com.obadiahpcrowe.stirling.util.UtilLog;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 21/9/17 at 5:10 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.schools
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public class SchoolManager {

    private static SchoolManager instance;
    private RegisteredSchool school = null;
    private List<RegisteredSchool> schools;

    public void init() {
        schools = Arrays.asList(
          new GIHS()
        );

        String schoolName = UtilConfig.getInstance().getConfig().getSchoolName();
        if (!schoolName.equalsIgnoreCase("UNREGISTERED")) {
            schools.forEach(s -> {
                if (s.getName().equalsIgnoreCase(schoolName)) {
                    school = s;
                }
            });
        }

        if (school == null) {
            UtilLog.getInstance().log("This version of Stirling is unregistered, please apply your specific school " +
              "settings. If this version of Stirling was not licensed to your school, cease using it immediately.");
        } else {
            UtilLog.getInstance().log("You have set your school to: " + school.getName() + "! Thank you for using Stirling.");
        }
    }

    public static SchoolManager getInstance() {
        if (instance == null)
            instance = new SchoolManager();
        return instance;
    }
}
