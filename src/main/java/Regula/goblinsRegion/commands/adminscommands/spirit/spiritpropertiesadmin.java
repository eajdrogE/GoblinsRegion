package Regula.goblinsRegion.commands.adminscommands.spirit;

import Regula.goblinsRegion.commands.DBcommands.SpiritDataHandler;
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

public class spiritpropertiesadmin implements CommandExecutor, Listener {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Эту команду могут использовать только игроки.");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("SpiritModer")) {
            player.sendMessage("У вас нет прав для выполнения этой команды.");
            return true;
        }
        if (args.length == 0) {
            player.sendMessage("Укажите название духа. Используйте: /spiritpropertiesadmin <дух>");
            return true;
        }
        String spiritName = args[0];
        player.sendMessage("Название духа: " + spiritName);

        openSpiritProperties(player, spiritName);
        return true;
    }

    private JsonObject loadSpiritData(String spiritName) {
        return SpiritDataHandler.getSpiritData(spiritName);
    }

    private void openSpiritProperties(Player player, String spiritName) {
        JsonObject spiritData = loadSpiritData(spiritName);
        if (spiritData == null) {
            player.sendMessage("Данные о духе " + spiritName + " не найдены.");
            return;
        }

        Inventory inventory = Bukkit.createInventory(null, 27, "Дух: " + spiritName);

        // Добавляем данные в инвентарь
        addBasicPropertyItems(inventory, spiritData);
        addSpecialPropertyItems(inventory, spiritData, spiritName);
        player.openInventory(inventory);
    }

    private void addBasicPropertyItems(Inventory inventory, JsonObject spiritData) {
        addItemToInventoryIfPresent(inventory, Material.PLAYER_HEAD, ChatColor.DARK_BLUE + "Информация_о_духе",
                "Название: " + spiritData.get("Название").getAsString());
        addItemToInventoryIfPresent(inventory, Material.BOOK, "Описание",
                spiritData.get("Описание").getAsString());
        addItemToInventoryIfPresent(inventory, Material.ENCHANTED_BOOK, "Тип_духа",
                "Тип: " + spiritData.get("Тип_духа").getAsInt());
        addItemToInventoryIfPresent(inventory, Material.PAPER, "Влияние_на_культуру",
                "Эффект: " + spiritData.get("Влияние_на_культуру").getAsString());
        addItemToInventoryIfPresent(inventory, Material.BEACON, "Стабильность",
                "Текущая: " + spiritData.get("Стабильность").getAsInt());
        addItemToInventoryIfPresent(inventory, Material.GOLD_INGOT, "Процветание",
                "Текущее: " + spiritData.get("Процветание").getAsInt());
        addItemToInventoryIfPresent(inventory, Material.IRON_BLOCK, "Лимит",
                "Текущий: " + spiritData.get("Лимит").getAsInt());
        addItemToInventoryIfPresent(inventory, Material.BREAD, "Очки_пополнения",
                "Текущие: " + spiritData.get("Очки_пополнения").getAsInt());
        addItemToInventoryIfPresent(inventory, Material.DIAMOND, "Плюс_базовая_стабильность",
                "Плюс: " + spiritData.get("Плюс_базовая_стабильность").getAsInt());
        addItemToInventoryIfPresent(inventory, Material.REDSTONE, "Рост_стабильности_к_базовой",
                "Рост: " + spiritData.get("Рост_стабильности_к_базовой").getAsInt());
        addItemToInventoryIfPresent(inventory, Material.REDSTONE_TORCH, "Рост_стабильности_за_пределами_базовой",
                "Рост: " + spiritData.get("Рост_стабильности_за_пределами_базовой").getAsInt());
        addItemToInventoryIfPresent(inventory, Material.EMERALD, "Максимальная_стабильность",
                "Максимальная: " + spiritData.get("Максимальная_стабильность").getAsInt());
        addItemToInventoryIfPresent(inventory, Material.GOLD_BLOCK, "Рост_процветания",
                "Рост: " + spiritData.get("Рост_процветания").getAsInt());
        addItemToInventoryIfPresent(inventory, Material.IRON_INGOT, "Рост_лимита",
                "Рост: " + spiritData.get("Рост_лимита").getAsInt());
    }

    private void addSpecialPropertyItems(Inventory inventory, JsonObject spiritData, String spiritName) {
        addItemToInventory(inventory, Material.CHEST, "Список_регионов", "Выберите для управления");
        Material menuMaterial = Material.valueOf(spiritData.has("Материал_иконки") ? spiritData.get("Материал_иконки").getAsString().toUpperCase() : "PAPER");
        addItemToInventory(inventory, menuMaterial, "Материал_иконки", "Выберите для управления");
    }

    private void addItemToInventoryIfPresent(Inventory inventory, Material material, String name, String value) {
        if (value != null && !value.isEmpty() && !value.equals("0") && !value.equals("[]")) {
            addItemToInventory(inventory, material, name, value);
        }
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
        String title = event.getView().getTitle();
        if (!title.startsWith("Дух: ")) return;

        event.setCancelled(true);

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        Player player = (Player) event.getWhoClicked();
        String spiritName = title.substring("Дух: ".length());
        handlePropertySelection(player, clickedItem, SpiritDataHandler.formatSpiritName(spiritName));
    }

    private void handlePropertySelection(Player player, ItemStack clickedItem, String spiritName) {
        String itemName = clickedItem.getItemMeta().getDisplayName();
        String command;

        if (itemName.equals("Список_регионов")) {
            command = "/spiritchangeregions " + spiritName + "";
        } else {
            String propertyName = itemName.split(": ")[0];
            command = "/spiritchangeproperties " + propertyName + " " + spiritName;
        }

        player.closeInventory();
        player.chat(command);
    }
}
