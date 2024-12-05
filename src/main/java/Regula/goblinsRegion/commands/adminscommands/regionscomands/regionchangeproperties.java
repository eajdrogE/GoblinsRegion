package Regula.goblinsRegion.commands.adminscommands.regionscomands;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class regionchangeproperties implements CommandExecutor {

    private final File townsDir = new File("towny_data/towns");

    public regionchangeproperties(File townsDir) {
        if (!townsDir.exists()) {
            townsDir.mkdirs(); // Создаем папку, если она не существует
        }
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

        // Убираем кавычки, если они есть в начале и конце строки (для корректного имени города)
        if (townName.startsWith("\"") && townName.endsWith("\"")) {
            townName = townName.substring(1, townName.length() - 1);  // Удаляем кавычки
        }

        // Проверка существования файла города
        File townFile = new File(townsDir, townName + ".json");
        if (!townFile.exists()) {
            player.sendMessage("Город " + townName + " не найден.");
            return true;
        }

        // Если значение передано, сразу обновляем JSON
        if (args.length >= 3) {
            String newValue = args[2]; // Значение, которое нужно установить
            return updateTownProperty(player, propertyName, townName, newValue);
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
        yesButton.setClickEvent(new net.md_5.bungee.api.chat.ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.SUGGEST_COMMAND, commandMessage));

        // Добавляем кнопку в сообщение
        message.addExtra(yesButton);

        // Отправляем сообщение через Spigot API с кнопкой "ДА"
        player.spigot().sendMessage(message);
        return true;
    }

    // Метод для обновления свойства города в JSON
    private boolean updateTownProperty(Player player, String propertyName, String townName, String newValue) {
        // Убираем кавычки из имени города, если они есть
        if (townName.startsWith("\"") && townName.endsWith("\"")) {
            townName = townName.substring(1, townName.length() - 1);  // Удаляем кавычки
        }

        // Проверка существования файла города
        File townFile = new File(townsDir, townName + ".json");
        if (!townFile.exists()) {
            player.sendMessage("Город " + townName + " не найден.");
            return true;
        }

        // Чтение JSON файла
        JsonObject townData;
        try (FileReader reader = new FileReader(townFile)) {
            townData = JsonParser.parseReader(reader).getAsJsonObject();
        } catch (IOException e) {
            player.sendMessage("Ошибка чтения данных города.");
            e.printStackTrace();
            return true;
        }

        // Проверяем, существует ли указанное свойство в JSON
        if (!townData.has(propertyName)) {
            player.sendMessage("Свойство " + propertyName + " не найдено в данных города.");
            return true;
        }

        // Обновляем свойство в JSON
        townData.addProperty(propertyName, newValue);

        // Сохраняем изменения обратно в файл
        try (FileWriter writer = new FileWriter(townFile)) {
            writer.write(townData.toString());
        } catch (IOException e) {
            player.sendMessage("Ошибка сохранения данных города.");
            e.printStackTrace();
            return true;
        }

        // Сообщаем игроку, что данные обновлены
        player.sendMessage("Свойство " + propertyName + " для города " + townName + " успешно изменено на: " + newValue);

        return true;
    }
}
