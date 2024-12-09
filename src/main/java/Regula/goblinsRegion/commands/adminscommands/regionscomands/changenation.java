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
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import Regula.goblinsRegion.commands.DBcommands.NationDataHandler;

import java.util.List;

public class changenation implements CommandExecutor, Listener {

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
        Inventory inventory = Bukkit.createInventory(null, 27, "Нация: " + NationDataHandler.formatNationName(nationName));

        // Сборник всей информации нации в первом слоте
        addItemToInventory(inventory, Material.BOOK, ChatColor.AQUA + "Общая информация",
                "Доход: " + nationData.get("income").getAsInt(),
                "Лимит ресурсов: " + nationData.get("limit").getAsInt(),
                "Лимит атак: " + nationData.get("attackLimit").getAsInt(),
                "Лимит защиты: " + nationData.get("defenseLimit").getAsInt(),
                "Процветание: " + nationData.get("prosperity").getAsInt(),
                "Эффективность: " + nationData.get("efficiency").getAsDouble(),
                "Престиж: " + nationData.get("prestige").getAsInt(),
                "Бесчестие: " + nationData.get("dishonor").getAsInt(),
                "Репутация знати: " + nationData.get("nobilityLoyalty").getAsInt(),
                "Репутация духовенства: " + nationData.get("clergyLoyalty").getAsInt(),
                "Репутация крестьян: " + nationData.get("commonersLoyalty").getAsInt(),
                "Главный бог: " + nationData.get("mainDeity").getAsString(),
                "Основная раса: " + nationData.get("mainRace").getAsString(),
                "Статус: " + nationData.get("status").getAsString()
        );

// Отдельные свойства
        addItemToInventory(inventory, Material.PLAYER_HEAD, ChatColor.DARK_BLUE + "Правитель",
                "Имя: " + nationData.get("rulerName").getAsString(),
                "Возраст: " + nationData.get("rulerAge").getAsInt(),
                "Раса: " + nationData.get("rulerRace").getAsString());
        addItemToInventory(inventory, Material.ZOMBIE_HEAD, ChatColor.BLUE + "Основная раса",
                nationData.get("mainRace").getAsString());
        addItemToInventory(inventory, Material.FEATHER, ChatColor.AQUA + "Статус",
                nationData.get("status").getAsString());
        addItemToInventory(inventory, Material.BLAZE_POWDER, ChatColor.GOLD + "Главный бог",
                nationData.get("mainDeity").getAsString());
        addItemToInventory(inventory, Material.EMERALD, ChatColor.GREEN + "Процветание",
                String.valueOf(nationData.get("prosperity").getAsInt()));
        addItemToInventory(inventory, Material.EXPERIENCE_BOTTLE, ChatColor.GREEN + "Эффективность",
                String.valueOf(nationData.get("efficiency").getAsDouble()));
        addItemToInventory(inventory, Material.BOOK, ChatColor.AQUA + "Престиж",
                String.valueOf(nationData.get("prestige").getAsInt()));
        addItemToInventory(inventory, Material.REDSTONE, ChatColor.RED + "Бесчестие",
                String.valueOf(nationData.get("dishonor").getAsInt()));
        addItemToInventory(inventory, Material.PAPER, ChatColor.LIGHT_PURPLE + "Репутация знати",
                String.valueOf(nationData.get("nobilityLoyalty").getAsInt()));
        addItemToInventory(inventory, Material.BOOKSHELF, ChatColor.DARK_PURPLE + "Репутация духовенства",
                String.valueOf(nationData.get("clergyLoyalty").getAsInt()));
        addItemToInventory(inventory, Material.HAY_BLOCK, ChatColor.YELLOW + "Репутация крестьян",
                String.valueOf(nationData.get("commonersLoyalty").getAsInt()));

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

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        if (event.getView().getTitle().startsWith("Нация:")) {
            event.setCancelled(true); // Запрет на любые клики
            player.sendMessage(ChatColor.RED + "Вы не можете взаимодействовать с этим инвентарем.");
        }
    }
}
