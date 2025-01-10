package Regula.goblinsRegion.commands.adminscommands.spirit;

import com.google.gson.*;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Town;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Random;

public class spiritlist implements CommandExecutor {

    private final JsonArray availableResources = new JsonArray();
    private final Random random = new Random();

    public spiritlist() {
    }

    private void loadResources() {
        try (FileReader reader = new FileReader("towny_data/resources.json")) {
            JsonObject resourceData = JsonParser.parseReader(reader).getAsJsonObject();
            availableResources.addAll(resourceData.getAsJsonArray("resources"));
        } catch (IOException e) {
            Bukkit.getLogger().severe("Не удалось загрузить доступные ресурсы: " + e.getMessage());
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Команду может выполнять только игрок.");
            return true;
        }

        Player player = (Player) sender;

        // Проверка прав с LuckPerms
        LuckPerms luckPerms = LuckPermsProvider.get();
        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        if (user == null || !user.getCachedData().getPermissionData().checkPermission("RegionModer").asBoolean()) {
            player.sendMessage("У вас нет прав для выполнения этой команды.");
            return true;
        }
        player.sendMessage("Лист игровых духов");
        return true;
    }
}
