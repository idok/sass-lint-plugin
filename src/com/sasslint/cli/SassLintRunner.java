package com.sasslint.cli;

import com.google.common.base.Strings;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.ProcessOutput;
import com.intellij.openapi.diagnostic.Logger;
import com.wix.nodejs.CLI;
import com.wix.nodejs.NodeRunner;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.concurrent.TimeUnit;

public final class SassLintRunner {
    private SassLintRunner() {
    }

    private static final Logger LOG = Logger.getInstance(SassLintRunner.class);

    private static final int TIME_OUT = (int) TimeUnit.SECONDS.toMillis(120L);

    public static class SassLintSettings {
        public String node;
        public String executablePath;
        public String rules;
        public String extensions;
        public String config;
        public String cwd;
        public String targetFile;
    }

    public static SassLintSettings buildSettings(@NotNull String cwd, @NotNull String path, @NotNull String node, @NotNull String executable, @Nullable String configFile, @Nullable String rulesdir, @Nullable String extensions) {
        SassLintSettings settings = new SassLintSettings();
        settings.cwd = cwd;
        settings.executablePath = executable;
        settings.node = node;
        settings.rules = rulesdir;
        settings.extensions = extensions;
        settings.config = configFile;
        settings.targetFile = path;
        return settings;
    }

    public static LintResult lint(@NotNull String cwd, @NotNull String path, @NotNull String node, @NotNull String executable, @Nullable String configFile, @Nullable String rulesdir, @Nullable String extensions) {
        return lint(buildSettings(cwd, path, node, executable, configFile,  rulesdir, extensions));
    }

    public static LintResult lint(@NotNull SassLintSettings settings) {
        LintResult result = new LintResult();
        try {
            GeneralCommandLine commandLine = createCommandLineLint(settings);
            ProcessOutput out = NodeRunner.execute(commandLine, TIME_OUT);
//            if (out.getExitCode() == 0) {
                result.errorOutput = out.getStderr();
                try {
                    if (Strings.isNullOrEmpty(out.getStdout())) {
                        LOG.debug("SASS-Lint Empty output");
                    } else {
                        result.sassLint = SassLint.read(out.getStdout());
                    }
                } catch (Exception e) {
                    LOG.error(e);
                    //result.errorOutput = out.getStdout();
                }
//            } else {
//                result.errorOutput = out.getStderr();
//            }
        } catch (Exception e) {
            e.printStackTrace();
            result.errorOutput = e.toString();
        }
        return result;
    }

    @NotNull
    private static ProcessOutput version(@NotNull SassLintSettings settings) throws ExecutionException {
        GeneralCommandLine commandLine = createCommandLine(settings);
        commandLine.addParameter("-V");
        return NodeRunner.execute(commandLine, TIME_OUT);
    }

    @NotNull
    public static String runVersion(@NotNull SassLintSettings settings) throws ExecutionException {
        if (!new File(settings.executablePath).exists()) {
            LOG.warn("Calling version with invalid sasslint exe " + settings.executablePath);
            return "";
        }
        ProcessOutput out = version(settings);
        if (out.getExitCode() == 0) {
            return out.getStdout().trim();
        }
        return "";
    }

    @NotNull
    private static GeneralCommandLine createCommandLine(@NotNull SassLintSettings settings) {
        return NodeRunner.createCommandLine(settings.cwd, settings.node, settings.executablePath);
    }

    @NotNull
    private static GeneralCommandLine createCommandLineLint(@NotNull SassLintSettings settings) {
        GeneralCommandLine commandLine = createCommandLine(settings);
        // TODO validate arguments (file exist etc)
        commandLine.addParameter(settings.targetFile);
        commandLine.addParameter("-v");
        commandLine.addParameter("-q");
        CLI.addParamIfNotEmpty(commandLine, "-c", settings.config);
        CLI.addParam(commandLine, "--format", "checkstyle");
        return commandLine;
    }
}
