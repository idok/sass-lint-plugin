package com.sasslint.inspection;

import org.jetbrains.annotations.NotNull;

public class SassScssLintInspection extends SassLintInspection {

    public static final String INSPECTION_SHORT_NAME = "SassScssLintInspection";

    @NotNull
    public String getShortName() {
        return INSPECTION_SHORT_NAME;
    }
}