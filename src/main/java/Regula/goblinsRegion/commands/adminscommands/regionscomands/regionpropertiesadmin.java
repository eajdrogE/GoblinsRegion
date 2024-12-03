package Regula.goblinsRegion.commands.adminscommands.regionscomands;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class regionpropertiesadmin implements CommandExecutor, Listener {

    private final File townsDir;

    public regionpropertiesadmin(File townsDir) {
        this.townsDir = new File("towny_data/towns"); // Укажите ваш путь
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

        Inventory inventory = Bukkit.createInventory(null, 27, "Свойства: " + townName);

        // Заполнение инвентаря
        addPropertyToInventory(inventory, 0, Material.PAPER, "Имя", townData.get("name").getAsString());
        addPropertyToInventory(inventory, 1, Material.ANVIL, "Стабильность", townData.get("stability").getAsString());
        addPropertyToInventory(inventory, 2, Material.GOLD_INGOT, "Благосостояние", townData.get("prosperity").getAsString());
        addPropertyToInventory(inventory, 3, Material.IRON_SWORD, "Лимит", townData.get("limit").getAsString());
        addPropertyToInventory(inventory, 4, Material.BREAD, "Очки восстановления", townData.get("replenishmentPoints").getAsString());
        addPropertyToInventory(inventory, 5, Material.NETHER_STAR, "Культура", townData.get("culture").getAsString());
        addPropertyToInventory(inventory, 6, Material.IRON_PICKAXE, "Ресурсы", townData.get("resources").toString());
        addPropertyToInventory(inventory, 7, Material.WATER_BUCKET, "Доступ к морю", townData.get("hasSeaAccess").getAsString());
        addPropertyToInventory(inventory, 8, Material.IRON_BLOCK, "Базовая стабильность", townData.get("baseStability").getAsString());
        addPropertyToInventory(inventory, 9, Material.IRON_NUGGET, "Прирост стабильности до базового", townData.get("stabilityGrowthToBase").getAsString());
        addPropertyToInventory(inventory, 10, Material.CHAIN, "Прирост стабильности сверх базового", townData.get("stabilityGrowthBeyondBase").getAsString());
        addPropertyToInventory(inventory, 11, Material.IRON_INGOT, "Максимальная стабильность", townData.get("maxStability").getAsString());
        addPropertyToInventory(inventory, 12, Material.GOLD_BLOCK, "Прирост благосостояния", townData.get("prosperityGrowth").getAsString());
        addPropertyToInventory(inventory, 13, Material.CAKE, "Прирост лимита", townData.get("limitGrowth").getAsString());
        addPropertyToInventory(inventory, 14, Material.PAPER, "Материал меню", townData.get("menuMaterial").getAsString());

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

    public void handleInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().startsWith("Свойства: ")) return;

        event.setCancelled(true); // Запретить взаимодействие

        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;

        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();

        // Обработка кликов по слотам
        switch (slot) {
            case 0 -> method1(player);
            case 1 -> method2(player);
            case 2 -> method3(player);
            case 3 -> method4(player);
            case 4 -> method5(player);
            case 5 -> method6(player);
            case 6 -> method7(player);
            case 7 -> method8(player);
            case 8 -> method9(player);
            case 9 -> method10(player);
            case 10 -> method11(player);
            case 11 -> method12(player);
            case 12 -> method13(player);
            case 13 -> method14(player);
        }
    }

    // Пример методов (пустые, реализуйте по необходимости)
    private void method1(Player player) { player.sendMessage("Вызван method1"); }
    private void method2(Player player) { player.sendMessage("Вызван method2"); }
    private void method3(Player player) { player.sendMessage("Вызван method3"); }
    private void method4(Player player) { player.sendMessage("Вызван method4"); }
    private void method5(Player player) { player.sendMessage("Вызван method5"); }
    private void method6(Player player) { player.sendMessage("Вызван method6"); }
    private void method7(Player player) { player.sendMessage("Вызван method7"); }
    private void method8(Player player) { player.sendMessage("Вызван method8"); }
    private void method9(Player player) { player.sendMessage("Вызван method9"); }
    private void method10(Player player) { player.sendMessage("Вызван method10"); }
    private void method11(Player player) { player.sendMessage("Вызван method11"); }
    private void method12(Player player) { player.sendMessage("Вызван method12"); }
    private void method13(Player player) { player.sendMessage("Вызван method13"); }
    private void method14(Player player) { player.sendMessage("Вызван method14"); }
}
