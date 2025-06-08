package Lobaster.aliasmod;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class ThemeManager {
    private static final Map<String, List<String>> themes = new ConcurrentHashMap<>();

    public static void loadThemes() {
        themes.clear();
        try {
            Path themesDir = FabricLoader.getInstance().getConfigDir().resolve("aliasmod");

            Files.createDirectories(themesDir);

            try (Stream<Path> files = Files.list(themesDir)) {
                files.filter(path -> path.toString().endsWith(".txt"))
                        .forEach(path -> {
                            try {
                                String themeName = path.getFileName().toString().replace(".txt", "");
                                List<String> words = Files.readAllLines(path, StandardCharsets.UTF_8);
                                words.removeIf(String::isBlank);
                                if (!words.isEmpty()) {
                                    themes.put(themeName, words);
                                }
                            } catch (IOException e) {
                                Aliasmod.LOGGER.error("Не вдалося прочитати файл теми: " + path, e);
                            }
                        });
            }
        } catch (IOException e) {
            Aliasmod.LOGGER.error("Не вдалося створити або отримати доступ до папки з темами!", e);
        }
    }

    public static List<String> getWordsForTheme(String themeName) {
        return themes.getOrDefault(themeName, Collections.emptyList());
    }

    public static List<String> getThemeNames() {
        return new ArrayList<>(themes.keySet());
    }
}