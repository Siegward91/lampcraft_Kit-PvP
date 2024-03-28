package siegward.kitpvp.Managers;

import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import siegward.kitpvp.KitPvP;
import siegward.kitpvp.utils.KitType;


import java.util.HashSet;
import java.util.Set;

public class InteractionManager {
    private static final KitPvP plugin = KitPvP.getPlugin();
    public static void init(){
        Location hurt = new Location(Bukkit.getWorld("world"), -15.5, 11, -15.5);
        ItemStack ishurt = new ItemStack(Material.TIPPED_ARROW);
        PotionMeta imhurt = (PotionMeta) ishurt.getItemMeta();
        imhurt.setColor(Color.RED);
        imhurt.displayName(Component.text(ChatColor.RED + "Стрела вреда"));
        imhurt.addCustomEffect(new PotionEffect(PotionEffectType.HARM, 1, 1,false,true,true),true);
        ishurt.setItemMeta(imhurt);

        Location poison = new Location(Bukkit.getWorld("world"), -20.5, 15, -16.5);
        ItemStack ispoison = new ItemStack(Material.TIPPED_ARROW);
        PotionMeta impoison = (PotionMeta) ispoison.getItemMeta();
        impoison.setColor(Color.GREEN);
        impoison.displayName(Component.text(ChatColor.DARK_GREEN + "Стрела отравления"));
        impoison.addCustomEffect(new PotionEffect(PotionEffectType.POISON, 100, 2,false,true,true),true);
        ispoison.setItemMeta(impoison);

        BoundingBox mana = new BoundingBox(95, 27, 27, 18, 19, 10);
        BoundingBox sword = new BoundingBox(24, 17, -38, 5, 5, -23);
        World w = Bukkit.getWorld("world");

        Set<Entity> items = new HashSet<>();
        Set<Entity> players = new HashSet<>();
        BukkitRunnable mainHandler30t = new BukkitRunnable() {

            @Override
            public void run() {
                items.clear();
                items.addAll(w.getNearbyEntities(hurt,0.5,0.5,0.5, (entity) -> entity.getType().equals(EntityType.DROPPED_ITEM)));
                if (items.isEmpty()) w.dropItem(hurt,ishurt);
                for (Entity e : items){
                    ItemStack item = ((Item) e).getItemStack();
                    if (item.getAmount() < 10){
                        w.dropItem(hurt,ishurt).setVelocity(new Vector(0,0,0));
                    }
                }
                items.clear();

                items.addAll(w.getNearbyEntities(poison,0.5,0.5,0.5, (entity) -> entity.getType().equals(EntityType.DROPPED_ITEM)));
                if (items.isEmpty()) w.dropItem(poison,ispoison);
                for (Entity e : items){
                    ItemStack item = ((Item) e).getItemStack();
                    if (item.getAmount() < 10){
                        w.dropItem(poison,ispoison).setVelocity(new Vector(0,0,0));
                    }
                }
                items.clear();
            }
        };
        mainHandler30t.runTaskTimer(plugin,0,30);

        BukkitRunnable mainHandler1t = new BukkitRunnable() {
            @Override
            public void run() {
                players.clear();
                players.addAll(w.getNearbyEntities(mana,(entity) -> entity.getType().equals(EntityType.PLAYER)));
                for (Entity e : players){
                    Player p = (Player) e;
                    PlayerModel m = PlayerManager.getModelByPlayer(p);
                    if (m.getKit().equals(KitType.PYRO) || m.getKit().equals(KitType.SATANIST) || m.getKit().equals(KitType.CHAOTIC)){
                        if (m.getResourceDifferencePerSecond() > 0) {
                            m.setResourceCurrent(m.getResourceCurrent() + m.getResourceDifferencePerSecond() / 10);
                        }
                    }
                }
                players.clear();

                players.addAll(w.getNearbyEntities(sword,(entity) -> entity.getType().equals(EntityType.PLAYER)));
                for (Entity e : players){
                    Player p = (Player) e;
                    PlayerModel m = PlayerManager.getModelByPlayer(p);
                    if (m.getKit().equals(KitType.MERC) || m.getKit().equals(KitType.ASSASSIN) || m.getKit().equals(KitType.KNIGHT)){
                        p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*2, 1, false,true,true));
                    }
                }
                players.clear();
            }
        };
        mainHandler1t.runTaskTimer(plugin,0,1);
    }
}
