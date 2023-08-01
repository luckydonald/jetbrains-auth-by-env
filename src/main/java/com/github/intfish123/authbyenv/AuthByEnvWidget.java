package com.github.intfish123.authbyenv;

import com.intellij.database.dataSource.DatabaseAuthProvider;
import com.intellij.database.dataSource.DatabaseConnectionConfig;
import com.intellij.database.dataSource.DatabaseConnectionPoint;
import com.intellij.database.dataSource.url.template.MutableParametersHolder;
import com.intellij.database.dataSource.url.template.ParametersHolder;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

import static com.intellij.database.dataSource.url.ui.UrlPropertiesPanel.createLabelConstraints;

public class AuthByEnvWidget implements DatabaseAuthProvider.AuthWidget {
    private final JPanel panel;
    private final JBTextField usernameText;
    private final JBTextField passwordText;

    public AuthByEnvWidget() {
        panel = new JPanel(new GridLayoutManager(3, 6));
        usernameText = new JBTextField();
        passwordText = new JBTextField();
        usernameText.getEmptyText().setText("Default: $DB_USERNAME");
        passwordText.getEmptyText().setText("Default: $DB_PASSWORD");

        final var usernameLabel = new JBLabel(MessageBundle.message("username"));
        final var passwordLabel = new JBLabel(MessageBundle.message("password"));

        panel.add(usernameLabel, createLabelConstraints(0, 0, usernameLabel.getPreferredSize().getWidth()));
        panel.add(usernameText, createLabelConstraints(0, 1, 3));

        panel.add(passwordLabel, createLabelConstraints(1, 0, passwordLabel.getPreferredSize().getWidth()));
        panel.add(passwordText, createLabelConstraints(1, 1, 3));
    }

    @Override
    public void onChanged(@NotNull Runnable runnable) {

    }

    @Override
    public void save(@NotNull DatabaseConnectionConfig config, boolean copyCredentials) {
        config.setAdditionalProperty(AuthByEnvDatabaseAuthProvider.PROP_USERNAME, usernameText.getText());
        config.setAdditionalProperty(AuthByEnvDatabaseAuthProvider.PROP_PASSWORD, passwordText.getText());
    }

    @Override
    public void reset(@NotNull DatabaseConnectionPoint point, boolean resetCredentials) {
        usernameText.setText(point.getAdditionalProperty(AuthByEnvDatabaseAuthProvider.PROP_USERNAME));
        passwordText.setText(point.getAdditionalProperty(AuthByEnvDatabaseAuthProvider.PROP_PASSWORD));
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
}
