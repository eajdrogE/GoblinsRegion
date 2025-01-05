package Regula.goblinsRegion.commands.DBcommands;

import com.google.gson.*;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Town;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Random;

public class FillTownyDb implements CommandExecutor {

    private final JsonArray availableResources = new JsonArray();
    private final Random random = new Random();

    public FillTownyDb() {
        loadResources();
    }

    private void loadResources() {
        try (FileReader reader = new FileReader("towny_data/resources.json")) {
            JsonObject resourceData = JsonParser.parseReader(reader).getAsJsonObject();
            availableResources.addAll(resourceData.getAsJsonArray("resources"));
        } catch (IOException e) {
            Bukkit.getLogger().severe("Не удалось загрузить доступные ресурсы: " + e.getMessage());
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Команду может выполнять только игрок.");
            return true;
        }

        Player player = (Player) sender;

        // Проверка прав с LuckPerms
        LuckPerms luckPerms = LuckPermsProvider.get();
        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        if (user == null || !user.getCachedData().getPermissionData().checkPermission("GoblinsMaster").asBoolean()) {
            player.sendMessage("У вас нет прав для выполнения этой команды.");
            return true;
        }

        // Директории для сохранения данных
        File townsDir = new File("towny_data/towns");
        File nationsDir = new File("towny_data/nations");

        if (!townsDir.exists()) townsDir.mkdirs();
        if (!nationsDir.exists()) nationsDir.mkdirs();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        // Обработка городов
        Collection<Town> towns = TownyUniverse.getInstance().getTowns();
        for (Town town : towns) {
            JsonObject townJson = new JsonObject();
            townJson.addProperty("Название", TownsDataHandler.formatCityName(town.getName()));
            townJson.addProperty("Стабильность", 10);
            townJson.addProperty("Процветание", 200);
            townJson.addProperty("Лимит", 1);
            townJson.addProperty("Очки_пополнения", 0);
            townJson.addProperty("Культура", "Default Culture");
            townJson.addProperty("Доступ_к_морю", false);
            townJson.addProperty("Базовая стабильность", 10);
            townJson.addProperty("Рост_стабильности_к_базовой", 1);
            townJson.addProperty("Рост_стабильности-за_пределами_базовой", 0);
            townJson.addProperty("Максимальная_стабильность", 20);
            townJson.addProperty("Рост_процветания", 0);
            townJson.addProperty("Рост_лимита", 0);
            townJson.addProperty("Материал_иконки", "PAPER");

            // Запись основных данных города в папку towny_data/towns
            try (FileWriter writer = new FileWriter(new File(townsDir, TownsDataHandler.formatCityName(town.getName()) + ".json"))) {
                gson.toJson(townJson, writer);
            } catch (IOException e) {
                player.sendMessage("Ошибка записи данных города: " + TownsDataHandler.formatCityName(town.getName()));
                e.printStackTrace();
            }

            // Запись ресурсов города в папку towny_data/towns_resources
            JsonObject townResourcesJson = new JsonObject();
            townResourcesJson.add("resources", getRegionResources(town.getName()));
            TownsDataHandler.saveCityResources(townResourcesJson, town.getName());
        }

        // Обработка наций
        Collection<Nation> nations = TownyUniverse.getInstance().getNations();
        for (Nation nation : nations) {
            JsonObject nationJson = new JsonObject();
            nationJson.addProperty("Название", nation.getName());
            nationJson.addProperty("Доход", 0);
            nationJson.addProperty("Лимит", 0);
            nationJson.addProperty("Лимит_наступления", 0);
            nationJson.addProperty("Лимит_защиты", 0);
            nationJson.addProperty("Стабильность", 10);
            nationJson.addProperty("Процветание", 200);
            nationJson.addProperty("Эффективность", 0.5);
            nationJson.addProperty("Престиж", 100);
            nationJson.addProperty("Репутация_знати", 5);
            nationJson.addProperty("Репутация_духовенства", 5);
            nationJson.addProperty("Репутация_крестьян", 5);
            nationJson.addProperty("Бесчестие", 0);
            nationJson.addProperty("Очки_пополнения", 0);
            nationJson.addProperty("Статус", "Default Status");
            nationJson.addProperty("Главный_бог", "Default Deity");
            nationJson.addProperty("Основная_раса", "Default Race");
            nationJson.addProperty("Имя", "Default Ruler");
            nationJson.addProperty("Возраст", 30);
            nationJson.addProperty("Раса", "Default Race");
            nationJson.addProperty("Материал_иконки", "PAPER");

            // Использование методов NationDataHandler для сохранения наций
            NationDataHandler.saveNationData(nationJson, NationDataHandler.formatNationName(nation.getName()));
        }

        player.sendMessage("Данные городов и наций успешно сохранены.");
        return true;
    }

    private JsonArray getRegionResources(String cityName) {
        JsonArray resources = new JsonArray();

        // Заполнение ресурсов для города на основе данных из resources.json
        for (int i = 0; i < availableResources.size(); i++) {
            JsonObject resource = availableResources.get(i).getAsJsonObject();
            JsonObject townResource = new JsonObject();
            townResource.addProperty("name", resource.get("name").getAsString());
            townResource.addProperty("material", resource.get("material").getAsString());

            // Добавление случайного количества ресурсов для города
            int randomAmount = 0;
            townResource.addProperty("amount", randomAmount);

            resources.add(townResource);
        }

        return resources;
    }


}
