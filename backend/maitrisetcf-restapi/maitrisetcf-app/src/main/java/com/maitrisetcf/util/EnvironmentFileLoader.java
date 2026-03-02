package com.maitrisetcf.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class EnvironmentFileLoader {

    private static final String COMMENT_LINE_START_SYMBOL = "#";
    private static final String VARIABLE_AFFECTATION_OPERATOR = "=";

    public static void load() throws FileNotFoundException {
        try {
            Path envPath = resolveEnvPath();
            if (envPath != null) {
                Map<String, String> envProperties = buildEnvironmentVariables(envPath);
                addVariableToSystemEnvironmentVariable(envProperties);
            } else {
                log.error("No .env file found. Create one at the project root or set environment variables directly.");
            }
        } catch (IOException e) {
            throw new FileNotFoundException("Failed to load .env file.");
        }
    }

    private static Path resolveEnvPath() {
        Path cwd = Paths.get(System.getProperty("user.dir")).toAbsolutePath().normalize();
        List<Path> candidates = new ArrayList<>();

        // Current working directory
        candidates.add(cwd.resolve(".env"));

        // Legacy path (when running from other directories)
        candidates.add(cwd.resolve("backend").resolve("maitrisetcf-restapi").resolve(".env"));

        // Walk up a few levels to find project root .env (multi-module safe)
        Path current = cwd.getParent();
        for (int i = 0; i < 4 && current != null; i++) {
            candidates.add(current.resolve(".env"));
            current = current.getParent();
        }

        for (Path candidate : candidates) {
            if (Files.exists(candidate)) {
                log.info("Loading environment variables from: {}", candidate);
                return candidate;
            }
        }

        return null;
    }

    private static void addVariableToSystemEnvironmentVariable(Map<String, String> envProperties) {
        for (Map.Entry<String, String> entry : envProperties.entrySet()) {
            System.setProperty(entry.getKey(), entry.getValue()); // Sets as system properties
        }
    }

    private static Map<String, String> buildEnvironmentVariables(Path path) throws IOException {
        Map<String, String> envProperties = new HashMap<>();
        List<String> lines = Files.readAllLines(path);

        for (String line : lines) {
            if (!shouldSkipLine(line)) {
                String[] parts = line.split(VARIABLE_AFFECTATION_OPERATOR, 2);
                String key = parts[0].trim();
                String value = parts[1].trim();
                envProperties.put(key, value);
            }
        }
        return envProperties;
    }

    private static boolean shouldSkipLine(String line) {
        // Skip comments and empty lines
        return StringUtils.startsWith(line, COMMENT_LINE_START_SYMBOL)
                || !StringUtils.contains(line, VARIABLE_AFFECTATION_OPERATOR);
    }
}
