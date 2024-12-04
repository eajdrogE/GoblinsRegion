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
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class regionpropertiesadmin implements CommandExecutor, Listener {

    private final File townsDir = new File("towny_data/towns");
    private String townName;
    private final Map<Player, EditData> editQueue = new HashMap<>(); // Хранение редактируемых параметров для игроков

    public regionpropertiesadmin(File townsDir) {
        // Укажите ваш путь
        if (!townsDir.exists()) {
            townsDir.mkdirs(); // Создаем папку, если она не существует
        }
    }

    // Класс для хранения данных редактируемого параметра и региона
    private static class EditData {
        String propertyName;
        String regionName;

        EditData(String propertyName, String regionName) {
            this.propertyName = propertyName;
            this.regionName = regionName;
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

        townName = args[0];
        player.sendMessage("Название города: " + townName);

        openRegionProperties(player);
        return true;
    }

    public void openRegionProperties(Player player) {
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

        // Заполнение инвентаря
        addPropertyToInventory(inventory, 0, Material.PAPER, "Имя", townData.get("name").getAsString());
        addPropertyToInventory(inventory, 1, Material.ANVIL, "Стабильность", townData.get("stability").getAsString());
        addPropertyToInventory(inventory, 2, Material.GOLD_INGOT, "Благосостояние", townData.get("prosperity").getAsString());
        addPropertyToInventory(inventory, 3, Material.IRON_SWORD, "Лимит", townData.get("limit").getAsString());
        addPropertyToInventory(inventory, 4, Material.BREAD, "Очки восстановления", townData.get("replenishmentPoints").getAsString());
        // Добавьте другие параметры аналогично

        // Заполнение оставшихся слотов серым стеклом
        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        if (fillerMeta != null) {
            fillerMeta.setDisplayName(" ");
            filler.setItemMeta(fillerMeta);
        }

        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, filler);
            }
        }

        player.openInventory(inventory);
    }

    private void addPropertyToInventory(Inventory inventory, int slot, Material material, String name, String value) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name + ": " + value);
            item.setItemMeta(meta);
        }
        inventory.setItem(slot, item);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().startsWith("Регион: ")) return;

        event.setCancelled(true); // Запретить взаимодействие

        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;

        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        String itemName = clickedItem.getItemMeta().getDisplayName();

        // Получение названия редактируемого параметра
        String[] parts = itemName.split(": ");
        if (parts.length < 2) return;

        String propertyName = parts[0]; // Название параметра
        player.sendMessage("Введите новое значение для параметра: " + propertyName);
        player.closeInventory();

        // Сохранение в очередь редактирования с именем региона
        editQueue.put(player, new EditData(propertyName, townName));
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        // Проверяем, находится ли игрок в очереди редактирования
        if (!editQueue.containsKey(player)) return;

        EditData editData = editQueue.get(player); // Получаем данные для редактирования
        String propertyName = editData.propertyName; // Имя редактируемого параметра
        String regionName = editData.regionName; // Имя региона
        String newValue = event.getMessage(); // Новое значение от игрока
        event.setCancelled(true); // Блокируем отправку сообщения в чат

        // Формируем путь к файлу JSON
        File townFile = new File(townsDir, regionName + ".json");

        // Логирование для отладки
        player.sendMessage("Название города: " + regionName);
        player.sendMessage("Путь к файлу: " + townFile.getAbsolutePath());
        player.sendMessage("Файл существует: " + townFile.exists());
        player.sendMessage("Можно читать: " + townFile.canRead());

        // Проверяем, существует ли файл
        if (!townFile.exists()) {
            player.sendMessage("Файл города не найден.");
            editQueue.remove(player);
            return;
        }

        // Чтение данных из JSON
        JsonObject townData;
        try (FileReader reader = new FileReader(townFile)) {
            townData = JsonParser.parseReader(reader).getAsJsonObject();
        } catch (IOException e) {
            player.sendMessage("Ошибка чтения данных города.");
            e.printStackTrace();
            editQueue.remove(player);
            return;
        }

        // Обновляем указанное свойство
        try {
            townData.addProperty(propertyName, newValue); // Добавляем или изменяем свойство

            // Записываем обновленный JSON обратно в файл
            try (FileWriter writer = new FileWriter(townFile)) {
                writer.write(townData.toString());
            }

            player.sendMessage("Параметр \"" + propertyName + "\" успешно обновлён на \"" + newValue + "\".");
        } catch (Exception e) {
            player.sendMessage("Ошибка при обновлении данных города.");
            e.printStackTrace();
        }

        editQueue.remove(player); // Удаление из очереди редактирования
    }
}
