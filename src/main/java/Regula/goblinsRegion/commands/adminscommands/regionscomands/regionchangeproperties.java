package Regula.goblinsRegion.commands.adminscommands.regionscomands;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.EventHandler;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class regionchangeproperties implements CommandExecutor, Listener {

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
        String townName = args[1]; // Имя города

        // Проверка существования файла города
        File townFile = new File(townsDir, townName + ".json");
        if (!townFile.exists()) {
            player.sendMessage("Город " + townName + " не найден.");
            return true;
        }

        // Уведомление игрока и запрос ввода
        player.sendMessage("Вы редактируете свойство: " + propertyName + ". Пожалуйста, введите новое значение.");

        // Сохраняем имя редактируемого свойства и города для дальнейшего использования
        // Мы будем использовать переменную для хранения этих значений, чтобы их использовать в событии чата
        // Например, можно использовать Map<Player, EditData>, чтобы для каждого игрока запоминать, что они редактируют
        // Вместо EditData можно сразу запомнить имя города и название свойства.

        // Пишем в карту, чтобы получить это значение в событии чата
        regionChangeQueue.put(player, new EditData(propertyName, townName));

        return true;
    }

    private Map<Player, EditData> regionChangeQueue = new HashMap<>(); // Хранение данных редактируемых параметров для игроков

    private static class EditData {
        String propertyName;
        String townName;

        EditData(String propertyName, String townName) {
            this.propertyName = propertyName;
            this.townName = townName;
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        // Если игрок не редактирует свойство, пропускаем
        if (!regionChangeQueue.containsKey(player)) return;

        // Получаем данные редактируемого свойства и города
        EditData editData = regionChangeQueue.get(player);
        String propertyName = editData.propertyName;
        String townName = editData.townName;

        // Получаем введенное значение
        String newValue = event.getMessage();

        // Найдем файл города
        File townFile = new File(townsDir, townName + ".json");
        if (!townFile.exists()) {
            player.sendMessage("Ошибка: Город не найден.");
            return;
        }

        // Чтение JSON файла
        JsonObject townData;
        try (FileReader reader = new FileReader(townFile)) {
            townData = JsonParser.parseReader(reader).getAsJsonObject();
        } catch (IOException e) {
            player.sendMessage("Ошибка чтения данных города.");
            e.printStackTrace();
            return;
        }

        // Проверяем, существует ли указанное свойство в JSON
        if (!townData.has(propertyName)) {
            player.sendMessage("Свойство " + propertyName + " не найдено в данных города.");
            return;
        }

        // Обновляем свойство в JSON
        townData.addProperty(propertyName, newValue);

        // Сохраняем изменения обратно в файл
        try (FileWriter writer = new FileWriter(townFile)) {
            writer.write(townData.toString());
        } catch (IOException e) {
            player.sendMessage("Ошибка сохранения данных города.");
            e.printStackTrace();
            return;
        }

        // Сообщаем игроку, что данные обновлены
        player.sendMessage("Свойство " + propertyName + " для города " + townName + " успешно изменено на: " + newValue);

        // Убираем игрока из очереди редактирования
        regionChangeQueue.remove(player);
    }
}
