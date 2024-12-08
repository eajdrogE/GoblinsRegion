package Regula.goblinsRegion.commands.adminscommands.regionscomands;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import com.google.gson.JsonObject;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import Regula.goblinsRegion.commands.DBcommands.TownsDataHandler;

import java.io.File;

public class regionchangeproperties implements CommandExecutor {

    public regionchangeproperties() {
        // Конструктор
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Эту команду могут использовать только игроки.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 2) {
            player.sendMessage("Использование: /regionchangeproperties <property> <townName>");
            return true;
        }

        String propertyName = args[0]; // Имя свойства
        String townName = args[1];

        // Убираем кавычки, если они есть в начале и конце строки
        if (townName.startsWith("\"") && townName.endsWith("\"")) {
            townName = townName.substring(1, townName.length() - 1);  // Удаляем кавычки
        }

        // Проверка существования данных города через TownsDataHandler
        JsonObject townData = TownsDataHandler.getRegionData(townName);
        if (townData == null) {
            player.sendMessage("Город " + townName + " не найден.");
            return true;
        }

        // Если значение передано, сразу обновляем JSON
        if (args.length >= 3) {
            String newValue = args[2]; // Значение, которое нужно установить
            return updateTownProperty(player, propertyName, townName, newValue, townData);
        }

        // Если значения нет, выводим запрос на ввод через чат
        player.sendMessage("Вы хотите изменить свойство " + propertyName + " города " + townName + "?");

        // Формируем команду для вставки в текстовое поле
        String commandMessage = "/regionchangeproperties " + propertyName + " \"" + townName + "\" ";

        // Формируем JSON для tellraw с кнопкой "ДА"
        TextComponent message = new TextComponent("Хотите изменить значение свойства " + propertyName + " города " + townName + "?\n");

        // Создаем кнопку "ДА"
        TextComponent yesButton = new TextComponent("ДА");
        yesButton.setColor(net.md_5.bungee.api.ChatColor.GREEN);
        yesButton.setUnderlined(true);

        // Добавляем в кнопку команду для вставки текста в чат (вызывается при клике)
        yesButton.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, commandMessage));

        // Добавляем кнопку в сообщение
        message.addExtra(yesButton);

        // Отправляем сообщение через Spigot API с кнопкой "ДА"
        player.spigot().sendMessage(message);
        return true;
    }

    // Метод для обновления свойства города в JSON
    private boolean updateTownProperty(Player player, String propertyName, String townName, String newValue, JsonObject townData) {
        // Проверяем, существует ли указанное свойство в JSON
        if (!townData.has(propertyName)) {
            player.sendMessage("Свойство " + propertyName + " не найдено в данных города.");
            return true;
        }

        // Если обновляется имя города, переименовываем файл
        if (propertyName.equalsIgnoreCase("name")) {
            String oldTownName = townName;
            String newTownName = newValue;

            boolean renamed = renameTownFileProp(oldTownName, newTownName, townData);
            renameTownFileRes(oldTownName, newTownName, townData);
            renameTownFileBuild(oldTownName, newTownName, townData);
            if (!renamed) {
                player.sendMessage("Ошибка: не удалось переименовать файл города.");
                return true;
            }

            player.sendMessage("Файл города успешно переименован с " + oldTownName + " на " + newTownName);
            return true;
        }

        // Обновляем свойство в JSON
        townData.addProperty(propertyName, newValue);

        // Сохраняем изменения обратно в файл с помощью TownsDataHandler
        TownsDataHandler.saveJsonToFile(townData, "towny_data/towns/" + TownsDataHandler.formatCityName(townName) + ".json");

        // Сообщаем игроку, что данные обновлены
        player.sendMessage("Свойство " + propertyName + " для города " + townName + " успешно изменено на: " + newValue);

        return true;
    }

    // Метод для переименования файла города
    private boolean renameTownFileProp(String oldTownName, String newTownName, JsonObject townData) {
        // Форматируем имена файлов
        String oldFileName = "towny_data/towns/" + TownsDataHandler.formatCityName(oldTownName) + ".json";
        String newFileName = "towny_data/towns/" + TownsDataHandler.formatCityName(newTownName) + ".json";

        File oldFile = new File(oldFileName);
        File newFile = new File(newFileName);

        // Проверяем существование старого файла
        if (!oldFile.exists()) {
            return false; // Старый файл не существует
        }

        // Если файл с новым именем уже существует, возвращаем ошибку
        if (newFile.exists()) {
            return false; // Новый файл уже существует
        }

        // Переименовываем файл
        boolean success = oldFile.renameTo(newFile);
        if (success) {
            // Обновляем JSON-данные с новым именем
            townData.addProperty("name", newTownName);
            TownsDataHandler.saveJsonToFile(townData, newFileName);
        }

        return success;
    }

    private boolean renameTownFileRes(String oldTownName, String newTownName, JsonObject townData) {
        // Форматируем имена файлов
        String oldFileName = "towny_data/towns_resources/" + TownsDataHandler.formatCityName(oldTownName) + ".json";
        String newFileName = "towny_data/towns_resources/" + TownsDataHandler.formatCityName(newTownName) + ".json";

        File oldFile = new File(oldFileName);
        File newFile = new File(newFileName);

        // Проверяем существование старого файла
        if (!oldFile.exists()) {
            return false; // Старый файл не существует
        }

        // Если файл с новым именем уже существует, возвращаем ошибку
        if (newFile.exists()) {
            return false; // Новый файл уже существует
        }

        // Переименовываем файл
        boolean success = oldFile.renameTo(newFile);
//        if (success) {
//            // Обновляем JSON-данные с новым именем
//            townData.addProperty("name", newTownName);
//            TownsDataHandler.saveJsonToFile(townData, newFileName);
//        }

        return success;
    }
    private boolean renameTownFileBuild(String oldTownName, String newTownName, JsonObject townData) {
        // Форматируем имена файлов
        String oldFileName = "towny_data/towns_buildings/" + TownsDataHandler.formatCityName(oldTownName) + ".json";
        String newFileName = "towny_data/towns_buildings/" + TownsDataHandler.formatCityName(newTownName) + ".json";

        File oldFile = new File(oldFileName);
        File newFile = new File(newFileName);

        // Проверяем существование старого файла
        if (!oldFile.exists()) {
            return false; // Старый файл не существует
        }

        // Если файл с новым именем уже существует, возвращаем ошибку
        if (newFile.exists()) {
            return false; // Новый файл уже существует
        }

        // Переименовываем файл
        boolean success = oldFile.renameTo(newFile);
//        if (success) {
//            // Обновляем JSON-данные с новым именем
//            townData.addProperty("name", newTownName);
//            TownsDataHandler.saveJsonToFile(townData, newFileName);
//        }

        return success;
    }
}
