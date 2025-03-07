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

public class nationchange implements CommandExecutor, Listener {

    public nationchange() {
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
            player.sendMessage(ChatColor.RED + "Использование: /nationchange <название нации>");
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
        addItemToInventory(inventory, Material.BOOK, ChatColor.AQUA + "Общая_информация",
                "Доход: " + nationData.get("Доход").getAsInt(),
                "Военный_ресурс: " + nationData.get("Военный_ресурс").getAsInt(),
                "Лимит_наступления: " + nationData.get("Лимит_наступления").getAsInt(),
                "Лимит_защиты: " + nationData.get("Лимит_защиты").getAsInt(),
                "Процветание: " + nationData.get("Процветание").getAsInt(),
                "Эффективность: " + nationData.get("Эффективность").getAsDouble(),
                "Престиж: " + nationData.get("Престиж").getAsInt(),
                "Бесчестие: " + nationData.get("Бесчестие").getAsInt(),
                "Репутация_знати: " + nationData.get("Репутация_знати").getAsInt(),
                "Репутация_духовенства: " + nationData.get("Репутация_духовенства").getAsInt(),
                "Репутация_крестьян: " + nationData.get("Репутация_крестьян").getAsInt(),
                "Главный_бог: " + nationData.get("Главный_бог").getAsString(),
                "Основная_раса: " + nationData.get("Основная_раса").getAsString(),
                "Статус: " + nationData.get("Статус").getAsString()
        );

        // Отдельные свойства
        addItemToInventory(inventory, Material.PLAYER_HEAD, ChatColor.DARK_BLUE + "Правитель",
                "Имя: " + nationData.get("Имя").getAsString(),
                "Возраст: " + nationData.get("Возраст").getAsInt(),
                "Раса: " + nationData.get("Раса").getAsString());
        addItemToInventory(inventory, Material.ZOMBIE_HEAD, ChatColor.BLUE + "Основная_раса",
                nationData.get("Основная_раса").getAsString());
        addItemToInventory(inventory, Material.FEATHER, ChatColor.AQUA + "Статус",
                nationData.get("Статус").getAsString());
        addItemToInventory(inventory, Material.BLAZE_POWDER, ChatColor.GOLD + "Главный_бог",
                nationData.get("Главный_бог").getAsString());
        addItemToInventory(inventory, Material.EMERALD, ChatColor.GREEN + "Процветание",
                String.valueOf(nationData.get("Процветание").getAsInt()));
        addItemToInventory(inventory, Material.EXPERIENCE_BOTTLE, ChatColor.GREEN + "Эффективность",
                String.valueOf(nationData.get("Эффективность").getAsDouble()));
        addItemToInventory(inventory, Material.BOOK, ChatColor.AQUA + "Престиж",
                String.valueOf(nationData.get("Престиж").getAsInt()));
        addItemToInventory(inventory, Material.REDSTONE, ChatColor.RED + "Бесчестие",
                String.valueOf(nationData.get("Бесчестие").getAsInt()));
        addItemToInventory(inventory, Material.PAPER, ChatColor.LIGHT_PURPLE + "Репутация_знати",
                String.valueOf(nationData.get("Репутация_знати").getAsInt()));
        addItemToInventory(inventory, Material.BOOKSHELF, ChatColor.DARK_PURPLE + "Репутация_духовенства",
                String.valueOf(nationData.get("Репутация_духовенства").getAsInt()));
        addItemToInventory(inventory, Material.HAY_BLOCK, ChatColor.YELLOW + "Репутация_крестьян",
                String.valueOf(nationData.get("Репутация_крестьян").getAsInt()));

        // Ячейка для духов
        addItemToInventory(inventory, Material.GHAST_TEAR, ChatColor.BLUE + "Духи", "Посмотреть духов нации");

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

            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

            String itemName = clickedItem.getItemMeta().getDisplayName();
            String nationName = event.getView().getTitle().substring("Нация: ".length());

            if (itemName.equals(ChatColor.DARK_BLUE + "Правитель")) {
                player.sendMessage(ChatColor.YELLOW + "Это свойство предназначено только для просмотра.");
                return;
            }

            if (itemName.equals(ChatColor.BLUE + "Духи")) {
                player.closeInventory();
                player.chat("/viewspirits " + nationName);
                return;
            }

            String propertyName = ChatColor.stripColor(itemName);
            String command = "/nationchangeproperties " + propertyName + " " + nationName;

            player.closeInventory();
            player.chat(command);
        }
    }
}
