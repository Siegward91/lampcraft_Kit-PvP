package siegward.kitpvp;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

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

                }else if (args[1].equalsIgnoreCase("royal.escape")){
                    int cost = 40;
                    int cooldown = 20;

                }
            }
        }
        return true;
    }
}
