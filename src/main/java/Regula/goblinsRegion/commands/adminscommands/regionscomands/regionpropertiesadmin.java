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
        addPropertyToInventory(inventory, 1, Material.PAPER, "name", townData.get("name").getAsString());
        addPropertyToInventory(inventory, 2, Material.ANVIL, "stability", String.valueOf(townData.get("stability").getAsInt()));
        addPropertyToInventory(inventory, 3, Material.GOLD_INGOT, "prosperity", String.valueOf(townData.get("prosperity").getAsInt()));
        addPropertyToInventory(inventory, 4, Material.IRON_SWORD, "limit", String.valueOf(townData.get("limit").getAsInt()));
        addPropertyToInventory(inventory, 5, Material.BREAD, "replenishmentPoints", String.valueOf(townData.get("replenishmentPoints").getAsInt()));
        addPropertyToInventory(inventory, 6, Material.PAPER, "culture", townData.get("culture").getAsString());
        addPropertyToInventory(inventory, 7, Material.WATER_BUCKET, "hasSeaAccess", String.valueOf(townData.get("hasSeaAccess").getAsBoolean()));
        addPropertyToInventory(inventory, 8, Material.DIAMOND, "baseStability", String.valueOf(townData.get("baseStability").getAsInt()));
        addPropertyToInventory(inventory, 9, Material.REDSTONE, "stabilityGrowthToBase", String.valueOf(townData.get("stabilityGrowthToBase").getAsInt()));
        addPropertyToInventory(inventory, 10, Material.REDSTONE_TORCH, "stabilityGrowthBeyondBase", String.valueOf(townData.get("stabilityGrowthBeyondBase").getAsInt()));
        addPropertyToInventory(inventory, 11, Material.EMERALD, "maxStability", String.valueOf(townData.get("maxStability").getAsInt()));
        addPropertyToInventory(inventory, 12, Material.GOLD_BLOCK, "prosperityGrowth", String.valueOf(townData.get("prosperityGrowth").getAsInt()));
        addPropertyToInventory(inventory, 13, Material.IRON_BLOCK, "limitGrowth", String.valueOf(townData.get("limitGrowth").getAsInt()));
    }

    private void addSpecialPropertyItems(Inventory inventory, JsonObject townData, String townName) {
        addPropertyToInventory(inventory, 14, Material.CHEST, "resources", "Выберите для управления");
        Material menuMaterial = Material.valueOf(townData.get("menuMaterial").getAsString().toUpperCase());
        addPropertyToInventory(inventory, 15, menuMaterial, "menuMaterial", "Выберите для управления");
        addPropertyToInventory(inventory, 16, Material.BRICKS, "buildings", "Выберите для управления");
    }

    private String formatTownInfo(JsonObject townData) {
        StringBuilder info = new StringBuilder();

        info.append(ChatColor.YELLOW).append("Название: ").append(ChatColor.WHITE)
                .append(townData.get("name").getAsString()).append("\n");
        info.append(ChatColor.YELLOW).append("Стабильность: ").append(ChatColor.WHITE)
                .append(townData.get("stability").getAsInt()).append("\n");
        info.append(ChatColor.YELLOW).append("Процветание: ").append(ChatColor.WHITE)
                .append(townData.get("prosperity").getAsInt()).append("\n");
        info.append(ChatColor.YELLOW).append("Лимит: ").append(ChatColor.WHITE)
                .append(townData.get("limit").getAsInt()).append("\n");
        info.append(ChatColor.YELLOW).append("Очки пополнения: ").append(ChatColor.WHITE)
                .append(townData.get("replenishmentPoints").getAsInt()).append("\n");
        info.append(ChatColor.YELLOW).append("Культура: ").append(ChatColor.WHITE)
                .append(townData.get("culture").getAsString()).append("\n");
        info.append(ChatColor.YELLOW).append("Доступ к морю: ").append(ChatColor.WHITE)
                .append(townData.get("hasSeaAccess").getAsBoolean() ? "Да" : "Нет").append("\n");
        info.append(ChatColor.YELLOW).append("Базовая стабильность: ").append(ChatColor.WHITE)
                .append(townData.get("baseStability").getAsInt()).append("\n");
        info.append(ChatColor.YELLOW).append("Рост стабильности к базовой: ").append(ChatColor.WHITE)
                .append(townData.get("stabilityGrowthToBase").getAsInt()).append("\n");
        info.append(ChatColor.YELLOW).append("Рост стабильности за пределами базовой: ").append(ChatColor.WHITE)
                .append(townData.get("stabilityGrowthBeyondBase").getAsInt()).append("\n");
        info.append(ChatColor.YELLOW).append("Максимальная стабильность: ").append(ChatColor.WHITE)
                .append(townData.get("maxStability").getAsInt()).append("\n");
        info.append(ChatColor.YELLOW).append("Рост процветания: ").append(ChatColor.WHITE)
                .append(townData.get("prosperityGrowth").getAsInt()).append("\n");
        info.append(ChatColor.YELLOW).append("Рост лимита: ").append(ChatColor.WHITE)
                .append(townData.get("limitGrowth").getAsInt()).append("\n");
        info.append(ChatColor.YELLOW).append("Материал меню: ").append(ChatColor.WHITE)
                .append(townData.get("menuMaterial").getAsString().toUpperCase());

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
