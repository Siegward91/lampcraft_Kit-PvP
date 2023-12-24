package siegward.kitpvp;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class listeners implements Listener {
    KitPvP plugin = KitPvP.getPlugin();
    @EventHandler
    public void OnJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        p.setMetadata("kit", new FixedMetadataValue(plugin,"null"));
        p.setMetadata("team", new FixedMetadataValue(plugin,"null"));
        p.setMetadata("combat", new FixedMetadataValue(plugin,0));
        p.setMetadata("resource", new FixedMetadataValue(plugin,100));
        p.setMaxHealth(20.0);
        p.setHealth(20.0);
        p.setWalkSpeed(0.2f);
        p.setLevel(0);
        p.setExp(0.9999f);
    }

    @EventHandler
    public void OnRespawn(PlayerRespawnEvent e){
        Player p = e.getPlayer();
        BukkitRunnable lildelay = new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(),"clear " + p.getName());
                p.setMetadata("kit", new FixedMetadataValue(plugin,"null"));
                p.setMetadata("team", new FixedMetadataValue(plugin,"null"));
                p.setMetadata("combat", new FixedMetadataValue(plugin,0));
                p.setMetadata("resource", new FixedMetadataValue(plugin,100));
                p.setMaxHealth(20.0);
                p.setHealth(20.0);
                p.setWalkSpeed(0.2f);
                p.setLevel(0);
                p.setExp(0.9999f);
                this.cancel();
            }
        };
        lildelay.runTaskTimer(plugin,1,5);
    }

    @EventHandler
    public void OnDamage(EntityDamageByEntityEvent e){
        if (e.getEntity() instanceof Player){
            Player t = (Player) e.getEntity();
            if (e.getDamager() instanceof Player){
                Player d = (Player) e.getDamager();
                if (t.getMetadata("team").get(0).asString().equals(t.getMetadata("team").get(0).asString()) && !t.getMetadata("team").get(0).asString().equalsIgnoreCase("null")){
                    e.setCancelled(true);
                    return;
                }
                if (d.getMetadata("combat").get(0).asInt() ==  0){
                    d.setMetadata("combat", new FixedMetadataValue(plugin,600));
                    BukkitRunnable combat = new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (d.getMetadata("combat").get(0).asInt() > 0){
                                d.setMetadata("combat", new FixedMetadataValue(plugin,d.getMetadata("combat").get(0).asInt() - 1));
                            }else {
                                this.cancel();
                            }
                        }
                    };
                    combat.runTaskTimer(plugin,0,1);
                }else {
                    d.setMetadata("combat", new FixedMetadataValue(plugin,600));
                }
            }
            if (t.getMetadata("kit").get(0).asString().equalsIgnoreCase("merc") && t.isBlocking() && e.getFinalDamage() == 0){
                if (!t.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)){
                    t.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100, 0,false,false,true));
                    t.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 0,false,false,true));
                }else if (t.getPotionEffect(PotionEffectType.INCREASE_DAMAGE).getAmplifier() == 0){
                    t.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100, 1,false,false,true));
                    t.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 0,false,false,true));
                    t.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 0,false,false,true));
                }else if (t.getPotionEffect(PotionEffectType.INCREASE_DAMAGE).getAmplifier() == 1){
                    t.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, t.getPotionEffect(PotionEffectType.INCREASE_DAMAGE).getDuration() + 60, 1,false,false,true));
                    t.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, t.getPotionEffect(PotionEffectType.DAMAGE_RESISTANCE).getDuration() + 60, 0,false,false,true));
                    t.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, t.getPotionEffect(PotionEffectType.SPEED).getDuration() + 60, 0,false,false,true));
                }
            }
            if (t.getMetadata("kit").get(0).asString().equalsIgnoreCase("knight") && !t.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)){
                t.setExp(Math.min(0.9999f, (float) (t.getExp() + ((e.getDamage()*7)/t.getMetadata("resource").get(0).asInt()))));
                if (t.getExp() >= 0.9999f){
                    t.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 250, 1,false,false,true));
                    t.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 250, 0,false,false,true));
                    t.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 250, 1,false,false,true));
                    BukkitRunnable rage = new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (t.getExp() == 0) {
                                this.cancel();
                            }
                            t.setExp(Math.max(0f, t.getExp() - (float) (0.83 / t.getMetadata("resource").get(0).asInt())));
                        }
                    };
                    rage.runTaskTimer(plugin,0,1);
                }
            }
        }
    }

    @EventHandler
    public void OnDeath(PlayerDeathEvent e){
        Player p = e.getPlayer();
        if (p.getMetadata("combat").get(0).asInt() > 0){
            ItemStack soul = new ItemStack(Material.NETHER_STAR,1);
            p.getWorld().dropItem(p.getLocation(),soul);
        }
        if (p.getKiller() != null) {
            Player k = p.getKiller();
            if (k.getMetadata("kit").get(0).asString().equalsIgnoreCase("assassin") || k.getMetadata("kit").get(0).asString().equalsIgnoreCase("merc") || k.getMetadata("kit").get(0).asString().equalsIgnoreCase("knight")){
                BukkitRunnable PassiveHealing = new BukkitRunnable() {
                    int timer = 0;
                    @Override
                    public void run() {
                        if (timer < 8) {
                            k.setHealth(Math.min(k.getMaxHealth(), k.getHealth() + k.getMaxHealth()/16));
                        }else {
                            this.cancel();
                        }
                        timer++;
                    }
                };
                PassiveHealing.runTaskTimer(plugin, 0,5);
                if (k.getMetadata("kit").get(0).asString().equalsIgnoreCase("knight") && k.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)){
                    k.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 250, 1,false,false,true));
                    k.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 250, 0,false,false,true));
                    k.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 250, 1,false,false,true));
                    k.setExp(0.9999f);
                }
            }
        }
    }
}
