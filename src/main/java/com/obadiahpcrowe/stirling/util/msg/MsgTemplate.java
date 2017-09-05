package com.obadiahpcrowe.stirling.util.msg;

import lombok.Getter;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 5/9/17 at 4:25 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.util.msg
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public enum MsgTemplate {

    ACCOUNT_EXISTS(MsgResponse.USER_ERROR, "The account, {0}, already exists!", 1),
    ACCOUNT_CREATED(MsgResponse.SUCCESS, "Account, {0}, created!", 1),
    ACCOUNT_DELETED(MsgResponse.SUCCESS, "Account, {0}, deleted!", 1),
    ACCOUNT_FIELD_EDITED(MsgResponse.SUCCESS, "The field, {0}, was edited for {1}!", 2),

    ANNOUNCEMENT_CREATED(MsgResponse.SUCCESS, "The announcement, {0}, has been posted!", 1),
    ANNOUNCEMENT_DELETED(MsgResponse.SUCCESS, "The announcement, {0}, has been deleted!", 1),
    ANNOUNCEMENT_EDITED(MsgResponse.SUCCESS, "The announcement, {0}, has been edited!", 1),

    CALENDAR_EVENT_ADDED(MsgResponse.SUCCESS, "The event, {0}, has been added!", 1),
    CALENDAR_EVENT_DELETED(MsgResponse.SUCCESS, "The event, {0}, has been deleted!", 1),
    CALENDAR_EVENT_EDITED(MsgResponse.SUCCESS, "The event, {0}, has been edited!", 1),

    CLOUD_STORAGE_FULL(MsgResponse.USER_ERROR, "Your cloud storage is full!", 0),

    NOTE_CREATED(MsgResponse.SUCCESS, "The note, {0}, has been created!", 1),
    NOTE_DELETED(MsgResponse.SUCCESS, "The note, {0}, has been deleted!", 1),
    NOTE_EDITED(MsgResponse.SUCCESS, "The note, {0}, has been edited!", 1),

    SCHOOL_SIGN_IN(MsgResponse.SUCCESS, "You have signed into school with the following reason: {0}!", 1),
    SCHOOL_SIGN_OUT(MsgResponse.SUCCESS, "You have signed out of school with the following reason: {0}!", 1),

    POD_SIGN_IN(MsgResponse.SUCCESS, "You have signed into the pod for {0}!", 1),

    CONTACT_ADDED(MsgResponse.SUCCESS, "You have added, {0}, as a contact!", 1),
    CONTACT_REMOVED(MsgResponse.SUCCESS, "You have removed, {0}, as a contact!", 1),
    CONTACT_REQUESTED(MsgResponse.SUCCESS, "You have requested {0} to add you as a contact!", 1),
    CONTACT_REQUEST_RECEIVED(MsgResponse.SUCCESS, "You have recieved a contact request from {0}!", 1),

    LOCALE_SET(MsgResponse.SUCCESS, "You have set your locale to: {0}!", 1),

    MESSENGER_IS_TYPING(MsgResponse.SUCCESS, "{0} is typing..", 1),

    SACE_ID_ADDED(MsgResponse.SUCCESS, "Your SACE ID, {0}, has been added!", 1),

    INSUFFICIENT_PERMISSIONS(MsgResponse.USER_ERROR, "You have insufficient permissions to {0}! The minimum account type is: {1}!", 2),
    INCORRECT_PASSWORD(MsgResponse.USER_ERROR, "Your password is incorrect!", 0),

    FIELD_TOO_LONG(MsgResponse.USER_ERROR, "The {0} length is too long! The maximum length is: {1}!", 2),
    FIELD_TOO_SHORT(MsgResponse.USER_ERROR, "The {0} length is too short! The minimum length is: {0}!", 2),

    INCOMPATIBLE_VALUE(MsgResponse.USER_ERROR, "The value, {0}, is incompatible with {1}!", 2),

    UNEXPECTED_ERROR(MsgResponse.STIRLING_ERROR, "An unexpected error occurred while {0}!", 1),

    DOWNLOADING_FILE(MsgResponse.SUCCESS, "Downloading {0}..", 1),
    UPLOADING_FILE(MsgResponse.SUCCESS, "Uploading {0}..", 1),
    SUBMITTING_FILE(MsgResponse.SUCCESS, "Submitting {0}..", 1),

    CANNOT_SET_FIELD_TO_FIELD(MsgResponse.USER_ERROR, "Cannot apply the value, {0}, to the field, {1}!", 2),
    SET_FIELD_TO_FIELD(MsgResponse.SUCCESS, "Set the value, {0}, to the field, {1}!", 2);

    private MsgResponse response;
    private String message;
    private int args;

    MsgTemplate(MsgResponse response, String message, int args) {
        this.response = response;
        this.message = message;
        this.args = args;
    }
}
