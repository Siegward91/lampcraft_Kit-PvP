package siegward.kitpvp;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

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

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
