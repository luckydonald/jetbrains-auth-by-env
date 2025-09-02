package de.luckydonald.authbyenv;

import com.github.intfish123.authbyenv.MessageBundle;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.fileTypes.PlainTextLanguage;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.Icon;


public class EnvFileType extends LanguageFileType {
    public static final EnvFileType INSTANCE = new EnvFileType();

    private EnvFileType() {
        super(PlainTextLanguage.INSTANCE, true);
    }

    public @NotNull String getName() {
        return ".env";
    }

    public @NotNull String getDescription() {
        @NotNull String msg = MessageBundle.message("filetype.dot-env.description");
        return msg;
    }

    public @Nls @NotNull String getDisplayName() {
        @NotNull String msg = MessageBundle.message("filetype.dot-env.display.name");
        return msg;
    }

    public @NotNull String getDefaultExtension() {
        return "";
    }

    public Icon getIcon() {
        return AllIcons.FileTypes.Config;
    }
}
