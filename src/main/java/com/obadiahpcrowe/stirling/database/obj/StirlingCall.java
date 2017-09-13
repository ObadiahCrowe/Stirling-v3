package com.obadiahpcrowe.stirling.database.obj;

import com.obadiahpcrowe.stirling.database.enums.CallType;
import lombok.Getter;

import java.util.Map;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 6/9/17 at 10:46 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.database
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public class StirlingCall {

    private StirlingDatabase database;
    private CallType callType;
    private Map<String, Object> identifiers;
    private String field;
    private Object insertableObject;
    private Class returnableObject;

    public StirlingCall(StirlingDatabase database) {
        this.database = database;
    }

    public StirlingCall insert(Object insertableObject) {
        this.callType = CallType.INSERT;
        this.insertableObject = insertableObject;
        return this;
    }

    public StirlingCall get(Map<String, Object> identifiers, Class returnableObject) {
        this.callType = CallType.GET;
        this.identifiers = identifiers;
        this.returnableObject = returnableObject;
        return this;
    }

    public StirlingCall getField(Map<String, Object> identifiers, Class returnableObject, String field) {
        this.callType = CallType.GET_FIELD;
        this.identifiers = identifiers;
        this.returnableObject = returnableObject;
        this.field = field;
        return this;
    }

    public StirlingCall replace(Map<String, Object> identifiers, Object insertableObject) {
        this.callType = CallType.REPLACE;
        this.identifiers = identifiers;
        this.insertableObject = insertableObject;
        return this;
    }

    public StirlingCall replaceField(Map<String, Object> identifiers, String field, Object insertableObject) {
        this.callType = CallType.REPLACE_FIELD;
        this.identifiers = identifiers;
        this.field = field;
        this.insertableObject = insertableObject;
        return this;
    }

    public StirlingCall remove(Map<String, Object> identifiers) {
        this.callType = CallType.REMOVE;
        this.identifiers = identifiers;
        return this;
    }
}
