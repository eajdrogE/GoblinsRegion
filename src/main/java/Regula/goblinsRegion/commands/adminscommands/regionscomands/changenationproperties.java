package Regula.goblinsRegion.commands.adminscommands.regionscomands;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import Regula.goblinsRegion.commands.DBcommands.NationDataHandler;

public class changenationproperties implements CommandExecutor {

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
            player.sendMessage(ChatColor.RED + "Использование: /changenationproperties <нация> <свойство> <значение>");
            return true;
        }

        String nationName = args[0]; // Название нации
        String propertyName = args[1]; // Свойство, которое мы хотим изменить
        String propertyValue = args.length > 2 ? args[2] : ""; // Значение для свойства

        // Загружаем данные нации
        JsonObject nationData = NationDataHandler.loadNationData(nationName);
        if (nationData == null) {
            player.sendMessage(ChatColor.RED + "Нация с таким названием не найдена.");
            return true;
        }

        // Проверка на существование свойства и изменение
        if (nationData.has(propertyName)) {
            nationData.addProperty(propertyName, propertyValue); // Обновляем значение свойства
            // Сохраняем обновленные данные нации в файл
            NationDataHandler.saveNationData(nationData, nationName);
            player.sendMessage(ChatColor.GREEN + "Свойство " + propertyName + " нации " + nationName + " было обновлено на " + propertyValue);
        } else {
            player.sendMessage(ChatColor.RED + "Свойство " + propertyName + " не найдено в данных нации.");
        }

        return true;
    }
}
