package com.obadiahpcrowe.stirling.classes.importing.gclassroom;

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
 * Package: com.obadiahpcrowe.stirling.classes.importing.gclassroom
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
@Setter
public class GoogleClass extends ImportableClass {

    private List<AttachableResource> resources;
    private List<StirlingAssignment> assignments;
    private List<StirlingPostable> postables;

    @Deprecated
    public GoogleClass() {
    }

    public GoogleClass(String id, String name, List<AttachableResource> resources, List<StirlingAssignment> assignments,
                       List<StirlingPostable> postables) {
        super(name, id);
        this.resources = resources;
        this.assignments = assignments;
        this.postables = postables;
    }
}
