package Regula.goblinsRegion.commands.adminscommands.regionscomands;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import com.google.gson.JsonObject;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import Regula.goblinsRegion.commands.DBcommands.NationDataHandler;

public class nationchangeproperties implements CommandExecutor {

    public nationchangeproperties() {
        // Конструктор
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Эту команду могут использовать только игроки.");
            return true;
        }

        Player player = (Player) sender;

        // Проверка наличия прав
        if (!player.hasPermission("RegionModer")) {
            player.sendMessage("У вас нет прав для выполнения этой команды.");
            return true;
        }

        // Проверка правильности ввода аргументов
        if (args.length < 2) {
            player.sendMessage("Использование: /nationchangeproperties  <nationName> <property> <newValue>");
            return true;
        }
        String nationName = args[0];
        String propertyName = args[1]; // Имя свойства
        // Название нации
        String newValue = String.join(" ", java.util.Arrays.copyOfRange(args, 2, args.length)); // Новое значение

        // Загружаем данные нации
        JsonObject nationData = NationDataHandler.loadNationData(nationName);
        if (nationData == null) {
            player.sendMessage("Нация " + nationName + " не найдена.");
            return true;
        }

        // Если значение передано, сразу обновляем JSON
        if (args.length >= 3) {
            return updateNationProperty(player, propertyName, nationName, newValue, nationData);
        }

        // Если значения нет, выводим запрос на ввод через чат
        player.sendMessage("Вы хотите изменить свойство " + propertyName + " нации " + nationName + "?");

        // Формируем команду для вставки в текстовое поле
        String commandMessage = "/nationchangeproperties " + propertyName + " \"" + nationName + "\" \"" + newValue + "\"";

        // Формируем JSON для tellraw с кнопкой "ДА"
        TextComponent message = new TextComponent("Хотите изменить значение свойства " + propertyName + " нации " + nationName + "?\n");

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

    // Метод для обновления свойства нации в JSON
    private boolean updateNationProperty(Player player, String propertyName, String nationName, String newValue, JsonObject nationData) {
        // Проверяем, существует ли указанное свойство в JSON
        if (!nationData.has(propertyName)) {
            player.sendMessage("Свойство " + propertyName + " не найдено в данных нации.");
            return true;
        }
        // Обновляем свойство в JSON
        nationData.addProperty(propertyName, newValue);
        // Сохраняем изменения обратно в файл с помощью NationDataHandler
        NationDataHandler.saveNationData(nationData, nationName);
        // Сообщаем игроку, что данные обновлены
        player.sendMessage("Свойство " + propertyName + " для нации " + nationName + " успешно изменено на: " + newValue);
        return true;
    }
}
