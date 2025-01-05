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

        // Добавляем данные в инвентарь
        addBasicPropertyItems(inventory, townData);
        addSpecialPropertyItems(inventory, townData, townName);

        player.openInventory(inventory);
    }

    private void addBasicPropertyItems(Inventory inventory, JsonObject townData) {
        addPropertyToInventory(inventory, 0, Material.PAPER, "Информация о регионе", formatTownInfo(townData));
        addPropertyToInventory(inventory, 1, Material.PAPER, "Название", townData.get("Название").getAsString());
        addPropertyToInventory(inventory, 2, Material.ANVIL, "Стабильность", String.valueOf(townData.get("Стабильность").getAsInt()));
        addPropertyToInventory(inventory, 3, Material.GOLD_INGOT, "Процветание", String.valueOf(townData.get("Процветание").getAsInt()));
        addPropertyToInventory(inventory, 4, Material.IRON_SWORD, "Лимит", String.valueOf(townData.get("Лимит").getAsInt()));
        addPropertyToInventory(inventory, 5, Material.BREAD, "Очки_пополнения", String.valueOf(townData.get("Очки_пополнения").getAsInt()));
        addPropertyToInventory(inventory, 6, Material.PAPER, "Культура", townData.get("Культура").getAsString());
        addPropertyToInventory(inventory, 7, Material.WATER_BUCKET, "Доступ_к_морю", String.valueOf(townData.get("Доступ_к_морю").getAsBoolean()));
        addPropertyToInventory(inventory, 8, Material.DIAMOND, "Базовая_стабильность", String.valueOf(townData.get("Базовая_стабильность").getAsInt()));
        addPropertyToInventory(inventory, 9, Material.REDSTONE, "Рост_стабильности_к_базовой", String.valueOf(townData.get("Рост_стабильности_к_базовой").getAsInt()));
        addPropertyToInventory(inventory, 10, Material.REDSTONE_TORCH, "Рост_стабильности-за_пределами_базовой", String.valueOf(townData.get("Рост_стабильности-за_пределами_базовой").getAsInt()));
        addPropertyToInventory(inventory, 11, Material.EMERALD, "Максимальная_стабильность", String.valueOf(townData.get("Максимальная_стабильность").getAsInt()));
        addPropertyToInventory(inventory, 12, Material.GOLD_BLOCK, "Рост_процветания", String.valueOf(townData.get("Рост_процветания").getAsInt()));
        addPropertyToInventory(inventory, 13, Material.IRON_BLOCK, "Рост_лимита", String.valueOf(townData.get("Рост_лимита").getAsInt()));
    }

    private void addSpecialPropertyItems(Inventory inventory, JsonObject townData, String townName) {
        addPropertyToInventory(inventory, 14, Material.CHEST, "Ресурсы", "Выберите для управления");
        Material menuMaterial = Material.valueOf(townData.get("Материал_иконки").getAsString().toUpperCase());
        addPropertyToInventory(inventory, 15, menuMaterial, "Материал_иконки", "Выберите для управления");
        addPropertyToInventory(inventory, 16, Material.BRICKS, "Постройки", "Выберите для управления");
    }

    private String formatTownInfo(JsonObject townData) {
        StringBuilder info = new StringBuilder();

        info.append(ChatColor.YELLOW).append("Название: ").append(ChatColor.WHITE)
                .append(townData.get("Название").getAsString()).append("\n");
        info.append(ChatColor.YELLOW).append("Стабильность: ").append(ChatColor.WHITE)
                .append(townData.get("Стабильность").getAsInt()).append("\n");
        info.append(ChatColor.YELLOW).append("Процветание: ").append(ChatColor.WHITE)
                .append(townData.get("Процветание").getAsInt()).append("\n");
        info.append(ChatColor.YELLOW).append("Лимит: ").append(ChatColor.WHITE)
                .append(townData.get("Лимит").getAsInt()).append("\n");
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

    private void addPropertyToInventory(Inventory inventory, int slot, Material material, String displayName, String lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(displayName);
            meta.setLore(List.of(lore.split("\n")));
            item.setItemMeta(meta);
        }
        inventory.setItem(slot, item);
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

        if (itemName.equals("resources")) {
            command = "/regionchangeresources " + townName + "";
        } else if (itemName.equals("buildings")) {
            command = "/regionchangebuildings " + townName + "";
        } else {
            String propertyName = itemName.split(": ")[0];
            command = "/regionchangeproperties " + propertyName  +" "+ townName;
        }

        player.closeInventory();
        player.chat(command);
    }
}
