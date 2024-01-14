package siegward.kitpvp;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class commands implements CommandExecutor {
    KitPvP plugin = KitPvP.getPlugin();
    @Override
    public boolean onCommand(@NotNull CommandSender Sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("KitPvP")){
            if (args[0].equalsIgnoreCase("kit") && args.length == 3) {
                Player p = Bukkit.getPlayer(args[1]);
                p.setMetadata("kit", new FixedMetadataValue(plugin, args[2]));
                p.setMetadata("combat", new FixedMetadataValue(plugin, 0));
                p.setMetadata("resource", new FixedMetadataValue(plugin, 100));
                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "cmi kit " + args[2] + " " + p.getName());
                p.setShieldBlockingDelay(0);
                p.setMaxHealth(20.0);
                p.setHealth(20.0);
                p.setLevel(0);
                p.setExp(0.9999f);
                p.setWalkSpeed(0.2f);
                switch (p.getMetadata("kit").get(0).asString()) {
                    case "assassin":
                        p.setWalkSpeed(0.23f);
                        break;
                    case "knight":
                        p.setMetadata("resource", new FixedMetadataValue(plugin, 200));
                        p.setExp(0f);
                        p.setWalkSpeed(0.18f);
                        break;
                    case "dark":
                    case "chaotic":
                    case "pyro":
                    case "undead":
                    case "necromancer":
                    case "creeperman":
                        p.setMetadata("resource", new FixedMetadataValue(plugin, 2000));
                        break;
                }
            }else if (args[0].equalsIgnoreCase("team") && args.length == 3){
                Player p = Bukkit.getPlayer(args[1]);
                p.setMetadata("team", new FixedMetadataValue(plugin,args[2]));
            }else if (args[0].equalsIgnoreCase("use") && args.length == 3){
                Player p = Bukkit.getPlayer(args[2]);

                //способности
                if (args[1].equalsIgnoreCase("knight.armor")){
                    int cost = 20;
                    int cooldown = 240;
                    Material item = p.getInventory().getItemInMainHand().getType();
                    float tcost = (float) cost /p.getMetadata("resource").get(0).asInt();
                    if ((p.getCooldown(item) == 0 && p.getExp() >= tcost) || (p.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE))){
                        if (!p.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
                            p.setExp(p.getExp() - tcost);
                        }
                        double Before = p.getAbsorptionAmount();
                        p.setAbsorptionAmount(Before + 12.0);
                        BukkitRunnable timer = new BukkitRunnable() {
                            int timer = 0;
                            @Override
                            public void run() {
                                if (p.getAbsorptionAmount() <= Before || timer >= 100){
                                    if (timer >= 100){
                                        p.setAbsorptionAmount(Before);
                                    }
                                    p.setCooldown(item, cooldown);
                                    this.cancel();
                                }
                                timer++;
                            }
                        };
                        timer.runTaskTimer(plugin,0,1);
                    }


                }else if (args[1].equalsIgnoreCase("assassin.pearl")){
                    int cost = 60;
                    int cooldown = 10;
                    Material item = p.getInventory().getItemInMainHand().getType();
                    float tcost = (float) cost /p.getMetadata("resource").get(0).asInt();
                    if (p.getCooldown(item) == 0 && p.getExp() >= tcost){
                        p.setExp(p.getExp() - tcost);
                        p.launchProjectile(EnderPearl.class, p.getEyeLocation().getDirection().multiply(3));
                        p.setCooldown(item,cooldown);

                    }

                }else if (args[1].equalsIgnoreCase("merc.dash")){
                    int cost = 40;
                    int cooldown = 20;
                    Material item = p.getInventory().getItemInMainHand().getType();
                    float tcost = (float) cost /p.getMetadata("resource").get(0).asInt();
                    if (p.getCooldown(item) == 0 && p.getExp() >= tcost) {
                        p.setExp(p.getExp() - tcost);
                        p.setVelocity(p.getEyeLocation().getDirection().add(p.getEyeLocation().getDirection().setX(0).setY(0.2).setZ(0)).multiply(1.5));
                        p.setCooldown(item,cooldown);
                    }

                }else if (args[1].equalsIgnoreCase("royal.firework")){
                    int cost = 40;
                    int cooldown = 10;
                    Material item = p.getInventory().getItemInMainHand().getType();
                    float tcost = (float) cost /p.getMetadata("resource").get(0).asInt();
                    if (p.getCooldown(item) == 0 && p.getExp() >= tcost){
                        p.setExp(p.getExp() - tcost);

                        Set<Entity> list = new HashSet<>();
                        //область
                        list.addAll(Objects.requireNonNull(p.getWorld()).getNearbyEntities(p.getEyeLocation().add(p.getEyeLocation().getDirection().multiply(2)), 2.0,2.0, 2.0));
                        list.remove(p);
                        for (Entity i: list){
                            LivingEntity e = (LivingEntity) i;
                            if ((e.getType().equals(EntityType.PLAYER) || e.getMetadata("alive").get(0).asBoolean()) && (!e.getMetadata("team").get(0).equals("null") || !e.getMetadata("team").get(0).equals(p.getMetadata("team").get(0)))){
                                Vector v = p.getEyeLocation().getDirection();
                                double X1 = p.getEyeLocation().getDirection().getX();
                                double Z1 = p.getEyeLocation().getDirection().getZ();
                                double x2 = X1/Math.pow(X1*X1 + Z1*Z1, 0.5);
                                double z2 = Z1/Math.pow(X1*X1 + Z1*Z1, 0.5);
                                v.setY(0.2).setX(x2).setZ(z2);
                                e.setVelocity(v.multiply(1.5));
                            }
                        }
                        p.setVelocity(p.getEyeLocation().getDirection().multiply(-1));

                        p.setCooldown(item,cooldown);

                    }
                }else if (args[1].equalsIgnoreCase("robin.bolt")){
                    int cost = 80;
                    int cooldown = 20;
                    Material item = p.getInventory().getItemInMainHand().getType();
                    float tcost = (float) cost /p.getMetadata("resource").get(0).asInt();
                    if (p.getCooldown(item) == 0 && p.getExp() >= tcost && (p.getInventory().contains(Material.ARROW) || p.getInventory().contains(Material.TIPPED_ARROW))){
                        if (p.getInventory().contains(Material.ARROW)){
                            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(),"clear " + p.getName() + " minecraft:arrow 1");
                        }else{
                            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(),"clear " + p.getName() + " minecraft:tipped_arrow 1");
                        }
                        p.setExp(p.getExp() - tcost);

                        Set<Entity> list = new HashSet<>();
                        Vector v = p.getEyeLocation().getDirection().multiply(1.5);
                        BukkitRunnable arrow = new BukkitRunnable() {
                            Location l = p.getEyeLocation();
                            int time = 0;
                            @Override
                            public void run() {
                                if (time < 60) {
                                    //частицы
                                    for (Player i : Bukkit.getOnlinePlayers()){
                                        i.spawnParticle(Particle.EXPLOSION_LARGE,l,1);
                                    }

                                    list.addAll(Objects.requireNonNull(p.getWorld()).getNearbyEntities(l, 1.0, 1.0, 1.0));
                                    list.remove(p);
                                    for (Entity i : list) {
                                        LivingEntity e = (LivingEntity) i;
                                        if ((e.getType().equals(EntityType.PLAYER) || e.getMetadata("alive").get(0).asBoolean()) && (!e.getMetadata("team").get(0).equals("null") || !e.getMetadata("team").get(0).equals(p.getMetadata("team").get(0)))) {
                                            e.damage(10.0, p);
                                        }
                                    }
                                    list.clear();
                                    l = l.add(v);
                                    time++;
                                }else {
                                    this.cancel();
                                }
                            }
                        };
                        arrow.runTaskTimer(plugin,0,1);
                        p.setCooldown(item,cooldown);
                    }

                }else if (args[1].equalsIgnoreCase("inventor.rebuild")){
                    int cooldown = 20;
                    Material item = p.getInventory().getItemInMainHand().getType();
                    if (p.getCooldown(item) == 0){
                        if (p.getExp() >= 0.99){
                            p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, 1,false,false,true));
                            p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 40, 2,false,false,true));
                        }
                        p.setExp(0f);
                        int slot = -1;
                        for (int i = 0; i < 10; i++){
                            if (p.getInventory().getItem(i) != null && p.getInventory().getItem(i).getType().equals(Material.CROSSBOW)){
                                slot = i;
                                break;
                            }
                        }
                        if (slot != -1){
                            //дробаш 1, снайперка 0
                            int mode = p.getInventory().getItem(slot).getEnchantmentLevel(Enchantment.QUICK_CHARGE);
                            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(),"clear " + p.getName() + " minecraft:crossbow 1");
                            switch (mode){
                                case 0:
                                    Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(),"cmi kit inventor.shotgun " + p.getName());
                                    break;
                                case 1:
                                    Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(),"cmi kit inventor.pp " + p.getName());
                                    break;
                            }
                        }

                        p.setCooldown(item,cooldown);
                    }
                }else if (args[1].equalsIgnoreCase("chaotic.snowball")){
                    int cost = 50;
                    Material item = p.getInventory().getItemInMainHand().getType();
                    float tcost = (float) cost /p.getMetadata("resource").get(0).asInt();
                    if (p.getCooldown(item) == 0 && p.getExp() >= tcost){
                        p.setExp(p.getExp() - tcost);
                        p.launchProjectile(Snowball.class, p.getEyeLocation().getDirection()).setMetadata("data", new FixedMetadataValue(plugin, "chaotic.snowball"));
                    }
                }else if (args[1].equalsIgnoreCase("chaotic.claster")){
                    int cooldown = 20;
                    int cost = 50;
                    Material item = p.getInventory().getItemInMainHand().getType();
                    float tcost = (float) cost /p.getMetadata("resource").get(0).asInt();
                    if (p.getCooldown(item) == 0 && p.getExp() >= tcost){
                        p.setExp(p.getExp() - tcost);
                        p.launchProjectile(Snowball.class, p.getEyeLocation().getDirection()).setMetadata("data", new FixedMetadataValue(plugin, "chaotic.snowball"));

                    }
                }
                //вот над надписью
            }
        }
        return true;
    }
}
