package Regula.goblinsRegion.commands.adminscommands.regionscomands;

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
import Regula.goblinsRegion.commands.DBcommands.NationDataHandler;

import java.io.File;

public class nationchangelist implements CommandExecutor, Listener {

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

        openNationList(player, 0); // Открыть первую страницу
        return true;
    }

    public void openNationList(Player player, int page) {
        File nationsDir = new File("towny_data/nations");
        if (!nationsDir.exists() || !nationsDir.isDirectory()) {
            player.sendMessage("Папка с данными наций не найдена.");
            return;
        }

        File[] nationFiles = nationsDir.listFiles((dir, name) -> name.endsWith(".json"));
        if (nationFiles == null || nationFiles.length == 0) {
            player.sendMessage("Нет данных о нациях.");
            return;
        }

        int nationsPerPage = 45; // Количество наций на одной странице
        int totalPages = (int) Math.ceil((double) nationFiles.length / nationsPerPage); // Всего страниц
        page = Math.max(0, Math.min(page, totalPages - 1)); // Проверка допустимого диапазона страниц

        Inventory inventory = Bukkit.createInventory(null, 54, ChatColor.DARK_BLUE + "NationList");

        JsonObject nationJson;
        int start = page * nationsPerPage; // Индекс начала текущей страницы
        int end = Math.min(start + nationsPerPage, nationFiles.length); // Индекс конца текущей страницы

        for (int i = start; i < end; i++) {
            File nationFile = nationFiles[i];

            String formattedNationName = NationDataHandler.formatNationName(nationFile.getName().replace(".json", ""));
            nationJson = NationDataHandler.loadNationData(formattedNationName); // Загрузка данных нации

            if (nationJson == null) {
                player.sendMessage("Ошибка загрузки данных для нации: " + nationFile.getName());
                continue;
            }
            // Теперь мы используем nationJson как объект JsonObject
            String nationName = nationJson.has("Название") ? nationJson.get("Название").getAsString() : "Неизвестная нация";
            String materialName = nationJson.has("Материал_иконки") ? nationJson.get("Материал_иконки").getAsString() : "MAP";
            Material menuMaterial = Material.matchMaterial(materialName.toUpperCase());

            if (menuMaterial == null) {
                menuMaterial = Material.MAP;
            }
            ItemStack itemStack = new ItemStack(menuMaterial);
            ItemMeta meta = itemStack.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(nationName);
                itemStack.setItemMeta(meta);
            }
            inventory.addItem(itemStack);
        }
        // Добавление стрелок навигации
        if (page > 0) {
            ItemStack previousPage = new ItemStack(Material.ARROW);
            ItemMeta meta = previousPage.getItemMeta();
            if (meta != null) {
                meta.setDisplayName("Предыдущая страница");
                previousPage.setItemMeta(meta);
            }
            inventory.setItem(45, previousPage);
        }
        if (page < totalPages - 1) {
            ItemStack nextPage = new ItemStack(Material.ARROW);
            ItemMeta meta = nextPage.getItemMeta();
            if (meta != null) {
                meta.setDisplayName("Следующая страница");
                nextPage.setItemMeta(meta);
            }
            inventory.setItem(53, nextPage);
        }

        player.openInventory(inventory);
    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (event.getView().getTitle().equals(ChatColor.DARK_BLUE + "NationList")) {
            event.setCancelled(true);

            int slot = event.getRawSlot();
            if (slot >= event.getView().getTopInventory().getSize()) {
                return;
            }
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || clickedItem.getType() == Material.AIR) {
                return;
            }
            if (!clickedItem.hasItemMeta() || clickedItem.getItemMeta().getDisplayName() == null) {
                return;
            }
            String itemName = clickedItem.getItemMeta().getDisplayName();
            if (itemName.equals("Предыдущая страница")) {
                String[] titleParts = event.getView().getTitle().split(" ");
                int currentPage = titleParts.length > 1 ? Integer.parseInt(titleParts[1]) : 0;
                openNationList(player, currentPage - 1);
            } else if (itemName.equals("Следующая страница")) {
                String[] titleParts = event.getView().getTitle().split(" ");
                int currentPage = titleParts.length > 1 ? Integer.parseInt(titleParts[1]) : 0;
                openNationList(player, currentPage + 1);
            } else {
                String nationName = itemName;
                player.performCommand("nationchange " + nationName);
                player.sendMessage("Вы выбрали нацию: " + nationName);
            }
        }
    }
}
