package com.obadiahpcrowe.stirling.modules.events;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 8/9/17 at 10:53 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.modules.events
 * Copyright (c) Obadiah Crowe 2017
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EventHandler {
}
