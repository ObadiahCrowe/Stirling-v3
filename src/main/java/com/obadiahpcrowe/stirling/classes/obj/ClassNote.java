package com.obadiahpcrowe.stirling.classes.obj;

import com.obadiahpcrowe.stirling.accounts.AccountManager;
import com.obadiahpcrowe.stirling.classes.enums.PostableType;
import com.obadiahpcrowe.stirling.classes.interfaces.StirlingPostable;
import com.obadiahpcrowe.stirling.cloud.interfaces.CloudDocument;
import com.obadiahpcrowe.stirling.cloud.interfaces.CloudMedia;

import com.obadiahpcrowe.stirling.cloud.interfaces.CloudMedia;
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
    private List<CloudMedia> images;
    private List<CloudMedia> videos;
    private List<CloudDocument> attachments;
    private List<String> links;
    private UUID uuid;

    public ClassNote(UUID poster, String time, String date, String title, String content, List<CloudMedia> images,
                     List<CloudMedia> videos, List<CloudDocument> attachments, List<String> links, UUID uuid) {
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
        this.uuid = uuid;
    }

    @Override
    public String getPoster() {
        return (String) AccountManager.getInstance().getField("displayName", poster);
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
    public List<CloudMedia> getImages() {
        return images;
    }

    @Override
    public List<CloudMedia> getVideos() {
        return videos;
    }

    @Override
    public List<CloudDocument> getAttachments() {
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
