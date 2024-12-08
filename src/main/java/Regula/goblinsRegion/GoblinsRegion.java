package Regula.goblinsRegion;

import Regula.goblinsRegion.commands.DBcommands.FillTownyDb;
import Regula.goblinsRegion.commands.adminscommands.regions;
import Regula.goblinsRegion.commands.adminscommands.regionscomands.changeregion;
import Regula.goblinsRegion.commands.adminscommands.regionscomands.regionchangeproperties;
import Regula.goblinsRegion.commands.adminscommands.regionscomands.regionchangeresources;
import Regula.goblinsRegion.commands.adminscommands.regionscomands.regionpropertiesadmin;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class GoblinsRegion extends JavaPlugin {

    @Override
    public void onEnable() {
        // Создание директории для towny_data, если она не существует
        File townsDir = new File(getDataFolder(), "towny_data");
        if (!townsDir.exists()) {
            townsDir.mkdirs(); // Создаем папку, если её нет
        }

        // Регистрация команд
        registerCommand("regions", new regions(this));
        registerCommand("regionchangeresources", new regionchangeresources());
        getServer().getPluginManager().registerEvents(new changeregion(), this);
        registerCommand("filltownydb", new FillTownyDb());
        registerCommand("changeregion", new changeregion());
        registerCommand("regionpropertiesadmin", new regionpropertiesadmin());
        registerCommand("regionchangeproperties", new regionchangeproperties());
        getCommand("regionchangeproperties").setExecutor(new regionchangeproperties());
        getCommand("changeregion").setExecutor(new changeregion());
        getCommand("regionpropertiesadmin").setExecutor(new regionpropertiesadmin());
        getServer().getPluginManager().registerEvents(new regionpropertiesadmin(), this);

        getServer().getPluginManager().registerEvents(new regionchangeresources(), this);
        getLogger().info("GoblinsRegion плагин успешно включен!");
    }

    @Override
    public void onDisable() {
        getLogger().info("GoblinsRegion плагин выключен.");
    }

    /**
     * Утилита для регистрации команды
     */
    private void registerCommand(String commandName, CommandExecutor executor) {
        getCommand(commandName).setExecutor(executor);
    }
}
