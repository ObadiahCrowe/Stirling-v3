package com.obadiahpcrowe.stirling.classes.interfaces;

import com.obadiahpcrowe.stirling.classes.enums.PostableType;

import java.io.File;
import java.util.List;

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

    List<File> getImages();

    List<File> getVideos();

    List<File> getAttachments();

    List<String> getLinks();
}
