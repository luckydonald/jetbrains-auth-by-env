package com.github.intfish123.authbyenv;

import com.intellij.database.access.DatabaseCredentials;
import com.intellij.database.dataSource.*;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.util.EnvironmentUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class AuthByEnvDatabaseAuthProvider implements DatabaseAuthProvider {
    private static final Logger log = Logger.getInstance(AuthByEnvDatabaseAuthProvider.class);

    public static final String PROP_USERNAME = "authbyenv_username";
    public static final String PROP_PASSWORD = "authbyenv_password";
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
        String username = getUsername(protoConnection);
        String password = getPassword(protoConnection);
        log.info("Username used: " + username);
        protoConnection.getConnectionProperties().put("user", username);
        protoConnection.getConnectionProperties().put("password", password);
        return CompletableFuture.completedFuture(protoConnection);
    }
    private String getUsername(ProtoConnection protoConnection){
        String username = protoConnection.getConnectionPoint().getAdditionalProperty(PROP_USERNAME);
        if(username == null || username.isBlank()){
            username = DEFAULT_ENV_USERNAME;
        }
        username = username.replace("$", "");
//        String val = System.getenv(username);
        String val = EnvironmentUtil.getValue(username);
        if(val != null && !val.isBlank()){
            return val;
        }
        throw new RuntimeException("username not defined");
    }
    private String getPassword(ProtoConnection protoConnection){
        String password = protoConnection.getConnectionPoint().getAdditionalProperty(PROP_PASSWORD);
        if(password == null || password.isBlank()){
            password = DEFAULT_ENV_PASSWORD;
        }
        password = password.replace("$", "");
        return EnvironmentUtil.getValue(password);
    }
}
