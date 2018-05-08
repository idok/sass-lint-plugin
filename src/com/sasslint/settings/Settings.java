package com.sasslint.settings;

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
        name = "SassLintProjectComponent",
        storages = @Storage("sasslintPlugin.xml")
)
public class Settings implements PersistentStateComponent<Settings> {
    public String configFile = "";
    public String rulesPath = "";
    public String builtinRulesPath = "";
    public String extensions = "";
    public String lintExecutable = "";
    public String nodeInterpreter;
    public boolean treatAllIssuesAsWarnings;
    public boolean pluginEnabled;

    protected Project project;

    public static Settings getInstance(Project project) {
        Settings settings = ServiceManager.getService(project, Settings.class);
        settings.project = project;
        return settings;
    }

    @Nullable
    @Override
    public Settings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull Settings state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public String getVersion() {
        return nodeInterpreter + lintExecutable + configFile + rulesPath + builtinRulesPath + extensions;
    }
}
