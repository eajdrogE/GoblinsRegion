package Regula.goblinsRegion.commands.DBcommands;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class NationDataHandler {

    private static final String NATIONS_FOLDER = "towny_data/nations/";

    // Форматирование имени нации (например, замена пробелов на подчеркивания)
    public static String formatNationName(String nationName) {
        return nationName.replace(" ", "_");
    }

    // Загрузка данных нации из JSON-файла
    public static JsonObject loadNationData(String nationName) {
        String formattedNationName = formatNationName(nationName);
        String path = NATIONS_FOLDER + formattedNationName + ".json";
        return loadJsonFromFile(path);
    }

    // Сохранение данных нации в JSON-файл
    public static void saveNationData(JsonObject nationData, String nationName) {
        String formattedNationName = formatNationName(nationName);
        String path = NATIONS_FOLDER + formattedNationName + ".json";
        saveJsonToFile(nationData, path);
    }

    public static boolean updateNationProperty(String nationName, String propertyName, String propertyValue) {
        JsonObject nationData = loadNationData(nationName);

        if (nationData == null) {
            return false; // Нация не найдена
        }

        // Обновляем свойство
        nationData.addProperty(propertyName, propertyValue);

        // Сохраняем обновленный JSON
        saveNationData(nationData, nationName);

        return true;
    }

    // Проверка существования файла нации
    public static boolean nationExists(String nationName) {
        String formattedNationName = formatNationName(nationName);
        File file = new File(NATIONS_FOLDER + formattedNationName + ".json");
        return file.exists();
    }

    // Загрузка JSON из файла
    private static JsonObject loadJsonFromFile(String path) {
        try (FileReader reader = new FileReader(path)) {
            return JsonParser.parseReader(reader).getAsJsonObject();
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + path);
            e.printStackTrace();
            return null;
        }
    }

    // Сохранение JSON в файл
    private static void saveJsonToFile(JsonObject jsonData, String path) {
        try (FileWriter writer = new FileWriter(path)) {
            writer.write(jsonData.toString());
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении файла: " + path);
            e.printStackTrace();
        }
    }
}
