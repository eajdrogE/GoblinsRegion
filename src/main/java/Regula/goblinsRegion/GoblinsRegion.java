package Regula.goblinsRegion;

import Regula.goblinsRegion.commands.DBcommands.FillTownyDb;
import Regula.goblinsRegion.commands.adminscommands.regions;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

public final class GoblinsRegion extends JavaPlugin {

    @Override
    public void onEnable() {
        // Регистрация команд
        registerCommand("regions", new regions(this));
        registerCommand("filltownydb", new FillTownyDb());

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