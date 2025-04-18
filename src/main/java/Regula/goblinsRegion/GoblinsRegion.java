package Regula.goblinsRegion;

import Regula.goblinsRegion.commands.DBcommands.FillTownyDb;
import Regula.goblinsRegion.commands.adminscommands.regions;
import Regula.goblinsRegion.commands.adminscommands.regionscomands.*;
import Regula.goblinsRegion.commands.adminscommands.spirit.*;
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
        registerCommand("spiritlist", new spiritlist());
        getCommand("spiritlist").setExecutor(new spiritlist());
        registerCommand("regions", new regions(this));
        registerCommand("regions", new regions(this));
        registerCommand("regionchangeresources", new regionchangeresources());
        getServer().getPluginManager().registerEvents(new changeregion(), this);
        registerCommand("filltownydb", new FillTownyDb());
        registerCommand("changeregion", new changeregion());
        registerCommand("nationchange", new nationchange());
        registerCommand("nationchangelist", new nationchangelist());
        registerCommand("regionpropertiesadmin", new regionpropertiesadmin());
        registerCommand("spiritpropertiesadmin", new spiritpropertiesadmin());
        registerCommand("regionchangeproperties", new regionchangeproperties());
        getCommand("regionchangeproperties").setExecutor(new regionchangeproperties());
        getCommand("changeregion").setExecutor(new changeregion());
        getCommand("nationchange").setExecutor(new nationchange());
        getCommand("nationchangelist").setExecutor(new nationchangelist());
        getCommand("regionpropertiesadmin").setExecutor(new regionpropertiesadmin());
        getCommand("spiritpropertiesadmin").setExecutor(new spiritpropertiesadmin());
        getCommand("nationchangeproperties").setExecutor(new nationchangeproperties());
        registerCommand("nationchangeproperties", new  nationchangeproperties());
        //getServer().getPluginManager().registerEvents(new nationchangeproperties(), this);
        getServer().getPluginManager().registerEvents(new nationchange(), this);
        getServer().getPluginManager().registerEvents(new regionpropertiesadmin(), this);
        getServer().getPluginManager().registerEvents(new spiritpropertiesadmin(), this);
        getServer().getPluginManager().registerEvents(new nationchangelist(), this);
        getServer().getPluginManager().registerEvents(new spiritlist(), this);
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
        getLogger().info("GoblinRegion плагин выключен.");
    }

    /**
     * Утилита для регистрации команды
     */
    private void registerCommand(String commandName, CommandExecutor executor) {
        getCommand(commandName).setExecutor(executor);
    }
}
