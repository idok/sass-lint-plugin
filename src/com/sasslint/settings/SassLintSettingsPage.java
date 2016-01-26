package com.sasslint.settings;

import com.sasslint.SassLintProjectComponent;
import com.sasslint.cli.SassLintFinder;
import com.sasslint.cli.SassLintRunner;
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.execution.ExecutionException;
//import com.intellij.javascript.nodejs.NodeDetectionUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.HyperlinkLabel;
import com.intellij.ui.TextFieldWithHistory;
import com.intellij.ui.TextFieldWithHistoryWithBrowseButton;
import com.intellij.util.NotNullProducer;
import com.intellij.util.ui.UIUtil;
import com.intellij.webcore.ui.SwingHelper;
import com.wix.nodejs.NodeDetectionUtil;
import com.wix.settings.ValidationInfo;
import com.wix.ui.PackagesNotificationPanel;
import com.wix.utils.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SassLintSettingsPage implements Configurable {
    public static final String FIX_IT = "Fix it";
    public static final String HOW_TO_USE_SASS_LINT = "How to Use SassLint";
    public static final String HOW_TO_USE_LINK = "https://github.com/idok/sass-lint-plugin";
    protected Project project;

    private JCheckBox pluginEnabledCheckbox;
    private JTextField customRulesPathField;
    private JPanel panel;
    private JPanel errorPanel;
    private TextFieldWithHistoryWithBrowseButton sasslintBinField;
    private TextFieldWithHistoryWithBrowseButton nodeInterpreterField;
    private TextFieldWithHistoryWithBrowseButton sasslintrcFile;
    private JRadioButton searchForLintrcInRadioButton;
    private JRadioButton useProjectLintrcRadioButton;
    private HyperlinkLabel usageLink;
    private JLabel sassLintConfigFilePathLabel;
    private JLabel rulesDirectoryLabel;
    private JLabel pathToSasslintBinLabel;
    private JLabel nodeInterpreterLabel;
    private JLabel versionLabel;
    //    private TextFieldWithHistoryWithBrowseButton rulesPathField;
//    private JLabel rulesDirectoryLabel1;
    private final PackagesNotificationPanel packagesNotificationPanel;

    public SassLintSettingsPage(@NotNull final Project project) {
        this.project = project;
        configLintBinField();
        configConfigFileField();
//        configESLintRulesField();
        configNodeField();
//        searchForLintrcInRadioButton.addItemListener(new ItemListener() {
//            public void itemStateChanged(ItemEvent e) {
//                sasslintrcFile.setEnabled(e.getStateChange() == ItemEvent.DESELECTED);
//                System.out.println("searchForLintrcInRadioButton: " + (e.getStateChange() == ItemEvent.SELECTED ? "checked" : "unchecked"));
//            }
//        });
        useProjectLintrcRadioButton.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                sasslintrcFile.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
//                System.out.println("useProjectLintrcRadioButton: " + (e.getStateChange() == ItemEvent.SELECTED ? "checked" : "unchecked"));
            }
        });
        pluginEnabledCheckbox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                boolean enabled = e.getStateChange() == ItemEvent.SELECTED;
                setEnabledState(enabled);
            }
        });

        this.packagesNotificationPanel = new PackagesNotificationPanel(project);
//        GridConstraints gridConstraints = new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
//                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW,
//                null, new Dimension(250, 150), null);
        errorPanel.add(this.packagesNotificationPanel.getComponent(), BorderLayout.CENTER);

        DocumentAdapter docAdp = new DocumentAdapter() {
            protected void textChanged(DocumentEvent e) {
                updateLaterInEDT();
            }
        };
        sasslintBinField.getChildComponent().getTextEditor().getDocument().addDocumentListener(docAdp);
        sasslintrcFile.getChildComponent().getTextEditor().getDocument().addDocumentListener(docAdp);
        nodeInterpreterField.getChildComponent().getTextEditor().getDocument().addDocumentListener(docAdp);
//        rulesPathField.getChildComponent().getTextEditor().getDocument().addDocumentListener(docAdp);
        customRulesPathField.getDocument().addDocumentListener(docAdp);
    }

    private File getProjectPath() {
        return new File(project.getBaseDir().getPath());
    }

    private void updateLaterInEDT() {
        UIUtil.invokeLaterIfNeeded(new Runnable() {
            public void run() {
                SassLintSettingsPage.this.update();
            }
        });
    }

    private void update() {
        ApplicationManager.getApplication().assertIsDispatchThread();
        validate();
    }

    private void setEnabledState(boolean enabled) {
        sasslintrcFile.setEnabled(enabled);
        customRulesPathField.setEnabled(enabled);
        searchForLintrcInRadioButton.setEnabled(enabled);
        useProjectLintrcRadioButton.setEnabled(enabled);
        sasslintBinField.setEnabled(enabled);
        nodeInterpreterField.setEnabled(enabled);
        sassLintConfigFilePathLabel.setEnabled(enabled);
        rulesDirectoryLabel.setEnabled(enabled);
        pathToSasslintBinLabel.setEnabled(enabled);
        nodeInterpreterLabel.setEnabled(enabled);
    }

    private void validateField(List<ValidationInfo> errors, TextFieldWithHistoryWithBrowseButton field, boolean allowEmpty, String message) {
        if (!validatePath(field.getChildComponent().getText(), allowEmpty)) {
            ValidationInfo error = new ValidationInfo(field.getChildComponent().getTextEditor(), message, FIX_IT);
            errors.add(error);
        }
    }

    private void validate() {
        if (!pluginEnabledCheckbox.isSelected()) {
            return;
        }
        List<ValidationInfo> errors = new ArrayList<ValidationInfo>();
        validateField(errors, sasslintBinField, false, "Path to sasslint is invalid {{LINK}}");
        validateField(errors, sasslintrcFile, true, "Path to config file is invalid {{LINK}}"); //Please correct path to
        validateField(errors, nodeInterpreterField, false, "Path to node interpreter is invalid {{LINK}}");
        if (!validateDirectory(customRulesPathField.getText(), true)) {
            ValidationInfo error = new ValidationInfo(customRulesPathField, "Path to custom rules is invalid {{LINK}}", FIX_IT);
            errors.add(error);
        }
        if (errors.isEmpty()) {
            getVersion();
        }
        packagesNotificationPanel.processErrors(errors);
    }

    private SassLintRunner.SassLintSettings settings;

    private void getVersion() {
        if (settings != null &&
            areEqual(nodeInterpreterField, settings.node) &&
            areEqual(sasslintBinField, settings.executablePath) &&
            settings.cwd.equals(project.getBasePath())
                ) {
            return;
        }
        settings = new SassLintRunner.SassLintSettings();
        settings.node = nodeInterpreterField.getChildComponent().getText();
        settings.executablePath = sasslintBinField.getChildComponent().getText();
        settings.cwd = project.getBasePath();
        try {
            String version = SassLintRunner.runVersion(settings);
            versionLabel.setText(version.trim());
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private boolean validatePath(String path, boolean allowEmpty) {
        if (StringUtils.isEmpty(path)) {
            return allowEmpty;
        }
        File filePath = new File(path);
        if (filePath.isAbsolute()) {
            if (!filePath.exists() || !filePath.isFile()) {
                return false;
            }
        } else {
            VirtualFile child = project.getBaseDir().findFileByRelativePath(path);
            if (child == null || !child.exists() || child.isDirectory()) {
                return false;
            }
        }
        return true;
    }

    private boolean validateDirectory(String path, boolean allowEmpty) {
        if (StringUtils.isEmpty(path)) {
            return allowEmpty;
        }
        File filePath = new File(path);
        if (filePath.isAbsolute()) {
            if (!filePath.exists() || !filePath.isDirectory()) {
                return false;
            }
        } else {
            VirtualFile child = project.getBaseDir().findFileByRelativePath(path);
            if (child == null || !child.exists() || !child.isDirectory()) {
                return false;
            }
        }
        return true;
    }

    private static TextFieldWithHistory configWithDefaults(TextFieldWithHistoryWithBrowseButton field) {
        TextFieldWithHistory textFieldWithHistory = field.getChildComponent();
        textFieldWithHistory.setHistorySize(-1);
        textFieldWithHistory.setMinimumAndPreferredWidth(0);
        return textFieldWithHistory;
    }

    private void configLintBinField() {
        configWithDefaults(sasslintBinField);
        SwingHelper.addHistoryOnExpansion(sasslintBinField.getChildComponent(), new NotNullProducer<List<String>>() {
            @NotNull
            public List<String> produce() {
                List<File> newFiles = SassLintFinder.searchForSassLintExe(getProjectPath());
                return FileUtils.toAbsolutePath(newFiles);
            }
        });
        SwingHelper.installFileCompletionAndBrowseDialog(project, sasslintBinField, "Select SassLint cli", FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor());
    }

//    private void configESLintRulesField() {
//        TextFieldWithHistory textFieldWithHistory = rulesPathField.getChildComponent();
//        SwingHelper.addHistoryOnExpansion(textFieldWithHistory, new NotNullProducer<List<String>>() {
//            @NotNull
//            public List<String> produce() {
//                return SassLintFinder.tryFindRulesAsString(getProjectPath());
//            }
//        });
//        SwingHelper.installFileCompletionAndBrowseDialog(project, rulesPathField, "Select Built in rules", FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor());
//    }

    private void configConfigFileField() {
        TextFieldWithHistory textFieldWithHistory = configWithDefaults(sasslintrcFile);
        SwingHelper.addHistoryOnExpansion(textFieldWithHistory, new NotNullProducer<List<String>>() {
            @NotNull
            public List<String> produce() {
                return SassLintFinder.searchForConfigFiles(getProjectPath());
            }
        });
        SwingHelper.installFileCompletionAndBrowseDialog(project, sasslintrcFile, "Select SassLint config", FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor());
    }

    private void configNodeField() {
        TextFieldWithHistory textFieldWithHistory = configWithDefaults(nodeInterpreterField);
        SwingHelper.addHistoryOnExpansion(textFieldWithHistory, new NotNullProducer<List<String>>() {
            @NotNull
            public List<String> produce() {
                List<File> newFiles = NodeDetectionUtil.listAllPossibleNodeInterpreters();
                return FileUtils.toAbsolutePath(newFiles);
            }
        });
        SwingHelper.installFileCompletionAndBrowseDialog(project, nodeInterpreterField, "Select Node interpreter", FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor());
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "SassLint";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        loadSettings();
        return panel;
    }

    private static boolean areEqual(TextFieldWithHistoryWithBrowseButton field, String value) {
        return field.getChildComponent().getText().equals(value);
    }

    @Override
    public boolean isModified() {
        Settings s = getSettings();
        return pluginEnabledCheckbox.isSelected() != s.pluginEnabled ||
                !areEqual(sasslintBinField, s.lintExecutable) ||
                !areEqual(nodeInterpreterField, s.nodeInterpreter) ||
                !customRulesPathField.getText().equals(s.rulesPath) ||
                !getConfigFile().equals(s.configFile);
    }

    private String getConfigFile() {
        return useProjectLintrcRadioButton.isSelected() ? sasslintrcFile.getChildComponent().getText() : "";
    }

    @Override
    public void apply() throws ConfigurationException {
        saveSettings();
        PsiManager.getInstance(project).dropResolveCaches();
    }

    protected void saveSettings() {
        Settings settings = getSettings();
        settings.pluginEnabled = pluginEnabledCheckbox.isSelected();
        settings.lintExecutable = sasslintBinField.getChildComponent().getText();
        settings.nodeInterpreter = nodeInterpreterField.getChildComponent().getText();
        settings.configFile = getConfigFile();
        settings.rulesPath = customRulesPathField.getText();
        project.getComponent(SassLintProjectComponent.class).validateSettings();
        DaemonCodeAnalyzer.getInstance(project).restart();
    }

    protected void loadSettings() {
        Settings settings = getSettings();
        pluginEnabledCheckbox.setSelected(settings.pluginEnabled);
        sasslintBinField.getChildComponent().setText(settings.lintExecutable);
        sasslintrcFile.getChildComponent().setText(settings.configFile);
        nodeInterpreterField.getChildComponent().setText(settings.nodeInterpreter);
        customRulesPathField.setText(settings.rulesPath);
        useProjectLintrcRadioButton.setSelected(StringUtils.isNotEmpty(settings.configFile));
        searchForLintrcInRadioButton.setSelected(StringUtils.isEmpty(settings.configFile));
        sasslintrcFile.setEnabled(useProjectLintrcRadioButton.isSelected());
        setEnabledState(settings.pluginEnabled);
    }

    @Override
    public void reset() {
        loadSettings();
    }

    @Override
    public void disposeUIResources() {
    }

    protected Settings getSettings() {
        return Settings.getInstance(project);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        usageLink = SwingHelper.createWebHyperlink(HOW_TO_USE_SASS_LINT, HOW_TO_USE_LINK);
    }
}
