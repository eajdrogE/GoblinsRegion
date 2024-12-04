package Regula.goblinsRegion.commands.adminscommands.regionscomands;

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
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class changeregion implements CommandExecutor, Listener {

    private final File townsDir;

    public changeregion() {
        // Статичный путь к папке с данными городов
        this.townsDir = new File("towny_data/towns"); // Укажите фактический путь
        if (!townsDir.exists()) {
            townsDir.mkdirs(); // Создаём папку, если она не существует
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

        openRegionList(player, 0); // Открыть первую страницу
        return true;
    }

    public void openRegionList(Player player, int page) {
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

        Inventory inventory = Bukkit.createInventory(null, 54, ChatColor.DARK_GREEN + "Меню настройки регионов");


        JsonParser parser = new JsonParser();
        int start = page * townsPerPage; // Индекс начала текущей страницы
        int end = Math.min(start + townsPerPage, townFiles.length); // Индекс конца текущей страницы

        for (int i = start; i < end; i++) {
            File townFile = townFiles[i];
            try (FileReader reader = new FileReader(townFile)) {
                JsonObject townJson = parser.parse(reader).getAsJsonObject();

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
            } catch (IOException e) {
                player.sendMessage("Ошибка чтения данных города: " + townFile.getName());
                e.printStackTrace();
            }
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
        // Проверяем, что это инвентарь "Regions list"
        if (event.getView().getTitle().equals(ChatColor.DARK_GREEN + "Regions_list")) {
            event.setCancelled(true);
            if (event.getCurrentItem() != null) {
                Player player = (Player) event.getWhoClicked();
                player.sendMessage("Нажал в инвентаре");
                int slot = event.getSlot();

                // Проверяем, что клик был в пределах отображаемого инвентаря
                if (slot >= 0 && slot < 54) {
                    if (slot == 45) { // Слот для предыдущей страницы
                        String currentTitle = event.getView().getTitle();
                        int currentPage = Integer.parseInt(currentTitle.replaceAll("\\D", "")) - 1;
                        openRegionList(player, currentPage - 1);
                    } else if (slot == 53) { // Слот для следующей страницы
                        String currentTitle = event.getView().getTitle();
                        int currentPage = Integer.parseInt(currentTitle.replaceAll("\\D", "")) - 1;
                        openRegionList(player, currentPage + 1);
                    } else {
                        // Получаем предмет из слота
                        ItemStack clickedItem = event.getCurrentItem();
                        if (clickedItem.hasItemMeta() && clickedItem.getItemMeta().hasDisplayName()) {
                            String townName = clickedItem.getItemMeta().getDisplayName(); // Получаем название города из имени предмета
                            player.performCommand("regionpropertiesadmin " + townName); // Выполняем команду
                        } else {
                            player.sendMessage("Не удалось определить название города.");
                        }
                    }
                    player.sendMessage("прошел проверку на слот");
                }
            }
        }
    }
}
