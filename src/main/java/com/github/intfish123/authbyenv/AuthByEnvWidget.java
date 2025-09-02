package com.github.intfish123.authbyenv;

import com.intellij.database.dataSource.DatabaseAuthProvider;
import com.intellij.database.dataSource.DatabaseConnectionConfig;
import com.intellij.database.dataSource.DatabaseConnectionPoint;
import com.intellij.database.dataSource.url.template.MutableParametersHolder;
import com.intellij.database.dataSource.url.template.ParametersHolder;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.TextFieldWithHistory;
import com.intellij.ui.TextFieldWithHistoryWithBrowseButton;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.ui.SwingHelper;
import de.luckydonald.authbyenv.EnvFileType;
import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.ProjectScope;
import com.intellij.util.NotNullProducer;

import javax.swing.*;
import java.util.*;

import java.awt.*;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class AuthByEnvWidget implements DatabaseAuthProvider.AuthWidget {
    private final JPanel panel;
    private final JBTextField usernameText;
    private final JBTextField passwordText;
    private final TextFieldWithHistoryWithBrowseButton filepathText;

    public AuthByEnvWidget() {
        panel = new JPanel(new GridLayoutManager(3, 6));
        usernameText = new JBTextField();
        passwordText = new JBTextField();
        Project project = ProjectManager.getInstance().getDefaultProject();
        filepathText = createConfigurationFileTextField(project);
        usernameText.getEmptyText().setText("Default: $DB_USERNAME");
        passwordText.getEmptyText().setText("Default: $DB_PASSWORD");
        // filepathText.getChildComponent().getTextEditor().getgetEmptyText().setText("Default: Use global environment variables.");

        final var usernameLabel = new JBLabel(MessageBundle.message("username"));
        final var passwordLabel = new JBLabel(MessageBundle.message("password"));
        final var filepathLabel = new JBLabel(MessageBundle.message("filepath"));

        panel.add(usernameLabel, createLabelConstraints(0, 0, usernameLabel.getPreferredSize().getWidth()));
        panel.add(usernameText, createSimpleConstraints(0, 1, 3));

        panel.add(passwordLabel, createLabelConstraints(1, 0, passwordLabel.getPreferredSize().getWidth()));
        panel.add(passwordText, createSimpleConstraints(1, 1, 3));

        panel.add(filepathLabel, createLabelConstraints(1, 0, filepathLabel.getPreferredSize().getWidth()));
        panel.add(filepathText, createSimpleConstraints(2, 1, 3));
    }

    @NotNull
    private static TextFieldWithHistoryWithBrowseButton createConfigurationFileTextField(@NotNull Project project) {
        TextFieldWithHistoryWithBrowseButton textFieldWithHistoryWithBrowseButton = new TextFieldWithHistoryWithBrowseButton();
        final TextFieldWithHistory textFieldWithHistory = textFieldWithHistoryWithBrowseButton.getChildComponent();
        textFieldWithHistory.setHistorySize(-1);
        textFieldWithHistory.setMinimumAndPreferredWidth(0);
        SwingHelper.addHistoryOnExpansion(textFieldWithHistory, new NotNullProducer<>() {
            @NotNull
            @Override
            public List<String> produce() {
                List<VirtualFile> newFiles = listPossibleConfigFilesInProject(project);
                List<String> newFilePaths = ContainerUtil.map(newFiles, file -> FileUtil.toSystemDependentName(file.getPath()));
                Collections.sort(newFilePaths);
                return newFilePaths;
            }
        });

        SwingHelper.installFileCompletionAndBrowseDialog(
                project,
                textFieldWithHistoryWithBrowseButton,
                MessageBundle.message("env_file.browse_dialog.title"),
                FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor()
        );
        return textFieldWithHistoryWithBrowseButton;
    }


    @NotNull
    private static List<VirtualFile> listPossibleConfigFilesInProject(@NotNull Project project) {
        GlobalSearchScope scope = ProjectScope.getContentScope(project);
        Collection<VirtualFile> files = FileTypeIndex.getFiles(EnvFileType.INSTANCE, scope);
        List<VirtualFile> result = new ArrayList<>();
        for (VirtualFile file : files) {
            if (file == null) {
                continue;
            }
            if (! file.isValid()) {
                continue;
            }
            if (file.isDirectory()) {
                continue;
            }
            if (! file.getName().contains(".env")) {
                continue;
            }
            String path = file.getPath();
            if (path.contains("/node_modules/")) {
                continue;
            }
            if (path.contains("/bower_components/")) {
                continue;
            }
            if (path.contains(".venv")) {
                continue;
            }
            result.add(file);
        }
        return result;
    }

    @Override
    public void onChanged(@NotNull Runnable runnable) {

    }

    @Override
    public void save(@NotNull DatabaseConnectionConfig config, boolean copyCredentials) {
        config.setAdditionalProperty(AuthByEnvDatabaseAuthProvider.PROP_USERNAME, usernameText.getText());
        config.setAdditionalProperty(AuthByEnvDatabaseAuthProvider.PROP_PASSWORD, passwordText.getText());
        config.setAdditionalProperty(AuthByEnvDatabaseAuthProvider.PROP_FILEPATH, filepathText.getText());
    }

    @Override
    public void reset(@NotNull DatabaseConnectionPoint point, boolean resetCredentials) {
        usernameText.setText(Optional.ofNullable(point.getAdditionalProperty(AuthByEnvDatabaseAuthProvider.PROP_USERNAME)).orElse(""));
        passwordText.setText(Optional.ofNullable(point.getAdditionalProperty(AuthByEnvDatabaseAuthProvider.PROP_PASSWORD)).orElse(""));
        filepathText.setText(Optional.ofNullable(point.getAdditionalProperty(AuthByEnvDatabaseAuthProvider.PROP_FILEPATH)).orElse(""));
    }

    @Override
    public boolean isPasswordChanged() {
        return false;
    }

    @Override
    public void hidePassword() {

    }

    @Override
    public void reloadCredentials() {

    }

    @Override
    public @NotNull JComponent getComponent() {
        return panel;
    }

    @Override
    public @NotNull JComponent getPreferredFocusedComponent() {
        return usernameText;
    }

    @Override
    public void forceSave() {

    }

    @Override
    public void updateFromUrl(@NotNull ParametersHolder parametersHolder) {

    }

    @Override
    public void updateUrl(@NotNull MutableParametersHolder mutableParametersHolder) {

    }

    public static GridConstraints createLabelConstraints(int row, int col, double width) {
        return createConstraints(row, col, 1, 0, 3, (int)width, false);
    }

    public static GridConstraints createSimpleConstraints(int row, int col, int colSpan) {
        return createConstraints(row, col, colSpan, 0, 1, -1, true);
    }

    public static GridConstraints createConstraints(int row, int col, int colSpan, int anchor, int fill, int prefWidth, boolean rubber) {
        return createConstraints(row, col, 1, colSpan, anchor, fill, prefWidth, rubber);
    }

    public static GridConstraints createConstraints(int row, int col, int rowSpan, int colSpan, int anchor, int fill, int prefWidth, boolean rubber) {
        return createConstraints(row, col, rowSpan, colSpan, anchor, fill, prefWidth, rubber, false);
    }

    public static GridConstraints createConstraints(int row, int col, int rowSpan, int colSpan, int anchor, int fill, int prefWidth, boolean rubber, boolean vrubber) {
        Dimension nonPref = new Dimension(-1, -1);
        Dimension pref = new Dimension(prefWidth == -1 ? 100 : prefWidth, -1);
        return new GridConstraints(row, col, rowSpan, colSpan, anchor, fill, getPolicy(rubber), getPolicy(vrubber), nonPref, pref, nonPref, 0, true);
    }

    public static int getPolicy(boolean rubber) {
        return rubber ? 7 : 0;
    }
}
