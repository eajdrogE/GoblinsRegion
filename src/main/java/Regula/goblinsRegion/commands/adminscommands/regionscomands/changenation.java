package Regula.goblinsRegion.commands.adminscommands.regionscomands;

import com.google.gson.JsonObject;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import Regula.goblinsRegion.commands.DBcommands.NationDataHandler;

import java.util.List;

public class changenation implements CommandExecutor {
    public changenation() {
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Команду может выполнять только игрок.");
            return true;
        }

        Player player = (Player) sender;

        // Проверка прав через LuckPerms
        LuckPerms luckPerms = LuckPermsProvider.get();
        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        if (user == null || !user.getCachedData().getPermissionData().checkPermission("RegionModer").asBoolean()) {
            player.sendMessage(ChatColor.RED + "У вас нет прав для выполнения этой команды.");
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Использование: /changenation <название нации>");
            return true;
        }

        String nationName = args[0];

        // Загрузка данных нации из файла
        JsonObject nationData = NationDataHandler.loadNationData(nationName);
        if (nationData == null) {
            player.sendMessage(ChatColor.RED + "Нация с именем \"" + nationName + "\" не найдена.");
            return true;
        }

        // Создание GUI-меню
        Inventory inventory = Bukkit.createInventory(null, 27, ChatColor.GOLD + "Статистика нации: " + nationName);

        // Добавление всех данных нации в меню
        addItemToInventory(inventory, Material.GOLD_INGOT, ChatColor.YELLOW + "Доход", "Текущий доход: " + nationData.get("income").getAsInt());
        addItemToInventory(inventory, Material.CHEST, ChatColor.YELLOW + "Лимит ресурсов", "Лимит: " + nationData.get("limit").getAsInt());
        addItemToInventory(inventory, Material.IRON_SWORD, ChatColor.RED + "Лимит атак", "Максимум атак: " + nationData.get("attackLimit").getAsInt());
        addItemToInventory(inventory, Material.SHIELD, ChatColor.BLUE + "Лимит защиты", "Максимум защиты: " + nationData.get("defenseLimit").getAsInt());
        addItemToInventory(inventory, Material.EMERALD, ChatColor.GREEN + "Процветание", "Уровень процветания: " + nationData.get("prosperity").getAsInt());
        addItemToInventory(inventory, Material.EXPERIENCE_BOTTLE, ChatColor.GREEN + "Эффективность", "Эффективность: " + nationData.get("efficiency").getAsDouble());
        addItemToInventory(inventory, Material.BOOK, ChatColor.AQUA + "Престиж", "Текущий престиж: " + nationData.get("prestige").getAsInt());
        addItemToInventory(inventory, Material.REDSTONE, ChatColor.RED + "Дезонор", "Уровень дезонора: " + nationData.get("dishonor").getAsInt());
        addItemToInventory(inventory, Material.PAPER, ChatColor.LIGHT_PURPLE + "Репутация знати", "Уровень лояльности: " + nationData.get("nobilityLoyalty").getAsInt());
        addItemToInventory(inventory, Material.BOOKSHELF, ChatColor.DARK_PURPLE + "Репутация духовенства", "Уровень лояльности: " + nationData.get("clergyLoyalty").getAsInt());
        addItemToInventory(inventory, Material.HAY_BLOCK, ChatColor.YELLOW + "Репутация крестьян", "Уровень лояльности: " + nationData.get("commonersLoyalty").getAsInt());
        addItemToInventory(inventory, Material.BLAZE_POWDER, ChatColor.GOLD + "Главный бог", "Имя бога: " + nationData.get("mainDeity").getAsString());
        addItemToInventory(inventory, Material.PLAYER_HEAD, ChatColor.DARK_BLUE + "Правитель", "Имя: " + nationData.get("rulerName").getAsString(), "Возраст: " + nationData.get("rulerAge").getAsInt(), "Раса: " + nationData.get("rulerRace").getAsString());
        addItemToInventory(inventory, Material.ZOMBIE_HEAD, ChatColor.BLUE + "Основная раса", "Раса: " + nationData.get("mainRace").getAsString());
        addItemToInventory(inventory, Material.FEATHER, ChatColor.AQUA + "Статус", "Статус нации: " + nationData.get("status").getAsString());

        // Открытие меню для игрока
        player.openInventory(inventory);
        return true;
    }

    private void addItemToInventory(Inventory inventory, Material material, String name, String... loreLines) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(List.of(loreLines));
            item.setItemMeta(meta);
        }
        inventory.addItem(item);
    }
}
