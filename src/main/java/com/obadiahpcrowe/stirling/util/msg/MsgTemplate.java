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
    ACCOUNT_DOES_NOT_EXIST(MsgResponse.USER_ERROR, "The account, {0}, does not exist!", 1),
    ACCOUNT_CREATED(MsgResponse.SUCCESS, "Account, {0}, has been created!", 1),
    ACCOUNT_DELETED(MsgResponse.SUCCESS, "Account, {0}, has been deleted!", 1),
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
    SCHOOL_ALREADY_SIGNED_IN(MsgResponse.SUCCESS, "You are already signed in!", 0),
    SCHOOL_NOT_SIGNED_IN(MsgResponse.USER_ERROR, "You are not signed in yet!", 0),

    POD_SIGN_IN(MsgResponse.SUCCESS, "You have signed into the pod for {0}!", 1),

    CONTACT_ADDED(MsgResponse.SUCCESS, "You have added, {0}, as a contact!", 1),
    CONTACT_REMOVED(MsgResponse.SUCCESS, "You have removed, {0}, as a contact!", 1),
    CONTACT_REQUESTED(MsgResponse.SUCCESS, "You have requested {0} to add you as a contact!", 1),
    CONTACT_REQUEST_RECEIVED(MsgResponse.SUCCESS, "You have received a contact request from {0}!", 1),

    LOCALE_SET(MsgResponse.SUCCESS, "You have set your locale to: {0}!", 1),

    MESSENGER_IS_TYPING(MsgResponse.SUCCESS, "{0} is typing..", 1),

    SACE_CREDS_SET(MsgResponse.SUCCESS, "Your SACE credentials have been added to {0}!", 1),
    SACE_CREDS_NOT_FOUND(MsgResponse.USER_ERROR, "Your SACE credentials were not found! Try adding them!", 0),

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
    SET_FIELD_TO_FIELD(MsgResponse.SUCCESS, "Set the value, {0}, to the field, {1}!", 2),

    PASSWORD_INCORRECT(MsgResponse.USER_ERROR, "The password for {0} is incorrect!", 1),

    LAPTOP_IS_HOTSWAP(MsgResponse.USER_ERROR, "Cannot set laptop to a hotswap!", 0),
    LAPTOP_NAME_SET(MsgResponse.SUCCESS, "Laptop set to {0}!", 1),
    LAPTOP_NAME_NOT_FOUND(MsgResponse.USER_ERROR, "Laptop name not found! Try setting it!", 0),

    FEEDBACK_CREATED(MsgResponse.SUCCESS, "You have posted feedback, with the id: {0}!", 1),
    FEEDBACK_DELETED(MsgResponse.SUCCESS, "You have deleted feedback, with the id: {0}!", 1),

    TUTOR_REGISTERED(MsgResponse.SUCCESS, "You have successfully registered as a tutor!", 0),
    TUTOR_ALREADY_REGISTERED(MsgResponse.USER_ERROR, "You have already registered as a tutor!", 0),
    TUTOR_ASSIGNED_TO(MsgResponse.SUCCESS, "You have assigned the tutor, {0}, to {1} on {2} at {3)!", 4),
    TUTOR_REQUEST_MADE(MsgResponse.SUCCESS, "You have requested a tutor at {0} on {1} for the following reason: {2}!", 3),
    TUTOR_REQUEST_DELETED(MsgResponse.SUCCESS, "The tutor request has been deleted!", 0),
    TUTOR_ASSIGNMENT_DELETED(MsgResponse.SUCCESS, "The tutor assignment has been deleted!", 0),

    SESSION_EXISTS(MsgResponse.USER_ERROR, "The session for, {0}, already exists!", 1),
    SESSION_CREATED(MsgResponse.SUCCESS, "Created session for: {0}", 1),

    STUDENT_ID_ADDED(MsgResponse.SUCCESS, "{0}'s student ID has been set to: {1}!",  2),
    STUDENT_ID_NOT_FOUND(MsgResponse.USER_ERROR, "Student ID not found for: {0}!", 1),

    BLOG_ALREADY_EXISTS(MsgResponse.USER_ERROR, "The blog, {0}, already exists!", 1),
    BLOG_CREATED(MsgResponse.SUCCESS, "The blog, {0}, has been created!", 1),
    BLOG_DOES_NOT_EXIST(MsgResponse.USER_ERROR, "The blog, {0}, does not exist!", 1),
    BLOG_FIELD_EDITED(MsgResponse.SUCCESS, "The field, {0}, was edited for the blog: {1}!", 2),
    BLOG_DELETED(MsgResponse.SUCCESS, "the blog, {0}, has been deleted!", 1);

    private MsgResponse response;
    private String message;
    private int args;

    MsgTemplate(MsgResponse response, String message, int args) {
        this.response = response;
        this.message = message;
        this.args = args;
    }
}
