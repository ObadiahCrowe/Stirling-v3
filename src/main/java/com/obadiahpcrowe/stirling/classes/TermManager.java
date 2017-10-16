package com.obadiahpcrowe.stirling.classes;

import com.google.common.collect.Maps;
import com.obadiahpcrowe.stirling.classes.obj.TermLength;
import com.obadiahpcrowe.stirling.util.enums.AusState;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 16/10/17 at 9:20 PM
 * Project: Stirling-v3
 * Package: com.obadiahpcrowe.stirling.classes
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public class TermManager {

    private static TermManager instance;

    private TermLength SA;
    private TermLength VIC;

    public TermManager() {
        initSA();
        // TODO: 16/10/17 Other states
    }

    public static TermManager getInstance() {
        if (instance == null)
            instance = new TermManager();
        return instance;
    }

    private void initSA() {
        Map<Integer, Map<String, String>> map = Maps.newHashMap();

        map.put(1, new HashMap<String, String>() {{
            put("30/1/2017", "13/4/2017");
        }});

        map.put(2, new HashMap<String, String>() {{
            put("1/5/2017", "7/7/2017");
        }});

        map.put(3, new HashMap<String, String>() {{
            put("24/7/2017", "29/9/2017");
        }});

        map.put(4, new HashMap<String, String>() {{
            put("16/10/2017", "15/12/2017");
        }});

        SA = new TermLength(AusState.SA, 2017, map);
    }

    private void initVIC() {
    }
}
