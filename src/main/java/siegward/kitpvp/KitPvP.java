package siegward.kitpvp;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

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
        BukkitRunnable ResourcesRegen = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player i: Bukkit.getOnlinePlayers()) {
                    if (!i.getMetadata("kit").get(0).asString().equalsIgnoreCase("null")) {

                        if (i.getMetadata("kit").get(0).asString().equalsIgnoreCase("merc") && i.isBlocking()) {
                            if (i.getExp() < 0.01) {
                                i.setCooldown(Material.SHIELD, 60);
                                ItemStack itemStack = i.getInventory().getItemInOffHand();
                                ItemMeta itemMeta = i.getInventory().getItemInOffHand().getItemMeta();
                                itemMeta.setUnbreakable(true);
                                itemStack.setItemMeta(itemMeta);
                                i.getInventory().setItemInOffHand(new ItemStack(Material.AIR, 1));
                                BukkitRunnable lildelay = new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        i.getInventory().setItemInOffHand(itemStack);
                                        this.cancel();

                                    }
                                };
                                lildelay.runTaskTimer(plugin, 1, 1);

                            } else {
                                i.setExp(Math.max(0f, i.getExp() - (float) (0.8 / (i.getMetadata("resource").get(0).asInt()))));
                            }
                        } else if (!i.getMetadata("kit").get(0).asString().equalsIgnoreCase("knight")) {
                            i.setExp(Math.min(0.9999f, i.getExp() + (float) (0.5 / (i.getMetadata("resource").get(0).asInt()))));
                        }

                    }
                }
            }
        };
        ResourcesRegen.runTaskTimer(plugin,1,1);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
