package com.obadiahpcrowe.stirling.classes.obj;

import com.obadiahpcrowe.stirling.accounts.AccountManager;
import com.obadiahpcrowe.stirling.classes.enums.PostableType;
import com.obadiahpcrowe.stirling.classes.interfaces.StirlingPostable;
import com.obadiahpcrowe.stirling.resources.AttachableResource;

import java.util.List;
import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 5/9/17 at 11:15 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.classes.obj
 * Copyright (c) Obadiah Crowe 2017
 */
public class ClassNote implements StirlingPostable {

    private UUID poster;
    private PostableType type;
    private String time;
    private String date;
    private String title;
    private String content;
    private List<AttachableResource> images;
    private List<AttachableResource> videos;
    private List<AttachableResource> attachments;
    private List<String> links;
    private UUID uuid;

    public ClassNote(UUID poster, String time, String date, String title, String content, List<AttachableResource> images,
                     List<AttachableResource> videos, List<AttachableResource> attachments, List<String> links) {
        this.poster = poster;
        this.type = PostableType.CLASS_POST;
        this.time = time;
        this.date = date;
        this.title = title;
        this.content = content;
        this.images = images;
        this.videos = videos;
        this.attachments = attachments;
        this.links = links;
        this.uuid = UUID.randomUUID();
    }

    @Override
    public String getPoster() {
        return (String) AccountManager.getInstance().getField(AccountManager.getInstance().getAccount(poster), "displayName");
    }

    @Override
    public PostableType getType() {
        return type;
    }

    @Override
    public String getPostTime() {
        return time;
    }

    @Override
    public String getPostDate() {
        return date;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public List<AttachableResource> getImages() {
        return images;
    }

    @Override
    public List<AttachableResource> getVideos() {
        return videos;
    }

    @Override
    public List<AttachableResource> getAttachments() {
        return attachments;
    }

    @Override
    public List<String> getLinks() {
        return links;
    }

    @Override
    public UUID getHolderSection() {
        return uuid;
    }
}
