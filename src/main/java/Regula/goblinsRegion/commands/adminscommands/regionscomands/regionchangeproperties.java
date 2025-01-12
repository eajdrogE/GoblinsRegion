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
        if (!player.hasPermission("RegionModer")) {
            player.sendMessage("У вас нет прав для выполнения этой команды.");
            return true;
        }
        if (args.length < 2) {
            player.sendMessage("Использование: /regionchangeproperties <property> <townName> <newValue>");
            return true;
        }

        String propertyName = args[0]; // Имя свойства
        String townName = args[1]; // Старое имя города
        String newTownName = String.join(" ", java.util.Arrays.copyOfRange(args, 2, args.length)); // Новое имя города

        // Убираем кавычки, если они есть в начале и конце строки
        if (townName.startsWith("\"") && townName.endsWith("\"")) {
            townName = townName.substring(1, townName.length() - 1);  // Удаляем кавычки
        }
        if (newTownName.startsWith("\"") && newTownName.endsWith("\"")) {
            newTownName = newTownName.substring(1, newTownName.length() - 1);  // Удаляем кавычки
        }

        // Форматируем имена городов
        String formattedTownName = TownsDataHandler.formatCityName(townName);
        String formattedNewTownName = TownsDataHandler.formatCityName(newTownName);

        // Проверка существования данных города через TownsDataHandler
        JsonObject townData = TownsDataHandler.getRegionData(formattedTownName);
        if (townData == null) {
            player.sendMessage("Город " + townName + " не найден.");
            return true;
        }

        // Если значение передано, сразу обновляем JSON
        if (args.length >= 3) {
            return updateTownProperty(player, propertyName, formattedTownName, formattedNewTownName, townData);
        }

        // Если значения нет, выводим запрос на ввод через чат
        player.sendMessage("Вы хотите изменить свойство " + propertyName + " города " + townName + "?");

        // Формируем команду для вставки в текстовое поле
        String commandMessage = "/regionchangeproperties " + propertyName + " " + formattedTownName + " ";

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
        // Обновляем свойство в JSON
        townData.addProperty(propertyName, newValue);
        // Сохраняем изменения обратно в файл с помощью TownsDataHandler
        TownsDataHandler.saveCityData(townData, townName);
        // Сообщаем игроку, что данные обновлены
        if (propertyName.equalsIgnoreCase("name")) {
            boolean renamedProp = renameTownFileProp(townName, newValue, townData);
          //  boolean renamedRes = renameTownFileRes(townName, newValue, townData);
          //  boolean renamedBuild = renameTownFileBuild(townName, newValue, townData);

            if (renamedProp /*&& renamedRes && renamedBuild*/) {
                player.sendMessage("Название города успешно изменено на: " + newValue);
            }
        }
        player.sendMessage("Свойство " + propertyName + " для города " + townName + " успешно изменено на: " + newValue);
        return true;
    }
    // Метод для переименования файла города
    private boolean renameTownFileProp(String oldTownName, String newTownName, JsonObject townData) {
        String oldFileName = "towny_data/towns/" + oldTownName + ".json";
        String newFileName = "towny_data/towns/" + newTownName + ".json";
        return renameFile(oldFileName, newFileName, townData);
    }

   /* private boolean renameTownFileRes(String oldTownName, String newTownName, JsonObject townData) {
        String oldFileName = "towny_data/towns_resources/" + oldTownName + ".json";
        String newFileName = "towny_data/towns_resources/" + newTownName + ".json";
        return renameFile(oldFileName, newFileName, townData);
    }

    private boolean renameTownFileBuild(String oldTownName, String newTownName, JsonObject townData) {
        String oldFileName = "towny_data/towns_buildings/" + oldTownName + ".json";
        String newFileName = "towny_data/towns_buildings/" + newTownName + ".json";
        return renameFile(oldFileName, newFileName, townData);
    }
*/
    private boolean renameFile(String oldFileName, String newFileName, JsonObject townData) {
        File oldFile = new File(oldFileName);
        File newFile = new File(newFileName);

        if (!oldFile.exists()) {
            return false; // Старый файл не существует
        }

        if (newFile.exists()) {
            return false; // Новый файл уже существует
        }

        // Переименовываем файл
        boolean success = oldFile.renameTo(newFile);
        if (success) {
            townData.addProperty("name", newFileName); // Обновляем имя в JSON
            return true;
        } else {
            // Если переименование не удалось, удаляем старый файл
            boolean deleted = oldFile.delete();
            if (deleted) {
                System.out.println("Старый файл был удалён: " + oldFileName);
            } else {
                System.err.println("Не удалось удалить старый файл: " + oldFileName);
            }
        }
        return success;
    }
}
