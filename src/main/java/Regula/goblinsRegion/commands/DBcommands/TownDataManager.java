package Regula.goblinsRegion.commands.DBcommands;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;

public class TownDataManager {

    private final File townsDir;

    public TownDataManager(File townsDir) {
        if (!townsDir.exists()) {
            townsDir.mkdirs(); // Создаем папку, если она не существует
        }
        this.townsDir = townsDir;
    }

    /**
     * Загружает данные города из файла.
     *
     * @param townName название города
     * @return JsonObject с данными города или null, если файл не найден
     */
    public JsonObject loadTownData(String townName) {
        File townFile = new File(townsDir, townName + ".json");

        if (!townFile.exists()) {
            System.out.println("Файл города не найден: " + townFile.getAbsolutePath());
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(townFile))) {
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }

            return JsonParser.parseString(stringBuilder.toString()).getAsJsonObject();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Сохраняет данные города в файл.
     *
     * @param townName название города
     * @param townData JsonObject с данными города
     */
    public void saveTownData(String townName, JsonObject townData) {
        File townFile = new File(townsDir, townName + ".json");

        try (FileWriter writer = new FileWriter(townFile)) {
            writer.write(townData.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
