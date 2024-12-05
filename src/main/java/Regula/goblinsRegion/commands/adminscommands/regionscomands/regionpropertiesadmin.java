package Regula.goblinsRegion.commands.adminscommands.regionscomands;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class regionpropertiesadmin implements CommandExecutor, Listener {

    private final File townsDir = new File("towny_data/towns");

    public regionpropertiesadmin(File townsDir) {
        if (!townsDir.exists()) {
            townsDir.mkdirs(); // Создаем папку, если она не существует
        }
    }

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

    public void openRegionProperties(Player player, String townName) {
        File townFile = new File(townsDir, townName + ".json");
        if (!townFile.exists()) {
            player.sendMessage("Данные о городе " + townName + " не найдены.");
            return;
        }

        JsonObject townData;
        try (FileReader reader = new FileReader(townFile)) {
            townData = JsonParser.parseReader(reader).getAsJsonObject();
        } catch (IOException e) {
            player.sendMessage("Ошибка чтения данных города.");
            e.printStackTrace();
            return;
        }

        Inventory inventory = Bukkit.createInventory(null, 27, "Регион: " + townName);
        ItemStack infoPaper = new ItemStack(Material.PAPER);
        ItemMeta paperMeta = infoPaper.getItemMeta();
        if (paperMeta != null) {
            paperMeta.setDisplayName("Информация о регионе");
            StringBuilder loreBuilder = new StringBuilder();
            loreBuilder.append("name: ").append(townData.get("name").getAsString()).append("\n");
            loreBuilder.append("stability: ").append(townData.get("stability").getAsInt()).append("\n");
            loreBuilder.append("prosperity: ").append(townData.get("prosperity").getAsInt()).append("\n");
            loreBuilder.append("limit: ").append(townData.get("limit").getAsInt()).append("\n");
            loreBuilder.append("replenishmentPoints: ").append(townData.get("replenishmentPoints").getAsInt()).append("\n");
            loreBuilder.append("culture: ").append(townData.get("culture").getAsString()).append("\n");
            loreBuilder.append("hasSeaAccess: ").append(townData.get("hasSeaAccess").getAsBoolean()).append("\n");
            loreBuilder.append("baseStability: ").append(townData.get("baseStability").getAsInt()).append("\n");
            loreBuilder.append("stabilityGrowthToBase: ").append(townData.get("stabilityGrowthToBase").getAsInt()).append("\n");
            loreBuilder.append("stabilityGrowthBeyondBase: ").append(townData.get("stabilityGrowthBeyondBase").getAsInt()).append("\n");
            loreBuilder.append("maxStability: ").append(townData.get("maxStability").getAsInt()).append("\n");
            loreBuilder.append("prosperityGrowth: ").append(townData.get("prosperityGrowth").getAsInt()).append("\n");
            loreBuilder.append("limitGrowth: ").append(townData.get("limitGrowth").getAsInt()).append("\n");
            paperMeta.setLore(loreBuilder.toString().lines().toList());
            infoPaper.setItemMeta(paperMeta);
        }
        inventory.setItem(0, infoPaper);
        // Заполнение инвентаря
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
        addPropertyToInventory(inventory, 14, Material.CHEST, "resources", townData.get("resources").toString());

        // Добавление материала меню (menuMaterial)
        Material menuMaterial = Material.valueOf(townData.get("menuMaterial").getAsString().toUpperCase());
        addPropertyToInventory(inventory, 15, menuMaterial, "menuMaterial", townData.get("menuMaterial").getAsString());
        player.openInventory(inventory);
    }

    private void addPropertyToInventory(Inventory inventory, int slot, Material material, String propertyName, String value) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(propertyName + ": " + value);
            item.setItemMeta(meta);
        }
        inventory.setItem(slot, item);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        if (!title.startsWith("Регион: ")) return;

        event.setCancelled(true); // Запретить взаимодействие

        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;

        String townName = title.substring("Регион: ".length()); // Извлекаем имя города из названия сундука
        int slot = event.getRawSlot(); // Получение слота
        if (slot == 0) return; // Игнорируем первый слот

        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        String itemName = clickedItem.getItemMeta().getDisplayName();

        // Получение названия редактируемого параметра
        String[] parts = itemName.split(": ");
        if (parts.length < 2) return;

        String propertyName = parts[0]; // Название параметра


        String command = "/regionchangeproperties " + propertyName + " \"" + townName + "\"";
        if (itemName.startsWith("resources"))

            command = "/regionchangeresources "+ "\"" + townName + "\"";
// Закрываем инвентарь
            player.closeInventory();

// Вставляем команду в чат (как если бы игрок её сам ввёл)
            player.chat(command);
        }
    }

