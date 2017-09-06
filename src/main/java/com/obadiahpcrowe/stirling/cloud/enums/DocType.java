package com.obadiahpcrowe.stirling.cloud.enums;

import lombok.Getter;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 6/9/17 at 11:43 AM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.cloud.enums
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public enum DocType {

    PDF("application/pdf", ".pdf"),
    DOC("application/msword", ".doc"),
    DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document", ".docx"),
    PPT("application/vnd.openxmlformats-officedocument.presentationml.presentation", ".ppt", ".pptx"),
    XLS("application/vnd.ms-excel", ".xls", ".xlsx");

    private String contentType;
    private String[] extensions;

    DocType(String contentType, String... extensions) {
        this.contentType = contentType;
        this.extensions = extensions;
    }
}
