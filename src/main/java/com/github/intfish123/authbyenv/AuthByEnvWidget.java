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

import java.awt.*;

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
        panel.add(usernameText, createSimpleConstraints(0, 1, 3));

        panel.add(passwordLabel, createLabelConstraints(1, 0, passwordLabel.getPreferredSize().getWidth()));
        panel.add(passwordText, createSimpleConstraints(1, 1, 3));
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
