package Regula.goblinsRegion.commands.adminscommands.regionscomands;

import Regula.goblinsRegion.GoblinsRegion;
import com.google.gson.JsonArray;
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
import Regula.goblinsRegion.commands.DBcommands.TownsDataHandler;

import java.util.List;

public class regionchangeresources implements CommandExecutor, Listener {

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
            player.sendMessage("Укажите название города. Используйте: /regionchangeresources <город>");
            return true;
        }

        String townName = args[0];
        openResourcesMenu(player, townName);
        return true;
    }

    private void openResourcesMenu(Player player, String townName) {
        JsonObject townData = TownsDataHandler.getRegionData(townName);
        if (townData == null) {
            player.sendMessage(ChatColor.RED + "Данные о городе " + townName + " не найдены.");
            return;
        }

        JsonArray resourceArray = townData.getAsJsonArray("resources");
        if (resourceArray == null || resourceArray.size() == 0) {
            player.sendMessage(ChatColor.RED + "Ресурсы для города " + townName + " не найдены.");
            return;
        }

        Inventory inventory = Bukkit.createInventory(null, 54, "Ресурсы региона: " + townName);

        int slot = 0;
        for (int i = 0; i < resourceArray.size(); i++) {
            JsonObject resourceJson = resourceArray.get(i).getAsJsonObject();
            String resourceName = resourceJson.get("name").getAsString();
            String materialName = resourceJson.get("material").getAsString();
            int resourceAmount = resourceJson.get("amount").getAsInt();

            Material material = Material.getMaterial(materialName);
            if (material != null) {
                addResourcePair(inventory, slot, material, resourceName, resourceAmount);
                slot += 2; // Каждая пара занимает два слота
            }
        }

        player.openInventory(inventory);
    }

    private void addResourcePair(Inventory inventory, int startSlot, Material material, String resourceName, int amount) {
        ItemStack addItem = createResourceItem(material, resourceName, ChatColor.GREEN +"Добавить", amount);
        ItemStack removeItem = createResourceItem(material, resourceName, ChatColor.RED + "Удалить", amount);

        inventory.setItem(startSlot, addItem);
        inventory.setItem(startSlot + 1, removeItem);
    }

    private ItemStack createResourceItem(Material material, String resourceName, String action, int amount) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(resourceName + " (" + amount + ")");
            meta.setLore(List.of(action));
            item.setItemMeta(meta);
        }
        return item;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        if (!title.startsWith("Ресурсы региона: ")) return;

        event.setCancelled(true);

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        Player player = (Player) event.getWhoClicked();
        String displayName = clickedItem.getItemMeta().getDisplayName();
        String resourceName = displayName.split(" ")[0]; // Получаем только первое слово
        String action = clickedItem.getItemMeta().getLore().get(0);

        handleResourceAction(player, resourceName, action);
    }

    private void handleResourceAction(Player player, String resourceName, String action) {
        String townName = extractTownNameFromInventoryTitle(player.getOpenInventory().getTitle());
        if (townName == null) {
            player.sendMessage(ChatColor.RED + "Не удалось определить город.");
            return;
        }

        JsonObject townData = TownsDataHandler.getRegionData(townName);
        if (townData == null) {
            player.sendMessage(ChatColor.RED + "Данные о городе не найдены.");
            return;
        }

        JsonArray resourceArray = townData.getAsJsonArray("resources");
        if (resourceArray == null) {
            player.sendMessage(ChatColor.RED + "Ресурсы для города не найдены.");
            return;
        }

        // Определяем изменение количества
        int delta = action.equals(ChatColor.GREEN + "Добавить") ? 1 : -1; // Пример: изменяем на 10 единиц

        boolean updated = updateResourceAmount(resourceArray, resourceName, delta);
        if (updated) {
            // Сохраняем изменённые ресурсы в данных города
            townData.add("resources", resourceArray);
            TownsDataHandler.saveCityData(townData, townName); // Сохранение данных города
            reopenResourcesMenu(player, townName); // Обновляем GUI для игрока
        } else {
            player.sendMessage(ChatColor.RED + "Ресурс \"" + resourceName + "\" не найден.");
        }
    }


    private boolean updateResourceAmount(JsonArray resourceArray, String resourceName, int delta) {
        for (int i = 0; i < resourceArray.size(); i++) {
            JsonObject resource = resourceArray.get(i).getAsJsonObject();
            if (resource.get("name").getAsString().equals(resourceName)) {
                int currentAmount = resource.get("amount").getAsInt();
                int newAmount = Math.max(currentAmount + delta, 0); // Убедимся, что не уходим в отрицательное
                resource.addProperty("amount", newAmount); // Обновляем значение
                return true;
            }
        }
        return false; // Ресурс не найден
    }


    private void reopenResourcesMenu(Player player, String townName) {
        openResourcesMenu(player, townName);
    }

    private String extractTownNameFromInventoryTitle(String title) {
        if (title.startsWith("Ресурсы региона: ")) {
            return title.substring("Ресурсы региона: ".length());
        }
        return null;
    }
}
