package com.github.intfish123.authbyenv;

import com.intellij.database.access.DatabaseCredentials;
import com.intellij.database.dataSource.*;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.util.EnvironmentUtil;
import net.ashald.envfile.platform.ProjectFileResolver;
import net.ashald.envfile.providers.EnvFileReader;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.InvalidObjectException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@SuppressWarnings("UnstableApiUsage")
public class AuthByEnvDatabaseAuthProvider implements DatabaseAuthProvider {
    private static final Logger log = Logger.getInstance(AuthByEnvDatabaseAuthProvider.class);
    private static final ProjectFileResolver projectFileResolver = ProjectFileResolver.DEFAULT;
    private static final EnvFileReader reader = EnvFileReader.DEFAULT;


    public static final String PROP_USERNAME = "authbyenv_username";
    public static final String PROP_PASSWORD = "authbyenv_password";
    public static final String PROP_FILEPATH = "authbyenv_filepath";
    public static final String DEFAULT_ENV_USERNAME = "DB_USERNAME";
    public static final String DEFAULT_ENV_PASSWORD = "DB_PASSWORD";

    @Override
    public @NonNls @NotNull String getId() {
        return "auth-by-env";
    }

    @Override
    public @Nls @NotNull String getDisplayName() {
        return "Auth By Env";
    }

    @Override
    public @Nullable AuthWidget createWidget(@Nullable Project project, @NotNull DatabaseCredentials credentials, @NotNull DatabaseConnectionConfig config) {
        return new AuthByEnvWidget();
    }

    @Override
    public ApplicabilityLevel.@NotNull Result getApplicability(@NotNull DatabaseConnectionPoint point, @NotNull ApplicabilityLevel level) {
        return ApplicabilityLevel.Result.APPLICABLE;
    }

    @Override
    public @Nullable CompletionStage<@NotNull ProtoConnection> intercept(@NotNull ProtoConnection protoConnection, boolean b) {
        @Nullable String filePath = getFilePath(protoConnection);
        Project project = ProjectManager.getInstance().getDefaultProject();
        @NotNull Map<String, String> env = getEnvironment(filePath, project);
        @Nullable String username = getUsername(protoConnection, env);
        @Nullable String password = getPassword(protoConnection, env);

        log.info("Username used: " + username);
        log.info("env-file used: " + (filePath != null ? filePath : "null"));
        log.info("env-env loaded: " + env);
        if (username != null) {
            protoConnection.getConnectionProperties().put("user", username);
        }
        if (password != null) {
            protoConnection.getConnectionProperties().put("password", password);
        }
        return CompletableFuture.completedFuture(protoConnection);
    }
    private @Nullable String getUsername(@NotNull ProtoConnection protoConnection, @NotNull Map<String, String> env) {
        return getEnv(protoConnection, env, "username");
    }
    private @Nullable String getPassword(ProtoConnection protoConnection, @NotNull Map<String, String> env){
        return getEnv(protoConnection, env, "password");
    }
    private static @Nullable String getEnv(
            @NotNull ProtoConnection protoConnection,
            @NotNull Map<String, String> env,
            @NotNull String type
    ) {
        String key, defaultKey;
        switch (type) {
            case "username":
                key = PROP_USERNAME;
                defaultKey = DEFAULT_ENV_PASSWORD;
                break;
            case "password":
                key = PROP_PASSWORD;
                defaultKey = DEFAULT_ENV_USERNAME;
                break;
            default:
                throw new IllegalArgumentException("Unsupported type: " + type);
        }
        String var = protoConnection.getConnectionPoint().getAdditionalProperty(key);
        if(var == null || var.isBlank()){
            var = defaultKey;
        }
        var = var.replace("$", "");
        String val = getEnv(var, env);
        if(val == null || val.isBlank()){
            return null;
        }
        return val;
    }
    private static @Nullable String getFilePath(ProtoConnection protoConnection) {
        String filePath = protoConnection.getConnectionPoint().getAdditionalProperty(PROP_FILEPATH);
        if(filePath == null || filePath.isBlank()){
            return null;
        }
        return filePath;
    }
    private static @NotNull Map<String, String> getEnvironment(@Nullable String path, @NotNull Project project) {
        if (path == null) {
            return new HashMap<>();
        }
        Optional<File> file = projectFileResolver.resolvePath(project, path);
        if(file.isEmpty()){
           return new HashMap<>();
        }
        File envFile = file.get();
        String[] data = null;
        try {
            data = reader.read(envFile).split("\r?\n\r?");
        } catch (InvalidObjectException ignored) {
        }
        if (data == null) {
            return new HashMap<>();
        }
        return EnvironmentUtil.parseEnv(data);
    }
    private static @Nullable String getEnv(@NotNull String var, @Nullable Map<String, String> env) {
        if (env == null || env.isEmpty() || !env.containsKey(var)) {
            return EnvironmentUtil.getValue(var);
        }
        return env.get(var);
    }
}
