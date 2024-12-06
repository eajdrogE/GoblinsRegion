package Regula.goblinsRegion.commands.adminscommands.regionscomands;

import Regula.goblinsRegion.GoblinsRegion;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class regionchangeresources implements CommandExecutor, Listener {

    private final List<Resource> resources = new ArrayList<>();
    private final File townsDir = new File("towny_data/towns");
    public regionchangeresources(File townsDir) {
        if (!townsDir.exists()) {
            townsDir.mkdirs(); // Создаем папку, если она не существует
        }
    }
    private JsonObject loadTownData(String townName) {
        // Используем переданный путь
        File townFile = new File(townsDir, townName + ".json");

        // Логируем путь
        Bukkit.getLogger().info("Путь к файлу города: " + townFile.getAbsolutePath());

        if (!townFile.exists()) {
            Bukkit.getLogger().warning("Файл города не найден: " + townFile.getAbsolutePath());
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(townFile))) {
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }

            return JsonParser.parseString(stringBuilder.toString()).getAsJsonObject();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void loadResourcesFromJson() {
        try (FileReader reader = new FileReader("towny_data/resources.json")) {
            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
            JsonArray resourceArray = jsonObject.getAsJsonArray("resources");

            for (int i = 0; i < resourceArray.size(); i++) {
                JsonObject resourceObj = resourceArray.get(i).getAsJsonObject();
                String name = resourceObj.get("name").getAsString();
                Material material = Material.valueOf(resourceObj.get("material").getAsString().toUpperCase());

                resources.add(new Resource(name, material));
            }
        } catch (IOException e) {
            e.printStackTrace();
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
            player.sendMessage("Укажите название города. Используйте: /regionchangeresources <город>");
            return true;
        }

        String townName = args[0];
        openResourcesMenu(player, townName);
        return true;
    }

    private void openResourcesMenu(Player player, String townName) {
        JsonObject townData = loadTownData(townName);
        if (townData == null) {
            player.sendMessage(ChatColor.RED + "Данные о городе " + townName + " не найдены.");
            return;
        }

        JsonArray resourceArray = townData.getAsJsonArray("resources");
        Inventory inventory = Bukkit.createInventory(null, 54, "Ресурсы региона: " + townName);

        int slot = 0;
        for (Resource resource : resources) {
            int resourceAmount = getResourceAmount(resourceArray, resource.name);
            addResourcePair(inventory, slot, resource, resourceAmount);
            slot += 2; // Каждая пара занимает два слота
        }

        player.openInventory(inventory);
    }

    private int getResourceAmount(JsonArray resourceArray, String resourceName) {
        for (int i = 0; i < resourceArray.size(); i++) {
            JsonObject resource = resourceArray.get(i).getAsJsonObject();
            if (resource.get("name").getAsString().equals(resourceName)) {
                return resource.get("amount").getAsInt();
            }
        }
        return 0; // Если ресурс не найден, возвращаем 0
    }

    private void addResourcePair(Inventory inventory, int startSlot, Resource resource, int amount) {
        ItemStack addItem = createResourceItem(resource.material, resource.name, "Добавить", amount);
        ItemStack removeItem = createResourceItem(resource.material, resource.name, "Удалить", amount);

        inventory.setItem(startSlot, addItem);
        inventory.setItem(startSlot + 1, removeItem);
    }


    private ItemStack createResourceItem(Material material, String resourceName, String action, int amount) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.YELLOW + resourceName + " (" + amount + ")");
            meta.setLore(List.of(ChatColor.GREEN + action));
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
        String resourceName = clickedItem.getItemMeta().getDisplayName();
        String action = clickedItem.getItemMeta().getLore().get(0);

        handleResourceAction(player, resourceName, action);
    }

    private void handleResourceAction(Player player, String resourceName, String action) {
        String townName = extractTownNameFromInventoryTitle(player.getOpenInventory().getTitle());
        if (townName == null) {
            player.sendMessage(ChatColor.RED + "Не удалось определить город.");
            return;
        }

        JsonObject townData = loadTownData(townName);
        if (townData == null) {
            player.sendMessage(ChatColor.RED + "Данные о городе не найдены.");
            return;
        }

        JsonArray resourceArray = townData.getAsJsonArray("resources");
        boolean updated = updateResourceAmount(resourceArray, resourceName, action.equals(ChatColor.GREEN + "Добавить") ? 1 : -1);

        if (updated) {
            saveTownData(townName, townData);
            player.sendMessage(ChatColor.AQUA + "Ресурс \"" + resourceName + "\" успешно обновлён.");
            reopenResourcesMenu(player, townName);
        } else {
            player.sendMessage(ChatColor.RED + "Ресурс \"" + resourceName + "\" не найден.");
        }
    }
    private boolean updateResourceAmount(JsonArray resourceArray, String resourceName, int delta) {
        for (int i = 0; i < resourceArray.size(); i++) {
            JsonObject resource = resourceArray.get(i).getAsJsonObject();
            if (resource.get("name").getAsString().equals(resourceName)) {
                int currentAmount = resource.get("amount").getAsInt();
                int newAmount = Math.max(currentAmount + delta, 0); // Гарантируем, что количество не станет отрицательным
                resource.addProperty("amount", newAmount);
                return true;
            }
        }
        return false; // Если ресурс не найден
    }
    private void reopenResourcesMenu(Player player, String townName) {
        openResourcesMenu(player, townName);
    }
//    private JsonObject loadTownData(String townName) {
//        File townsDir = new File("towny_data/towns");
//        File townFile = new File(townsDir, townName + ".json");
//        if (!townFile.exists()) return null;
//
//        try (FileReader reader = new FileReader(townFile)) {
//            return JsonParser.parseReader(reader).getAsJsonObject();
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

    private void saveTownData(String townName, JsonObject townData) {
        // Путь к файлу, где будем сохранять данные
        File townFile = new File(townsDir, townName + ".json");

        try (FileWriter writer = new FileWriter(townFile)) {
            writer.write(townData.toString());
            Bukkit.getLogger().info("Данные города успешно сохранены: " + townFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String extractTownNameFromInventoryTitle(String title) {
        if (title.startsWith("Ресурсы региона: ")) {
            return title.substring("Ресурсы региона: ".length());
        }
        return null;
    }

    private static class Resource {
        final String name;
        final Material material;

        Resource(String name, Material material) {
            this.name = name;
            this.material = material;
        }
    }
}
