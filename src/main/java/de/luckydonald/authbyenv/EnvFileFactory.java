package de.luckydonald.authbyenv;

import com.intellij.openapi.fileTypes.ExactFileNameMatcher;
import com.intellij.openapi.fileTypes.ExtensionFileNameMatcher;
import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;
import com.intellij.openapi.fileTypes.WildcardFileNameMatcher;
import org.jetbrains.annotations.NotNull;

public class EnvFileFactory extends FileTypeFactory {
    @Override
    public void createFileTypes(@NotNull FileTypeConsumer fileTypeConsumer) {
        fileTypeConsumer.consume(EnvFileType.INSTANCE, new ExactFileNameMatcher(".env"));
        fileTypeConsumer.consume(EnvFileType.INSTANCE, new ExtensionFileNameMatcher("env"));
        fileTypeConsumer.consume(EnvFileType.INSTANCE, new WildcardFileNameMatcher(".env.*"));
    }
}
