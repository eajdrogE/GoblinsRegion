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

import java.io.File;

public class spiritlist implements CommandExecutor, Listener {

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

        openSpiritList(player, 0); // Открыть первую страницу
        return true;
    }

    public void openSpiritList(Player player, int page) {
        File spiritsDir = new File("towny_data/spirits");
        if (!spiritsDir.exists() || !spiritsDir.isDirectory()) {
            player.sendMessage("Папка с данными духов не найдена.");
            return;
        }
        File[] spiritFiles = spiritsDir.listFiles((dir, name) -> name.endsWith(".json"));
        if (spiritFiles == null || spiritFiles.length == 0) {
            player.sendMessage("Нет данных о духах.");
            return;
        }

        int spiritsPerPage = 45; // Количество духов на одной странице
        int totalPages = (int) Math.ceil((double) spiritFiles.length / spiritsPerPage); // Всего страниц
        page = Math.max(0, Math.min(page, totalPages - 1)); // Проверка допустимого диапазона страниц

        Inventory inventory = Bukkit.createInventory(null, 54, ChatColor.DARK_PURPLE + "SpiritList " + page);

        JsonObject spiritJson;
        int start = page * spiritsPerPage; // Индекс начала текущей страницы
        int end = Math.min(start + spiritsPerPage, spiritFiles.length); // Индекс конца текущей страницы

        for (int i = start; i < end; i++) {
            File spiritFile = spiritFiles[i];

            String formattedSpiritName = SpiritDataHandler.formatSpiritName(spiritFile.getName().replace(".json", ""));

            spiritJson = SpiritDataHandler.getSpiritData(formattedSpiritName);
            if (spiritJson == null) {
                player.sendMessage("Ошибка загрузки данных для духа: " + spiritFile.getName());
                continue;
            }

            String spiritName = spiritJson.has("Название") ? spiritJson.get("Название").getAsString() : "Неизвестный дух";
            String materialName = spiritJson.has("Материал_иконки") ? spiritJson.get("Материал_иконки").getAsString() : "PAPER";
            Material menuMaterial = Material.matchMaterial(materialName.toUpperCase());

            if (menuMaterial == null) {
                menuMaterial = Material.PAPER;
            }

            ItemStack itemStack = new ItemStack(menuMaterial);
            ItemMeta meta = itemStack.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(spiritName);
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

        if (event.getView().getTitle().startsWith(ChatColor.DARK_PURPLE + "SpiritList")) {
            event.setCancelled(true);

            int slot = event.getRawSlot();
            if (slot >= event.getView().getTopInventory().getSize()) {
                return; // Игнорируем клики в инвентаре игрока
            }

            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || clickedItem.getType() == Material.AIR) {
                return; // Игнорируем пустые слоты
            }

            if (!clickedItem.hasItemMeta() || clickedItem.getItemMeta().getDisplayName() == null) {
                return;
            }

            String itemName = clickedItem.getItemMeta().getDisplayName();

            if (itemName.equals("Предыдущая страница")) {
                String[] titleParts = event.getView().getTitle().split(" ");
                int currentPage = titleParts.length > 1 ? Integer.parseInt(titleParts[1]) : 0;
                openSpiritList(player, currentPage - 1);
            } else if (itemName.equals("Следующая страница")) {
                String[] titleParts = event.getView().getTitle().split(" ");
                int currentPage = titleParts.length > 1 ? Integer.parseInt(titleParts[1]) : 0;
                openSpiritList(player, currentPage + 1);
            } else {
                String spiritName = itemName;
                player.performCommand("spiritpropertiesadmin " + spiritName);
                player.sendMessage("Вы выбрали духа: " + spiritName);
            }
        }
    }
}
