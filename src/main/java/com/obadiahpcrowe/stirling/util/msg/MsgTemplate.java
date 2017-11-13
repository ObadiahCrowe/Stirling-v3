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

    EMAIL_ADDRESS_IN_USE(MsgResponse.USER_ERROR, "The email address, {0}, is in use by another user!", 1),
    EMAIL_ADDRESS_INVALID_EXT(MsgResponse.USER_ERROR, "The email address, {0}, does not end in {1}!", 1),

    ANNOUNCEMENT_CREATED(MsgResponse.SUCCESS, "The announcement, {0}, has been posted!", 1),
    ANNOUNCEMENT_DELETED(MsgResponse.SUCCESS, "The announcement, {0}, has been deleted!", 1),
    ANNOUNCEMENT_EDITED(MsgResponse.SUCCESS, "The announcement, {0}, has been edited!", 1),
    ANNOUNCEMENT_DOES_NOT_EXIST(MsgResponse.USER_ERROR, "The announcement, {0}, does not exist!", 1),

    CALENDAR_ALREADY_EXISTS(MsgResponse.USER_ERROR, "Your calendar already exists!", 0),
    CALENDAR_TITLE_CHANGED(MsgResponse.SUCCESS, "You have changed this calendar's title to: {0}!", 1),
    CALENDAR_DESC_CHANGED(MsgResponse.SUCCESS, "You have changed this calendar's description to: {0}!", 1),
    CALENDAR_EVENT_FIELD_EDITED(MsgResponse.SUCCESS, "You changed the {0} for {1}!", 2),
    CALENDAR_ENTRY_ADDED(MsgResponse.SUCCESS, "A calendar entry has been added to: {0}!", 1),
    CALENDAR_ENTRY_DELETED(MsgResponse.SUCCESS, "A calendar entry has been removed from: {0}!", 1),
    CALENDAR_DOES_NOT_EXIST(MsgResponse.USER_ERROR, "The calendar, {0}, does not exist!", 1),
    CALENDAR_DELETED(MsgResponse.SUCCESS, "{0}'s calendar has been deleted!", 1),
    CALENDAR_CREATED(MsgResponse.SUCCESS, "{0}'s calendar has been created!", 1),
    CALENDAR_EVENT_EDITED(MsgResponse.SUCCESS, "The event, {0}, has been edited!", 1),
    CALENDAR_EVENTS_IMPORTED(MsgResponse.SUCCESS, "The events from, {0}, have been imported into your calendar!", 1),

    CLOUD_STORAGE_FULL(MsgResponse.USER_ERROR, "Your cloud storage is full!", 0),
    CLOUD_FILE_DOES_NOT_EXIST(MsgResponse.USER_ERROR, "The file, {0}, does not exist!", 1),

    NOTE_CREATED(MsgResponse.SUCCESS, "The note, {0}, has been created!", 1),
    NOTE_DELETED(MsgResponse.SUCCESS, "The note, {0}, has been deleted!", 1),
    NOTE_EDITED(MsgResponse.SUCCESS, "The note, {0}, has been edited!", 1),
    NOTE_DOES_NOT_EXIST(MsgResponse.SUCCESS, "The note, with the id: {0}, does not exist!", 1),

    SCHOOL_SIGN_IN(MsgResponse.SUCCESS, "You have signed into school with the following reason: {0}!", 1),
    SCHOOL_SIGN_OUT(MsgResponse.SUCCESS, "You have signed out of school with the following reason: {0}!", 1),
    SCHOOL_ALREADY_SIGNED_IN(MsgResponse.SUCCESS, "You are already signed in!", 0),
    SCHOOL_NOT_SIGNED_IN(MsgResponse.USER_ERROR, "You are not signed in yet!", 0),

    POD_SIGN_IN(MsgResponse.SUCCESS, "You have signed into the pod for {0}!", 1),

    CLASS_CREATED(MsgResponse.SUCCESS, "You have created the class, {0}!", 1),
    CLASS_DELETED(MsgResponse.SUCCESS, "You have deleted the class, {0}!", 1),
    CLASS_ALREADY_EXISTS(MsgResponse.USER_ERROR, "The class, {0}, already exists!", 1),
    CLASS_DOES_NOT_EXIST(MsgResponse.USER_ERROR, "The class, with the id: {0}, does not exist!", 1),
    CLASS_NOT_OWNER(MsgResponse.USER_ERROR, "You are not an owner of this class!", 0),
    CLASS_TEACHERS_ADDED(MsgResponse.SUCCESS, "You have added teachers to the class: {0}!", 1),
    CLASS_TEACHERS_REMOVED(MsgResponse.SUCCESS, "You have removed teachers from the class: {0}!", 1),
    CLASS_ROOM_CHANGED(MsgResponse.SUCCESS, "You have changed {0}'s room to: {1}!", 2),
    CLASS_NAME_CHANGED(MsgResponse.SUCCESS, "You have changed {0}'s name to: {1}!", 2),
    CLASS_DESC_CHANGED(MsgResponse.SUCCESS, "You have changed {0}'s description to: {1}!", 2),
    CLASS_SECTION_ADDED(MsgResponse.SUCCESS, "You have added the section, {0}, to {1}!", 2),
    CLASS_SECTION_REMOVED(MsgResponse.SUCCESS, "You have removed the section, with the ID: {0}, from {1}!", 2),
    CLASS_SECTION_DOES_NOT_EXIST(MsgResponse.USER_ERROR, "The section with the ID: {0}, does not exist!", 1),
    CLASS_POSTABLE_ADDED(MsgResponse.SUCCESS, "You have created a postable in: {0}!", 1),
    CLASS_POSTABLE_REMOVED(MsgResponse.SUCCESS, "You have removed a postable from: {0}!", 1),
    CLASS_CATCHUP_ADDED(MsgResponse.SUCCESS, "You have added a catchup module to: {0}!", 1),
    CLASS_CATCHUP_REMOVED(MsgResponse.SUCCESS, "You have removed a catchup module from: {0}!", 1),
    CLASS_ASSIGNMENT_ADDED(MsgResponse.SUCCESS, "You have created an assignment for all students in {0}!", 1),
    CLASS_ASSIGNMENT_REMOVED_SINGLE(MsgResponse.SUCCESS, "You have removed the assignment, {0}, for {1}!", 2),
    CLASS_ASSIGNMENT_REMOVED_ALL(MsgResponse.SUCCESS, "You have removed the assignment, {0}, for the whole class!", 1),
    CLASS_ASSIGNMENT_DOES_NOT_EXIST(MsgResponse.USER_ERROR, "The assignment with the ID: {0}, does not exist!", 1),
    CLASS_ASSIGNMENT_SUBMITTED(MsgResponse.SUCCESS, "The assignment, {0}, has been submitted!", 1),
    CLASS_STUDENT_ADDED(MsgResponse.SUCCESS, "The student, with the ID: {0}, has been added to {1}!", 2),
    CLASS_STUDENT_REMOVED(MsgResponse.SUCCESS, "The student, with the ID: {0}, has been removed from {1}!", 2),
    CLASS_MULTIPLE_STUDENTS_ADDED(MsgResponse.SUCCESS, "You have added multiple students to {0}!", 1),
    CLASS_MULTIPLE_STUDENTS_REMOVED(MsgResponse.SUCCESS, "You have removed multiple students from {0}!", 1),
    CLASS_HOMEWORK_ADDED(MsgResponse.SUCCESS, "You have created a homework task in {0}!", 1),
    CLASS_HOMEWORK_REMOVED(MsgResponse.SUCCESS, "You have deleted a homework task in {0}!", 1),
    CLASS_HOMEWORK_DOES_NOT_EXIST(MsgResponse.USER_ERROR, "This homework task does not exist!", 0),
    CLASS_NOTE_ADDED(MsgResponse.SUCCESS, "You have created a class not in {0}!", 1),
    CLASS_NOTE_REMOVED(MsgResponse.SUCCESS, "You have deleted a class note in {0}!", 1),
    CLASS_NOTE_DOES_NOT_EXIST(MsgResponse.USER_ERROR, "This class note does not exist!", 0),
    CLASS_RESOURCE_ADDED(MsgResponse.SUCCESS, "You have added the resource, {0}, to {1}!", 2),
    CLASS_RESOURCE_DELETED(MsgResponse.SUCCESS, "You have deleted the resource, {0}!", 1),
    CLASS_RESOURCE_DOES_NOT_EXIST(MsgResponse.USER_ERROR, "This resource does not exist!", 0),
    CLASS_ASSIGNMENT_MARKED(MsgResponse.SUCCESS, "You have marked an assignment for {0}!", 1),
    CLASS_PROGRESS_MARKER_ADDED(MsgResponse.SUCCESS, "You have created a progress marker for {0}!", 1),
    CLASS_PROGRESS_MARKER_DOES_NOT_EXIST(MsgResponse.USER_ERROR, "This progress marker does not exist!", 0),
    CLASS_PROGRESS_MARKED_REMOVED(MsgResponse.SUCCESS, "You have removed a progress marker from {0}!", 1),
    CLASS_LESSON_DOES_NOT_EXIST(MsgResponse.USER_ERROR, "The lesson with the id: {0}, does not exist!", 1),
    CLASS_ATTENDANCE_SET(MsgResponse.SUCCESS, "You have set {0}'s attendance to {1}!", 2),
    CLASS_LESSONS_GENERATED(MsgResponse.SUCCESS, "Lessons have been generated for {0}!", 1),
    CLASS_OWNERSHIP_TAKEN(MsgResponse.SUCCESS, "You have taken ownership of {0}!", 1),
    CLASS_ADDED_TO_ACCOUNT(MsgResponse.SUCCESS, "The class, {0}, has been added to: {1}!", 2),

    IMPORT_ACCOUNT_CONTAINS_CRED(MsgResponse.USER_ERROR, "You already have credentials set for {0}!", 1),
    IMPORT_ACCOUNT_CREDS_SET(MsgResponse.SUCCESS, "You have added credentials for {0}!", 1),
    IMPORT_ACCOUNT_CREDS_REMOVED(MsgResponse.SUCCESS, "You have removed credentials for {0}!", 1),
    IMPORT_ACCOUNT_DOES_NOT_EXIST(MsgResponse.USER_ERROR, "You have no credentials set!", 1),
    IMPORT_ACCOUNT_CANNOT_ADD(MsgResponse.USER_ERROR, "You cannot add Google Classroom credentials with this method!", 0),

    STUDENT_NOT_IN_CLASS(MsgResponse.USER_ERROR, "You are not a member of this class!", 0),

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
    FIELD_TOO_SHORT(MsgResponse.USER_ERROR, "The {0} length is too short! The minimum length is: {1}!", 2),

    INCOMPATIBLE_VALUE(MsgResponse.USER_ERROR, "The value, {0}, is incompatible with {1}!", 2),
    UNKNOWN_FIELD(MsgResponse.USER_ERROR, "The field, {0}, is invalid!", 1),

    UNEXPECTED_ERROR(MsgResponse.STIRLING_ERROR, "An unexpected error occurred while {0}! Try again later.", 1),

    DOWNLOADING_FILE(MsgResponse.SUCCESS, "Downloading {0}..", 1),
    UPLOADING_FILE(MsgResponse.SUCCESS, "Uploading {0}..", 1),
    SUBMITTING_FILE(MsgResponse.SUCCESS, "Submitting {0}..", 1),
    CANNOT_RENAME_FILE_EXISTS(MsgResponse.USER_ERROR, "Cannot rename: {0}, as the name: {1} is already taken. Either delete {1}, or rename {0} to something different!", 2),
    FILE_RENAMED(MsgResponse.SUCCESS, "The file, {0}, has been renamed to: {1}!", 2),
    FILE_ALREADY_EXISTS(MsgResponse.USER_ERROR, "The file, {0}, already exists!", 1),
    FILE_DELETED(MsgResponse.SUCCESS, "The file, {0}, has been deleted!", 1),
    FILE_MOVED(MsgResponse.SUCCESS, "The file, {0}, has been moved!", 1),

    FOLDER_ALREADY_EXISTS(MsgResponse.USER_ERROR, "The folder, {0}, already exists!", 1),
    FOLDER_CREATED(MsgResponse.SUCCESS, "The folder, {0}, has been created!", 1),
    FOLDER_DELETED(MsgResponse.SUCCESS, "The folder, {0}, has been deleted!", 1),

    CANNOT_SET_FIELD_TO_FIELD(MsgResponse.USER_ERROR, "Cannot apply the value, {0}, to the field, {1}!", 2),
    SET_FIELD_TO_FIELD(MsgResponse.SUCCESS, "Set the field, {0}, to the value, {1}!", 2),

    PASSWORD_INCORRECT(MsgResponse.USER_ERROR, "The password for {0} is incorrect!", 1),

    LAPTOP_IS_HOTSWAP(MsgResponse.USER_ERROR, "Cannot set laptop to a hotswap!", 0),
    LAPTOP_NAME_SET(MsgResponse.SUCCESS, "Laptop set to {0}!", 1),
    LAPTOP_NAME_NOT_FOUND(MsgResponse.USER_ERROR, "Laptop name not found! Try setting it!", 0),

    FEEDBACK_CREATED(MsgResponse.SUCCESS, "You have posted feedback, with the id: {0}!", 1),
    FEEDBACK_DELETED(MsgResponse.SUCCESS, "You have deleted feedback, with the id: {0}!", 1),

    TUTOR_REGISTERED(MsgResponse.SUCCESS, "You have successfully registered as a tutor!", 0),
    TUTOR_ALREADY_REGISTERED(MsgResponse.USER_ERROR, "You have already registered as a tutor!", 0),
    TUTOR_UNREGISTERED(MsgResponse.SUCCESS, "You have successfully unregistered, {0}, as a tutor!", 1),
    TUTOR_DOES_NOT_EXIST(MsgResponse.USER_ERROR, "The tutor with the ID, {0}, does not exist!", 1),
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
    BLOG_DOES_NOT_EXIST(MsgResponse.USER_ERROR, "The blog id, {0}, does not exist!", 1),
    BLOG_FIELD_EDITED(MsgResponse.SUCCESS, "The field, {0}, was edited for the blog: {1}!", 2),
    BLOG_DELETED(MsgResponse.SUCCESS, "the blog, {0}, has been deleted!", 1),

    BLOG_POST_CREATED(MsgResponse.SUCCESS, "You have created a blog post with the title: {0}, on the blog: {1}!", 2),
    BLOG_POST_DELETED(MsgResponse.SUCCESS, "You have deleted a blog post with the id: {0}, on the blog: {1}!", 2),
    BLOG_POST_DOES_NOT_EXIST(MsgResponse.USER_ERROR, "The blog post id, {0}, does not exist!", 1),

    NOT_OWNER(MsgResponse.USER_ERROR, "You are not the owner of {0}! You are unable to {1}!", 2),

    SURVEY_CREATED(MsgResponse.SUCCESS, "You have created a survey with the title: {0}!", 1),
    SURVEY_DELETED(MsgResponse.SUCCESS, "You have deleted the survey, {0}!", 1),
    SURVEY_SUBMITTED(MsgResponse.SUCCESS, "You have submitted your responses to the survey: {0}!", 1),
    SURVEY_ALREADY_SUBMITTED(MsgResponse.USER_ERROR, "You have already submitted a response to this survey!", 0),
    SURVEY_UPDATED(MsgResponse.SUCCESS, "The survey, {0}, has had the field, {1}, updated!", 2),
    SURVEY_DOES_NOT_EXIST(MsgResponse.USER_ERROR, "The survey with the id: {0}, does not exist!", 1),

    INVALID_TYPE_FORMAT(MsgResponse.USER_ERROR, "{0} is invalid! The required format is {1}!", 2),

    IMPORT_CREDS_INVALID(MsgResponse.USER_ERROR, "You credentials for {0} are invalid!", 1),
    IMPORT_CANNOT_REFRESH_TOKEN(MsgResponse.STIRLING_ERROR, "Stirling could not refresh your Google access token. Does it still have access?", 0),
    IMPORT_REFRESH_INVALID(MsgResponse.STIRLING_ERROR, "Your refresh token has expired!", 0),
    IMPORT_CANNOT_VERIFY(MsgResponse.USER_ERROR, "We are unable to verify your {0} credentials!", 1);

    private MsgResponse response;
    private String message;
    private int args;

    MsgTemplate(MsgResponse response, String message, int args) {
        this.response = response;
        this.message = message;
        this.args = args;
    }
}
