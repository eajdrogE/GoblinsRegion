package Regula.goblinsRegion.commands.adminscommands.regionscomands;

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

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class regionchangeresources implements CommandExecutor, Listener {

    private final List<Resource> resources = new ArrayList<>();

    public regionchangeresources() {
        loadResourcesFromJson();
    }

    private void loadResourcesFromJson() {
        try (FileReader reader = new FileReader("resources.json")) {
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
        Inventory inventory = Bukkit.createInventory(null, 54, "Ресурсы региона: " + townName);

        int slot = 0;
        for (Resource resource : resources) {
            addResourcePair(inventory, slot, resource);
            slot += 2; // Каждая пара занимает два слота
        }

        player.openInventory(inventory);
    }

    private void addResourcePair(Inventory inventory, int startSlot, Resource resource) {
        ItemStack addItem = createResourceItem(resource.material, resource.name, "Добавить");
        ItemStack removeItem = createResourceItem(resource.material, resource.name, "Удалить");

        inventory.setItem(startSlot, addItem);
        inventory.setItem(startSlot + 1, removeItem);
    }

    private ItemStack createResourceItem(Material material, String resourceName, String action) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.YELLOW + resourceName);
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
        String message = action.equals(ChatColor.GREEN + "Добавить")
                ? "Вы добавили ресурс: " + resourceName
                : "Вы удалили ресурс: " + resourceName;

        player.sendMessage(ChatColor.AQUA + message);

        // Здесь можно добавить логику для изменения ресурсов региона в данных.
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
