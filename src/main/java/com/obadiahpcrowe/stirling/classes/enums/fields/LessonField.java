package com.obadiahpcrowe.stirling.classes.enums.fields;

import com.obadiahpcrowe.stirling.classes.obj.StirlingPostable;
import lombok.Getter;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 17/10/17 at 8:00 AM
 * Project: Stirling-v3
 * Package: com.obadiahpcrowe.stirling.classes.enums.fields
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public enum LessonField {

    TITLE("title", String.class),
    DESC("desc", String.class),
    ROOM("location", String.class),
    HOMEWORK("homework", StirlingPostable.class),
    CLASSNOTE("classNote", StirlingPostable.class);

    private String dbName;
    private Class valueClass;

    LessonField(String dbName, Class valueClass) {
        this.dbName = dbName;
        this.valueClass = valueClass;
    }
}
