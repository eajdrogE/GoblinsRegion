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
        // Регистрация команд
        File townsDir = null;
        townsDir = new File(getDataFolder(), "towny_data/towns");

        registerCommand("regions", new regions(this));
        registerCommand("regionchangeresources", new regionchangeresources());
        getServer().getPluginManager().registerEvents(new changeregion(townsDir), this);
        registerCommand("filltownydb", new FillTownyDb());
        registerCommand("changeregion", new changeregion(townsDir));
        registerCommand("regionpropertiesadmin", new regionpropertiesadmin(townsDir));
        registerCommand("regionchangeproperties", new regionchangeproperties(townsDir));
        getCommand("regionchangeproperties").setExecutor(new regionchangeproperties(townsDir));
        getCommand("changeregion").setExecutor(new changeregion(townsDir));
        getCommand("regionpropertiesadmin").setExecutor(new regionpropertiesadmin(townsDir));
        getServer().getPluginManager().registerEvents(new regionpropertiesadmin(townsDir), this);


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