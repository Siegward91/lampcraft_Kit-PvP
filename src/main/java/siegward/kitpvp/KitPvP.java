package siegward.kitpvp;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import siegward.kitpvp.Managers.EntityManager;
import siegward.kitpvp.Managers.InteractionManager;
import siegward.kitpvp.Managers.PlayerManager;
import siegward.kitpvp.abilities.commands;
import siegward.kitpvp.utils.listeners;

public final class KitPvP extends JavaPlugin implements Listener {
    public static KitPvP plugin;
    public static KitPvP getPlugin(){
        return plugin;
    }

    @Override
    public void onEnable() {
        plugin = this;
        Bukkit.getServer().getPluginManager().registerEvents(new listeners(),this);
        getCommand("KitPvP").setExecutor(new commands());
        EntityManager.init();
        PlayerManager.init();
        InteractionManager.init();
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "team add RED");
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "team modify RED friendlyFire false");
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "team modify RED collisionRule pushOtherTeams");
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "team modify RED color red");
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "team add BLUE");
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "team modify BLUE friendlyFire false");
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "team modify BLUE collisionRule pushOtherTeams");
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "team modify RED color blue");

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
