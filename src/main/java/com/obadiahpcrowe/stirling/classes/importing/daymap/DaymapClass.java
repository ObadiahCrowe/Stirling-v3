package com.obadiahpcrowe.stirling.classes.importing.daymap;

import com.obadiahpcrowe.stirling.classes.importing.obj.ImportableClass;
import com.obadiahpcrowe.stirling.classes.obj.StirlingAssignment;
import com.obadiahpcrowe.stirling.classes.obj.StirlingPostable;
import com.obadiahpcrowe.stirling.resources.AttachableResource;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 17/10/17 at 5:57 PM
 * Project: Stirling-v3
 * Package: com.obadiahpcrowe.stirling.classes.importing.daymap
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
@Setter
public class DaymapClass extends ImportableClass {

    private String room;
    private String teacher;
    private List<StirlingPostable> classNotes;
    private List<StirlingPostable> homework;
    private List<AttachableResource> resources;
    private List<StirlingAssignment> assignments;

    @Deprecated
    public DaymapClass() {
    }

    public DaymapClass(String id, String name, String room, String teacher, List<StirlingPostable> classNotes,
                       List<StirlingPostable> homework, List<AttachableResource> resources, List<StirlingAssignment> assignments) {
        super(id, name);
        this.room = room;
        this.teacher = teacher;
        this.classNotes = classNotes;
        this.homework = homework;
        this.resources = resources;
        this.assignments = assignments;
    }
}
