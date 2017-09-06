package com.obadiahpcrowe.stirling.cloud.enums;

import lombok.Getter;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 6/9/17 at 11:36 AM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.cloud.enums
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public enum MediaType {

    MP4(".mp4"),
    MP3(".mp3"),
    PNG(".png"),
    JPEG(".jpeg", ".jpg");

    private String[] extensions;

    MediaType(String... extensions) {
        this.extensions = extensions;
    }
}
