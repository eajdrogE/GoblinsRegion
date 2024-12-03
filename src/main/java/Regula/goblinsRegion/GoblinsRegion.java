package Regula.goblinsRegion;

import Regula.goblinsRegion.commands.DBcommands.FillTownyDb;
import Regula.goblinsRegion.commands.adminscommands.regions;
import Regula.goblinsRegion.commands.adminscommands.regionscomands.changeregion;
import Regula.goblinsRegion.commands.adminscommands.regionscomands.regionpropertiesadmin;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class GoblinsRegion extends JavaPlugin {

    @Override
    public void onEnable() {
        // Регистрация команд


        registerCommand("regions", new regions(this));
        registerCommand("filltownydb", new FillTownyDb());
        registerCommand("changeregion", new FillTownyDb());

        File townsDir = null;
        townsDir = new File(getDataFolder(), "towny_data/towns");
        getCommand("changeregion").setExecutor(new changeregion());
        getCommand("regionpropertiesadmin").setExecutor(new regionpropertiesadmin(townsDir));
        getServer().getPluginManager().registerEvents(new regionpropertiesadmin(townsDir), this);

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