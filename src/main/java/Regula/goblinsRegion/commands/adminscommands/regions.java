package Regula.goblinsRegion.commands.adminscommands;

import Regula.goblinsRegion.commands.adminscommands.regionscomands.changeregion;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
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
import org.bukkit.plugin.java.JavaPlugin;

public class regions implements CommandExecutor, Listener {

    private final JavaPlugin plugin;

    public regions(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Эту команду может выполнять только игрок.");
            return true;
        }

        Player player = (Player) sender;

        // Проверка прав через LuckPerms
        LuckPerms luckPerms = LuckPermsProvider.get();
        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        if (user == null || !user.getCachedData().getPermissionData().checkPermission("RegionModer").asBoolean()) {
            player.sendMessage(ChatColor.RED + "У вас нет прав для выполнения этой команды.");
            return true;
        }

        // Создание инвентаря
        Inventory inventory = Bukkit.createInventory(null, 18, ChatColor.DARK_GREEN + "Меню настройки регионов");

        // Добавление элементов в инвентарь
        addItem(inventory, Material.OAK_FENCE, 0, ChatColor.YELLOW + "Изменение регионов");
        addItem(inventory, Material.COBBLESTONE_WALL, 1, ChatColor.YELLOW + "Изменение наций");
        addItem(inventory, Material.ANVIL, 2, ChatColor.YELLOW + "Изменение игровых духов");
        addItem(inventory, Material.ENCHANTING_TABLE, 3, ChatColor.YELLOW + "Изменение решений");
        addItem(inventory, Material.OAK_SIGN, 4, ChatColor.YELLOW + "Изменение событий");
        addItem(inventory, Material.IRON_SWORD, 5, ChatColor.YELLOW + "Ведение войн");
        addItem(inventory, Material.PAPER, 6, ChatColor.YELLOW + "Внешняя политика");

        // Заполнение остальных ячеек серым стеклом
        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        if (fillerMeta != null) fillerMeta.setDisplayName(" ");
        filler.setItemMeta(fillerMeta);

        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, filler);
            }
        }

        // Открытие инвентаря для игрока
        player.openInventory(inventory);
        return true;
    }

    private void addItem(Inventory inventory, Material material, int slot, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }
        inventory.setItem(slot, item);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(ChatColor.DARK_GREEN + "Меню настройки регионов")) {
            event.setCancelled(true); // Запретить взаимодействие с предметами

            int slot = event.getRawSlot(); // Получаем слот, на который кликнули

            Player player = (Player) event.getWhoClicked();

            // Выполнение методов в зависимости от слота
            switch (slot) {
                case 0: // Изменение регионов
                    player.chat("/changeregion");
                    break;
                case 1: // Изменение наций
                    player.chat("/changenation");
                    break;
                case 2: // Изменение игровых духов
                    method3(player);
                    break;
                case 3: // Изменение решений
                    method4(player);
                    break;
                case 4: // Изменение событий
                    method5(player);
                    break;
                case 5: // Ведение войн
                    method6(player);
                    break;
                case 6: // Внешняя политика
                    method7(player);
                    break;
                default:
                    // Если слот не привязан к действию, ничего не делаем
                    break;
            }
        }
    }

    // Заглушки для методов


    private void method2(Player player) {
        player.sendMessage("Вызван метод 2: Изменение наций");
    }

    private void method3(Player player) {
        player.sendMessage("Вызван метод 3: Изменение игровых духов");
    }

    private void method4(Player player) {
        player.sendMessage("Вызван метод 4: Изменение решений");
    }

    private void method5(Player player) {
        player.sendMessage("Вызван метод 5: Изменение событий");
    }

    private void method6(Player player) {
        player.sendMessage("Вызван метод 6: Ведение войн");
    }

    private void method7(Player player) {
        player.sendMessage("Вызван метод 7: Внешняя политика");
    }
}
