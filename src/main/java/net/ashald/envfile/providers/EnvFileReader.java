package net.ashald.envfile.providers;

// import lombok.AccessLevel;
// import lombok.NoArgsConstructor;
// import net.ashald.envfile.exceptions.InvalidEnvFileException;

import java.io.File;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;


/**
 * Execute given command with given environment, and return standard output
 * Throw {@link InvalidObjectException} in case file cannot be executed.
 */
@FunctionalInterface
public interface EnvFileReader {
    EnvFileReader DEFAULT = new Utf8Reader();

    String read(File file) throws InvalidObjectException;

    // @NoArgsConstructor(access = AccessLevel.PRIVATE)
    class Utf8Reader implements EnvFileReader {

        @Override
        public String read(File file) throws InvalidObjectException {
            if (file == null) {
                throw new InvalidObjectException("File is required!");
            }

            byte[] bytes;
            try {
                bytes = Files.readAllBytes(file.toPath());
            } catch (IOException e) {
                throw new InvalidObjectException(e.getMessage());
            }

            return new String(bytes, StandardCharsets.UTF_8);
        }
    }
}