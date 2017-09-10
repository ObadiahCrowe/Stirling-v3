package com.obadiahpcrowe.stirling.classes.interfaces;

import com.obadiahpcrowe.stirling.classes.enums.PostableType;
import com.obadiahpcrowe.stirling.resources.AttachableResource;

import java.util.List;
import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 5/9/17 at 11:12 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.classes.interfaces
 * Copyright (c) Obadiah Crowe 2017
 */
public interface StirlingPostable {

    String getPoster();

    PostableType getType();

    String getPostTime();

    String getPostDate();

    String getTitle();

    String getContent();

    List<AttachableResource> getImages();

    List<AttachableResource> getVideos();

    List<AttachableResource> getAttachments();

    List<String> getLinks();

    UUID getHolderSection();
}
