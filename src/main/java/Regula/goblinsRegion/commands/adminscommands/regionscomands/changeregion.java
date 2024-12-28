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
import Regula.goblinsRegion.commands.DBcommands.TownsDataHandler;

import java.io.File;

public class changeregion implements CommandExecutor, Listener {

    public changeregion() {
        // Конструктор без townsDir, используем методы из TownsDataHandler
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

        openRegionList(player, 0); // Открыть первую страницу
        return true;
    }

    public void openRegionList(Player player, int page) {
        File townsDir = new File("towny_data/towns");
        if (!townsDir.exists() || !townsDir.isDirectory()) {
            player.sendMessage("Папка с данными городов не найдена.");
            return;
        }
        File[] townFiles = townsDir.listFiles((dir, name) -> name.endsWith(".json"));
        if (townFiles == null || townFiles.length == 0) {
            player.sendMessage("Нет данных о городах.");
            return;
        }

        int townsPerPage = 45; // Количество городов на одной странице
        int totalPages = (int) Math.ceil((double) townFiles.length / townsPerPage); // Всего страниц
        page = Math.max(0, Math.min(page, totalPages - 1)); // Проверка допустимого диапазона страниц

        Inventory inventory = Bukkit.createInventory(null, 54, ChatColor.DARK_GREEN + "RegionList");

        JsonObject townJson;
        int start = page * townsPerPage; // Индекс начала текущей страницы
        int end = Math.min(start + townsPerPage, townFiles.length); // Индекс конца текущей страницы

        for (int i = start; i < end; i++) {
            File townFile = townFiles[i];

            // Форматируем имя города с помощью formatCityName
            String formattedTownName = TownsDataHandler.formatCityName(townFile.getName().replace(".json", ""));

            // Получаем данные о городе с помощью TownsDataHandler
            townJson = TownsDataHandler.getRegionData(formattedTownName);
            if (townJson == null) {
                player.sendMessage("Ошибка загрузки данных для города: " + townFile.getName());
                continue;
            }

            String townName = townJson.has("name") ? townJson.get("name").getAsString() : "Неизвестный город";
            String materialName = townJson.has("menuMaterial") ? townJson.get("menuMaterial").getAsString() : "PAPER";
            Material menuMaterial = Material.matchMaterial(materialName.toUpperCase());

            if (menuMaterial == null) {
                menuMaterial = Material.PAPER;
            }

            ItemStack itemStack = new ItemStack(menuMaterial);
            ItemMeta meta = itemStack.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(townName);
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
        // Проверяем, что это наш кастомный инвентарь
        if (event.getView().getTitle().equals(ChatColor.DARK_GREEN + "RegionList")) {
            event.setCancelled(true); // Блокируем взаимодействие
            // Проверка, что клик в верхнем инвентаре
            int slot = event.getRawSlot();
            if (slot >= event.getView().getTopInventory().getSize()) {
                return; // Игнорируем клики в инвентаре игрока
            }
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || clickedItem.getType() == Material.AIR) {
                return; // Игнорируем пустые слоты
            }
            // Проверяем наличие метаданных
            if (!clickedItem.hasItemMeta() || clickedItem.getItemMeta().getDisplayName() == null) {
                return;
            }
            String itemName = clickedItem.getItemMeta().getDisplayName();

            // Обработка перехода между страницами
            if (itemName.equals("Предыдущая страница")) {
                String[] titleParts = event.getView().getTitle().split(" "); // Разделяем заголовок для получения страницы
                int currentPage = titleParts.length > 1 ? Integer.parseInt(titleParts[1]) : 0;
                openRegionList(player, currentPage - 1); // Переход на предыдущую страницу
            } else if (itemName.equals("Следующая страница")) {
                String[] titleParts = event.getView().getTitle().split(" "); // Разделяем заголовок для получения страницы
                int currentPage = titleParts.length > 1 ? Integer.parseInt(titleParts[1]) : 0;
                openRegionList(player, currentPage + 1); // Переход на следующую страницу
            } else {
                // Если это не стрелка, обрабатываем как выбор города
                String townName = itemName;
                player.performCommand("regionpropertiesadmin " + townName); // Выполняем команду
                player.sendMessage("Вы выбрали город: " + townName); // Уведомляем игрока
            }
        }
    }
}
