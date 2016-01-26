package com.sasslint.config;

import com.intellij.json.JsonLanguage;
//import com.intellij.lang.javascript.json.JSONLanguageDialect;
import com.intellij.openapi.fileTypes.LanguageFileType;

import javax.swing.Icon;

import icons.SassLintIcons;
import org.jetbrains.annotations.NotNull;

public class SassLintConfigFileType extends LanguageFileType {
    public static final SassLintConfigFileType INSTANCE = new SassLintConfigFileType();
    public static final String SASS_LINT_CONFIG = ".sass-lint.yml";

    private SassLintConfigFileType() {
        super(JsonLanguage.INSTANCE); //JSONLanguageDialect.JSON
    }

    @NotNull
    public String getName() {
        return "SassLint";
    }

    @NotNull
    public String getDescription() {
        return "SassLint configuration file";
    }

    @NotNull
    public String getDefaultExtension() {
        return SASS_LINT_CONFIG;
    }

    @NotNull
    public Icon getIcon() {
        return SassLintIcons.SassLint;
    }
}