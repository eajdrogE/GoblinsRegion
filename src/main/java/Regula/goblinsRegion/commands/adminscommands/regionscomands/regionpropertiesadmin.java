package Regula.goblinsRegion.commands.adminscommands.regionscomands;

import Regula.goblinsRegion.commands.DBcommands.TownsDataHandler;
import com.google.gson.JsonObject;
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

import java.util.List;

public class regionpropertiesadmin implements CommandExecutor, Listener {

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
        if (args.length == 0) {
            player.sendMessage("Укажите название города. Используйте: /regionpropertiesadmin <город>");
            return true;
        }
        String townName = args[0];
        player.sendMessage("Название города: " + townName);

        openRegionProperties(player, townName);
        return true;
    }

    private JsonObject loadTownData(String townName) {
        return TownsDataHandler.getRegionData(townName);
    }
    private void openRegionProperties(Player player, String townName) {
        JsonObject townData = loadTownData(townName);
        if (townData == null) {
            player.sendMessage("Данные о городе " + townName + " не найдены.");
            return;
        }
        Inventory inventory = Bukkit.createInventory(null, 27, "Регион: " + townName);
        addBasicPropertyItems(inventory, townData);
        addSpecialPropertyItems(inventory, townData, townName);
        player.openInventory(inventory);
    }
    private void addBasicPropertyItems(Inventory inventory, JsonObject townData) {
        addItemToInventory(inventory, Material.PLAYER_HEAD, ChatColor.DARK_BLUE + "Информация_о_регионе",
                "Название: " + townData.get("Название").getAsString());
        addItemToInventory(inventory, Material.ANVIL,  "Стабильность",
                "Текущая: " + townData.get("Стабильность").getAsInt());
        addItemToInventory(inventory, Material.GOLD_INGOT,  "Процветание",
                "Текущее: " + townData.get("Процветание").getAsInt());
        addItemToInventory(inventory, Material.IRON_SWORD,  "Военный_ресурс",
                "Текущий: " + townData.get("Военный_ресурс").getAsInt());
        addItemToInventory(inventory, Material.BREAD,  "Очки_пополнения",
                "Текущие: " + townData.get("Очки_пополнения").getAsInt());
        addItemToInventory(inventory, Material.PAPER,  "Культура",
                "Текущая: " + townData.get("Культура").getAsString());
        addItemToInventory(inventory, Material.WATER_BUCKET,  "Доступ_к_морю",
                "Есть ли доступ: " + townData.get("Доступ_к_морю").getAsBoolean());
        addItemToInventory(inventory, Material.DIAMOND, "Базовая_стабильность",
                "Базовая: " + townData.get("Базовая_стабильность").getAsInt());
        addItemToInventory(inventory, Material.REDSTONE,  "Рост_стабильности_к_базовой",
                "Рост: " + townData.get("Рост_стабильности_к_базовой").getAsInt());
        addItemToInventory(inventory, Material.REDSTONE_TORCH,  "Рост_стабильности_за_пределами_базовой",
                "Рост: " + townData.get("Рост_стабильности_за_пределами_базовой").getAsInt());
        addItemToInventory(inventory, Material.EMERALD,  "Максимальная_стабильность",
                "Максимальная: " + townData.get("Максимальная_стабильность").getAsInt());
        addItemToInventory(inventory, Material.GOLD_BLOCK,  "Рост_процветания",
                "Рост: " + townData.get("Рост_процветания").getAsInt());
        addItemToInventory(inventory, Material.IRON_BLOCK,  "Рост_лимита",
                "Рост: " + townData.get("Рост_лимита").getAsInt());
    }
    private void addSpecialPropertyItems(Inventory inventory, JsonObject townData, String townName) {
        addItemToInventory(inventory, Material.CHEST, "Ресурсы", "Выберите для управления");
        Material menuMaterial = Material.valueOf(townData.has("Материал_иконки") ? townData.get("Материал_иконки").getAsString().toUpperCase() : "PAPER");
        addItemToInventory(inventory, menuMaterial, "Материал_иконки", "Выберите для управления");
        addItemToInventory(inventory, Material.BRICKS, "Постройки", "Выберите для управления");
    }

    private String formatTownInfo(JsonObject townData) {
        StringBuilder info = new StringBuilder();

        info.append(ChatColor.YELLOW).append("Название: ").append(ChatColor.WHITE)
                .append(townData.get("Название").getAsString()).append("\n");
        info.append(ChatColor.YELLOW).append("Стабильность: ").append(ChatColor.WHITE)
                .append(townData.get("Стабильность").getAsInt()).append("\n");
        info.append(ChatColor.YELLOW).append("Процветание: ").append(ChatColor.WHITE)
                .append(townData.get("Процветание").getAsInt()).append("\n");
        info.append(ChatColor.YELLOW).append("Военный_ресурс: ").append(ChatColor.WHITE)
                .append(townData.get("Военный_ресурс").getAsInt()).append("\n");
        info.append(ChatColor.YELLOW).append("Очки_пополнения: ").append(ChatColor.WHITE)
                .append(townData.get("Очки_пополнения").getAsInt()).append("\n");
        info.append(ChatColor.YELLOW).append("Культура: ").append(ChatColor.WHITE)
                .append(townData.get("Культура").getAsString()).append("\n");
        info.append(ChatColor.YELLOW).append("Доступ_к_морю: ").append(ChatColor.WHITE)
                .append(townData.get("Доступ_к_морю").getAsBoolean() ? "Да" : "Нет").append("\n");
        info.append(ChatColor.YELLOW).append("Базовая стабильность: ").append(ChatColor.WHITE)
                .append(townData.get("Базовая_стабильность").getAsInt()).append("\n");
        info.append(ChatColor.YELLOW).append("Рост_стабильности_к_базовой: ").append(ChatColor.WHITE)
                .append(townData.get("Рост_стабильности_к_базовой").getAsInt()).append("\n");
        info.append(ChatColor.YELLOW).append("Рост стабильности за пределами базовой: ").append(ChatColor.WHITE)
                .append(townData.get("Рост_стабильности-за_пределами_базовой").getAsInt()).append("\n");
        info.append(ChatColor.YELLOW).append("Максимальная_стабильность: ").append(ChatColor.WHITE)
                .append(townData.get("Максимальная_стабильность").getAsInt()).append("\n");
        info.append(ChatColor.YELLOW).append("Рост_процветания: ").append(ChatColor.WHITE)
                .append(townData.get("Рост_процветания").getAsInt()).append("\n");
        info.append(ChatColor.YELLOW).append("Рост_лимита: ").append(ChatColor.WHITE)
                .append(townData.get("Рост_лимита").getAsInt()).append("\n");
        info.append(ChatColor.YELLOW).append("Материал_иконки: ").append(ChatColor.WHITE)
                .append(townData.get("Материал_иконки").getAsString().toUpperCase());

        return info.toString();
    }
//    private void addPropertyToInventory(Inventory inventory, int slot, Material material, String displayName, String lore) {
//        ItemStack item = new ItemStack(material);
//        ItemMeta meta = item.getItemMeta();
//        if (meta != null) {
//            meta.setDisplayName(displayName);
//            meta.setLore(List.of(lore.split("\n")));
//            item.setItemMeta(meta);
//        }
//        inventory.setItem(slot, item);
//    }
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
        String title = event.getView().getTitle();
        if (!title.startsWith("Регион: ")) return;
        event.setCancelled(true);
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;
        Player player = (Player) event.getWhoClicked();
        String townName = title.substring("Регион: ".length());
        handlePropertySelection(player, clickedItem, TownsDataHandler.formatCityName(townName));
    }

    private void handlePropertySelection(Player player, ItemStack clickedItem, String townName) {
        String itemName = clickedItem.getItemMeta().getDisplayName();
        String command;
        if (itemName.equals("Ресурсы")) {
            command = "/regionchangeresources " + townName + "";
        } else if (itemName.equals("Постройки")) {
            command = "/regionchangebuildings " + townName + "";
        } else {
            String propertyName = itemName.split(": ")[0];
            command = "/regionchangeproperties " + propertyName  +" "+ townName;
        }
        player.closeInventory();
        player.chat(command);
    }
}
S
