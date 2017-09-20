package com.obadiahpcrowe.stirling.classes;

import com.obadiahpcrowe.stirling.calendar.obj.LessonCalEntry;
import com.obadiahpcrowe.stirling.classes.interfaces.StirlingPostable;
import com.obadiahpcrowe.stirling.classes.obj.StirlingAssessment;
import com.obadiahpcrowe.stirling.classes.obj.StirlingCatchup;
import com.obadiahpcrowe.stirling.classes.obj.StirlingOutline;
import com.obadiahpcrowe.stirling.classes.obj.StirlingSection;
import com.obadiahpcrowe.stirling.messaging.MessageGroup;
import com.obadiahpcrowe.stirling.messaging.contacts.ContactableAccount;
import lombok.Getter;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 5/9/17 at 4:17 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.classes
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
@Entity("classes")
public class StirlingClass {

    @Id
    private ObjectId objectId;

    private List<ContactableAccount> teachers;
    private UUID uuid;
    private String room;
    private String name;
    private String desc;
    private StirlingOutline outline;
    private List<StirlingSection> sections;
    private List<StirlingPostable> postables;
    private List<StirlingCatchup> catchupModules;
    private List<LessonCalEntry> lessons;
    private List<StirlingAssessment> assessments;
    private List<ContactableAccount> classMates;
    private MessageGroup messageGroup;

    public StirlingClass(List<ContactableAccount> teachers, String room, String name, String desc, StirlingOutline outline,
                         List<StirlingSection> sections, List<StirlingPostable> postables, List<StirlingCatchup> catchupModules,
                         List<LessonCalEntry> lessons, List<StirlingAssessment> assessments, List<ContactableAccount> classMates,
                         MessageGroup messageGroup) {
        this.teachers = teachers;
        this.uuid = UUID.randomUUID();
        this.room = room;
        this.name = name;
        this.desc = desc;
        this.outline = outline;
        this.sections = sections;
        this.postables = postables;
        this.catchupModules = catchupModules;
        this.lessons = lessons;
        this.assessments = assessments;
        this.classMates = classMates;
        this.messageGroup = messageGroup;
    }
}
