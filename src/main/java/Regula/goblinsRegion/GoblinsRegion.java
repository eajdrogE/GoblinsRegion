package Regula.goblinsRegion;

import Regula.goblinsRegion.commands.DBcommands.FillTownyDb;
import Regula.goblinsRegion.commands.adminscommands.regions;
import Regula.goblinsRegion.commands.adminscommands.regionscomands.*;
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
        registerCommand("changenation", new changenation());
        registerCommand("changenationlist", new changenationlist());
        registerCommand("regionpropertiesadmin", new regionpropertiesadmin());
        registerCommand("regionchangeproperties", new regionchangeproperties());
        getCommand("regionchangeproperties").setExecutor(new regionchangeproperties());
        getCommand("changeregion").setExecutor(new changeregion());
        getCommand("changenation").setExecutor(new changenation());
        getCommand("changenationlist").setExecutor(new changenationlist());
        getCommand("regionpropertiesadmin").setExecutor(new regionpropertiesadmin());
        getCommand("changenationproperties").setExecutor(new changenationproperties());
        registerCommand("changenationproperties", new  changenationproperties());
        //getServer().getPluginManager().registerEvents(new changenationproperties(), this);
        getServer().getPluginManager().registerEvents(new changenation(), this);
        getServer().getPluginManager().registerEvents(new regionpropertiesadmin(), this);
        getServer().getPluginManager().registerEvents(new changenationlist(), this);
        getServer().getPluginManager().registerEvents(new regionchangeresources(), this);
        getLogger().info("       ,      ,      ");
        getLogger().info("      /(.-\"\"-.)\\");
        getLogger().info("  |\\  \\/      \\/  /|");
        getLogger().info("  | \\ / =.  .= \\ / |");
        getLogger().info("  \\( \\   o\\/o   / )/");
        getLogger().info("   \\_, '-/  \\-' ,_/ ");
        getLogger().info("     /   \\__/   \\  ");
        getLogger().info("     \\ \\__/\\__/ / ");
        getLogger().info("   ___\\ \\|--|/ /___");
        getLogger().info("  /`   \\      /    `\\");
        getLogger().info(" /      '----'       \\");
        getLogger().info(" GoblinRegion by Egordjae");

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
