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
            loreBuilder.append("stability: ").append(townData.get("stability").getAsString()).append("\n");
            loreBuilder.append("prosperity: ").append(townData.get("prosperity").getAsString()).append("\n");
            loreBuilder.append("limit: ").append(townData.get("limit").getAsString()).append("\n");
            loreBuilder.append("replenishmentPoints: ").append(townData.get("replenishmentPoints").getAsString()).append("\n");
            paperMeta.setLore(loreBuilder.toString().lines().toList());
            infoPaper.setItemMeta(paperMeta);
        }
        inventory.setItem(0, infoPaper);
        // Заполнение инвентаря
        addPropertyToInventory(inventory, 1, Material.PAPER, "name", townData.get("name").getAsString());
        addPropertyToInventory(inventory, 2, Material.ANVIL, "stability", townData.get("stability").getAsString());
        addPropertyToInventory(inventory, 3, Material.GOLD_INGOT, "prosperity", townData.get("prosperity").getAsString());
        addPropertyToInventory(inventory, 4, Material.IRON_SWORD, "limit", townData.get("limit").getAsString());
        addPropertyToInventory(inventory, 5, Material.BREAD, "replenishmentPoints", townData.get("replenishmentPoints").getAsString());

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

        // Формирование команды
        String command = "/regionchangeproperties " + propertyName + " \"" + townName + "\"";
        player.closeInventory();
        String tellrawCommand = String.format(
                "tellraw %s {\"text\":\"Команда для ввода в терминал: %s\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"%s\"}}",
                player.getName(), command, command);

// Отправляем команду на выполнение
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), tellrawCommand);
    }
}
