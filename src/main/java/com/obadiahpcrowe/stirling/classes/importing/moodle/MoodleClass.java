package com.obadiahpcrowe.stirling.classes.importing.moodle;

import com.obadiahpcrowe.stirling.classes.importing.obj.ImportableClass;
import com.obadiahpcrowe.stirling.classes.obj.StirlingAssignment;
import com.obadiahpcrowe.stirling.classes.obj.StirlingPostable;
import com.obadiahpcrowe.stirling.classes.obj.StirlingSection;
import com.obadiahpcrowe.stirling.resources.AttachableResource;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 17/10/17 at 5:58 PM
 * Project: Stirling-v3
 * Package: com.obadiahpcrowe.stirling.classes.importing.moodle
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
@Setter
public class MoodleClass extends ImportableClass {

    private List<StirlingSection> sections;
    private List<StirlingPostable> postables;
    private List<AttachableResource> resources;
    private List<StirlingAssignment> assignments;

    @Deprecated
    public MoodleClass() {
    }

    public MoodleClass(String id, String name, List<StirlingSection> sections, List<StirlingPostable> postables,
                       List<AttachableResource> resources, List<StirlingAssignment> assignments) {
        super(name, id);
        this.sections = sections;
        this.postables = postables;
        this.resources = resources;
        this.assignments = assignments;
    }
}
