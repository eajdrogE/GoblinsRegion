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
            townJson.addProperty("name", TownsDataHandler.formatCityName(town.getName()));
            townJson.addProperty("stability", 10);
            townJson.addProperty("prosperity", 200);
            townJson.addProperty("limit", 1);
            townJson.addProperty("replenishmentPoints", 0);
            townJson.addProperty("culture", "Default Culture");
            townJson.addProperty("hasSeaAccess", false);
            townJson.addProperty("baseStability", 10);
            townJson.addProperty("stabilityGrowthToBase", 1);
            townJson.addProperty("stabilityGrowthBeyondBase", 0);
            townJson.addProperty("maxStability", 20);
            townJson.addProperty("prosperityGrowth", 0);
            townJson.addProperty("limitGrowth", 0);
            townJson.addProperty("menuMaterial", "PAPER");

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
            nationJson.addProperty("name", TownsDataHandler.formatCityName(nation.getName()));
            nationJson.addProperty("income", 0);
            nationJson.addProperty("limit", 0);
            nationJson.addProperty("attackLimit", 0);
            nationJson.addProperty("defenseLimit", 0);
            nationJson.addProperty("stability", 10);
            nationJson.addProperty("prosperity", 200);
            nationJson.addProperty("efficiency", 0.5);
            nationJson.addProperty("prestige", 100);
            nationJson.addProperty("nobilityLoyalty", 5);
            nationJson.addProperty("clergyLoyalty", 5);
            nationJson.addProperty("commonersLoyalty", 5);
            nationJson.addProperty("dishonor", 0);
            nationJson.addProperty("replenishmentPoints", 0);
            nationJson.addProperty("status", "Default Status");
            nationJson.addProperty("mainDeity", "Default Deity");
            nationJson.addProperty("mainRace", "Default Race");
            nationJson.addProperty("rulerName", "Default Ruler");
            nationJson.addProperty("rulerAge", 30);
            nationJson.addProperty("rulerRace", "Default Race");
            nationJson.addProperty("menuMaterial", "PAPER");

            // Запись данных нации в папку towny_data/nations
            try (FileWriter writer = new FileWriter(new File(nationsDir, TownsDataHandler.formatCityName(nation.getName()) + ".json"))) {
                gson.toJson(nationJson, writer);
            } catch (IOException e) {
                player.sendMessage("Ошибка записи данных нации: " + nation.getName());
                e.printStackTrace();
            }
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
