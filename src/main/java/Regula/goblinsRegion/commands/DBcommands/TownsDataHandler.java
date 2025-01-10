package Regula.goblinsRegion.commands.DBcommands;

import com.google.gson.JsonParser;
import com.google.gson.JsonObject;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class TownsDataHandler {

    // Функция для преобразования имени города: замена пробелов на нижнее подчеркивание
    public static String formatCityName(String cityName) {
        return cityName.replace(" ", "_");
    }

    // Функция для получения данных региона по имени города
    public static JsonObject getRegionData(String cityName) {
        String formattedCityName = formatCityName(cityName);
        String path = "towny_data/towns/" + formattedCityName + ".json";
        return loadJsonFromFile(path);
    }

    // Функция для получения данных о зданиях региона по имени города
    public static JsonObject getRegionBuildings(String cityName) {
        String formattedCityName = formatCityName(cityName);
        String path = "towny_data/towns_buildings/" + formattedCityName + ".json";
        return loadJsonFromFile(path);
    }



    // Новый метод для парсинга JSON, теперь принимает строку пути
    public static JsonObject parseJson(String filePath) {
        try (FileReader reader = new FileReader(filePath)) {
            return JsonParser.parseReader(reader).getAsJsonObject(); // Преобразуем в JsonObject
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + filePath);
            e.printStackTrace();
            return null;
        }
    }

    // Функция для загрузки JSON файла с указанного пути
    private static JsonObject loadJsonFromFile(String path) {
        return parseJson(path); // Используем метод parseJson для парсинга
    }

    // Функция для сохранения JSON объекта в файл
    public static void saveJsonToFile(JsonObject jsonData, String path) {
        try (FileWriter writer = new FileWriter(path)) {
            writer.write(jsonData.toString());
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении файла: " + path);
            e.printStackTrace();
        }
    }

    // Проверка, существует ли файл по данному пути
    public static boolean fileExists(String path) {
        return java.nio.file.Files.exists(java.nio.file.Path.of(path));
    }

    // Новый метод для получения списка ресурсов
    public static JsonObject getResourcesList() {
        String path = "towny_data/resources.json";
        return loadJsonFromFile(path);  // Используем метод loadJsonFromFile для загрузки файла
    }

    // Метод для сохранения данных о городе
    public static void saveCityData(JsonObject cityData, String cityName) {
        String formattedCityName = formatCityName(cityName);
        String path = "towny_data/towns/" + formattedCityName + ".json";
        saveJsonToFile(cityData, path);
    }


}
