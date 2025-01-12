package Regula.goblinsRegion.commands.DBcommands;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SpiritDataHandler {

    private static final String SPIRITS_DIRECTORY = "towny_data/spirits/";
    private static final Logger LOGGER = Logger.getLogger(SpiritDataHandler.class.getName());

    /**
     * Загружает данные духа из файла.
     *
     * @param spiritName имя духа.
     * @return JsonObject с данными духа или null, если данные не найдены.
     */
    public static JsonObject getSpiritData(String spiritName) {
        File file = new File(SPIRITS_DIRECTORY + spiritName + ".json");

        if (!file.exists()) {
            LOGGER.log(Level.WARNING, "Файл для духа {0} не найден.", spiritName);
            return null;
        }

        try (FileReader reader = new FileReader(file)) {
            return JsonParser.parseReader(reader).getAsJsonObject();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Ошибка при загрузке данных духа {0}: {1}", new Object[]{spiritName, e.getMessage()});
            return null;
        }
    }

    /**
     * Сохраняет данные духа в файл.
     *
     * @param spiritName имя духа.
     * @param spiritData JsonObject с данными духа.
     * @return true, если данные успешно сохранены, иначе false.
     */
    public static boolean saveSpiritData(String spiritName, JsonObject spiritData) {
        File file = new File(SPIRITS_DIRECTORY + spiritName + ".json");

        // Создаем директорию, если она не существует
        if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
            LOGGER.log(Level.SEVERE, "Не удалось создать директорию для духов.");
            return false;
        }

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(spiritData.toString());
            return true;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Ошибка при сохранении данных духа {0}: {1}", new Object[]{spiritName, e.getMessage()});
            return false;
        }
    }

    /**
     * Форматирует имя духа, приводя его к единому виду (например, заменяет пробелы на нижние подчеркивания).
     *
     * @param spiritName имя духа.
     * @return форматированное имя духа.
     */
    public static String formatSpiritName(String spiritName) {
        return spiritName.trim().replace(" ", "_").toLowerCase();
    }

    /**
     * Обновляет отдельное свойство в данных духа.
     *
     * @param spiritName имя духа.
     * @param propertyName имя свойства.
     * @param value новое значение свойства.
     * @return true, если обновление прошло успешно, иначе false.
     */
    public static boolean updateSpiritProperty(String spiritName, String propertyName, JsonPrimitive value) {
        JsonObject spiritData = getSpiritData(spiritName);
        if (spiritData == null) {
            LOGGER.log(Level.WARNING, "Данные духа {0} отсутствуют для обновления.", spiritName);
            return false;
        }

        spiritData.add(propertyName, value);
        return saveSpiritData(spiritName, spiritData);
    }
}
