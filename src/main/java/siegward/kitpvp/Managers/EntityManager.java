package siegward.kitpvp.Managers;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import siegward.kitpvp.KitPvP;
import siegward.kitpvp.utils.TeamType;

import java.util.ArrayList;
import java.util.List;

public class EntityManager {
    private static final KitPvP plugin = KitPvP.getPlugin();
    private static final List<MobModel> mobList = new ArrayList<>();
    private static final List<MobModel> deadMobs = new ArrayList<>();


    public static void init(){
        BukkitRunnable mainHandler = new BukkitRunnable() {
            @Override
            public void run() {
                for (MobModel mob : mobList){
                    mob.incrementTicksLived(1);

                    if (mob.getTicksLiveMax() > 0 && mob.getTicksLived() > mob.getTicksLiveMax()){
                        LivingEntity l = mob.getEntity();
                        if (l.getHealth() == 0){
                            EntityManager.mobDeath(mob);
                        }else {
                            //частицы старения
                            l.setHealth(Math.max(0, l.getHealth() - (mob.getTicksLived() - mob.getTicksLiveMax()) * 0.02));
                            l.getWorld().spawnParticle(Particle.ASH, mob.getEntity().getLocation().add(0,1,0), 5, 0.2, 0.4, 0.2, 0);
                        }
                    }

                    //частицы
                    if (mob.getEntity().getType().equals(EntityType.SILVERFISH)) {
                        if (mob.getTicksLived() < 60) {
                            mob.getEntity().getWorld().spawnParticle(Particle.PORTAL, mob.getEntity().getLocation(), 2, 0, 0, 0, 1);
                        } else if (mob.getTicksLived() == 60) {
                            mob.getEntity().getWorld().spawnParticle(Particle.PORTAL, mob.getEntity().getLocation(), 10, 0, 0, 0, 1);
                        }else if (mob.getTicksLived() == 110) {
                            mob.getEntity().getWorld().spawnParticle(Particle.VILLAGER_HAPPY, mob.getEntity().getLocation(), 20, 0.2, 0.1, 0.2);
                        }
                    }

                }
                for (MobModel mob : deadMobs){
                    mobList.remove(mob);
                }
            }
        };
        mainHandler.runTaskTimer(plugin,0,1);
    }

    public static void spawnNoAIMob(Player p, EntityType type, Component name, int ticksLiveMax){
        LivingEntity mob = (LivingEntity) p.getWorld().spawnEntity(p.getLocation(), type);

        if (PlayerManager.getModelByPlayer(p).getTeam() == null){
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "team join " + p.getName() + "_p " + mob.getUniqueId());
        }else if (PlayerManager.getModelByPlayer(p).getTeam().equals(TeamType.RED)){
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "team join RED" + mob.getUniqueId());
        }else if (PlayerManager.getModelByPlayer(p).getTeam().equals(TeamType.BLUE)){
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "team join BLUE" + mob.getUniqueId());
        }

        mob.setAI(false);
        mob.setCollidable(false);
        mob.setSilent(true);
        mob.customName(name);
        mobList.add(new MobModel(mob, p, false,ticksLiveMax));
    }

    public static void spawnZombie(Player p, EntityType type, Component name, int ticksLiveMax, double movementSpeed, int armor, int attackDamage, double attackSpeed, int maxHealth, double knockbackResistance){
        Husk mob = (Husk) p.getWorld().spawnEntity(p.getLocation(), type);

        if (PlayerManager.getModelByPlayer(p).getTeam() == null){
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "team join " + p.getName() + "_p " + mob.getUniqueId());
        }else if (PlayerManager.getModelByPlayer(p).getTeam().equals(TeamType.RED)){
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "team join RED" + mob.getUniqueId());
        }else if (PlayerManager.getModelByPlayer(p).getTeam().equals(TeamType.BLUE)){
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "team join BLUE" + mob.getUniqueId());
        }

        mob.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(movementSpeed);
        mob.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(armor);
        mob.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(attackDamage);
        //mob.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(attackSpeed);
        mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHealth);
        mob.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(knockbackResistance);
        mob.getAttribute(Attribute.ZOMBIE_SPAWN_REINFORCEMENTS).setBaseValue(0);

        mob.setHealth(maxHealth);
        mob.customName(name);
        mob.setCustomNameVisible(true);
        mob.setCanPickupItems(false);
        mob.setShouldBurnInDay(false);
        mob.setAdult();

        mobList.add(new MobModel(mob, p, true,ticksLiveMax));
    }
    public static void spawnSkeleton(Player p, EntityType type, Component name, int ticksLiveMax, double movementSpeed, int armor,int power, int maxHealth, double knockbackResistance){
        Stray mob = (Stray) p.getWorld().spawnEntity(p.getLocation(), type);

        if (PlayerManager.getModelByPlayer(p).getTeam() == null){
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "team join " + p.getName() + "_p " + mob.getUniqueId());
        }else if (PlayerManager.getModelByPlayer(p).getTeam().equals(TeamType.RED)){
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "team join RED" + mob.getUniqueId());
        }else if (PlayerManager.getModelByPlayer(p).getTeam().equals(TeamType.BLUE)){
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "team join BLUE" + mob.getUniqueId());
        }

        mob.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(movementSpeed);
        mob.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(armor);
        mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHealth);
        mob.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(knockbackResistance);

        mob.setHealth(maxHealth);
        mob.customName(name);
        mob.setCustomNameVisible(true);
        mob.setCanPickupItems(false);
        mob.setShouldBurnInDay(false);

        ItemStack bow = new ItemStack(Material.BOW);
        ItemMeta bowMeta = bow.getItemMeta();
        bowMeta.setUnbreakable(true);
        bowMeta.addEnchant(Enchantment.ARROW_DAMAGE, power, true);
        bow.setItemMeta(bowMeta);
        mob.getEquipment().setItemInMainHand(bow);

        mobList.add(new MobModel(mob, p, true,ticksLiveMax));
    }
    public static void spawnCreeper(Player p, EntityType type, Component name, int ticksLiveMax, double movementSpeed, int armor, int explosion, int fuse, int maxHealth, double knockbackResistance){
        Creeper mob = (Creeper) p.getWorld().spawnEntity(p.getLocation(), type);

        if (PlayerManager.getModelByPlayer(p).getTeam() == null){
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "team join " + p.getName() + "_p " + mob.getUniqueId());
        }else if (PlayerManager.getModelByPlayer(p).getTeam().equals(TeamType.RED)){
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "team join RED" + mob.getUniqueId());
        }else if (PlayerManager.getModelByPlayer(p).getTeam().equals(TeamType.BLUE)){
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "team join BLUE" + mob.getUniqueId());
        }

        mob.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(movementSpeed);
        mob.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(armor);
        mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHealth);
        mob.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(knockbackResistance);

        mob.setHealth(maxHealth);
        mob.customName(name);
        mob.setCustomNameVisible(true);
        mob.setCanPickupItems(false);
        mob.setExplosionRadius(explosion);
        mob.setMaxFuseTicks(fuse);

        mobList.add(new MobModel(mob, p, true,ticksLiveMax));
    }

    public static void spawnVex(Player p, EntityType type, Component name, int ticksLiveMax, int armor, double attackDamage, int maxHealth, double knockbackResistance){
        Vex mob = (Vex) p.getWorld().spawnEntity(p.getLocation(), type);

        if (PlayerManager.getModelByPlayer(p).getTeam() == null){
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "team join " + p.getName() + "_p " + mob.getUniqueId());
        }else if (PlayerManager.getModelByPlayer(p).getTeam().equals(TeamType.RED)){
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "team join RED" + mob.getUniqueId());
        }else if (PlayerManager.getModelByPlayer(p).getTeam().equals(TeamType.BLUE)){
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "team join BLUE" + mob.getUniqueId());
        }

        mob.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(armor);
        mob.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(attackDamage);
        mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHealth);
        mob.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(knockbackResistance);

        mob.setHealth(maxHealth);
        mob.customName(name);
        mob.setCustomNameVisible(true);
        mob.setCanPickupItems(false);

        mobList.add(new MobModel(mob, p, true,ticksLiveMax));
    }

    public static int explodeC4(Player p){
        int count = 0;
        List<MobModel> tempMobList = new ArrayList<>();
        for (MobModel mob : mobList){
            if(mob.getSource().equals(p) && mob.getEntity().getType().equals(EntityType.SILVERFISH) && mob.getTicksLived() > 100){
                TNTPrimed tnt = (TNTPrimed) Bukkit.getWorld("world").spawnEntity(mob.getEntity().getLocation().add(0,1,0), EntityType.PRIMED_TNT);
                tnt.setFuseTicks(0);
                tnt.setSource(mob.getSource());
                tnt.setYield(3f);
                mob.getEntity().getWorld().spawnParticle(Particle.EXPLOSION_LARGE, mob.getEntity().getLocation(), 3);
                mob.getEntity().setHealth(0);
                tempMobList.add(mob);
                count++;
            }
        }
        for (MobModel mob : tempMobList){
            mobList.remove(mob);
        }
        return count;

    }
    public static void mobDeath(MobModel m){
        m.getEntity().setHealth(0);
        if (m.getEntity().getType().equals(EntityType.VEX)){
            TNTPrimed tnt = (TNTPrimed) Bukkit.getWorld("world").spawnEntity(m.getEntity().getLocation(), EntityType.PRIMED_TNT);
            tnt.setFuseTicks(0);
            tnt.setSource(m.getSource());
            tnt.setYield(3f);
            m.getEntity().getWorld().spawnParticle(Particle.EXPLOSION_LARGE, m.getEntity().getLocation(), 3);
        }
        deadMobs.add(m);
    }
    public static MobModel getModelByEntity(LivingEntity l){
        for (MobModel model : mobList){
            if (!model.getEntity().equals(l)) continue;
            return model;
        }
        return null;
    }

    public static boolean isInSameTeam(Player p, LivingEntity e){
        PlayerModel playerModel = PlayerManager.getModelByPlayer(p);
        MobModel mobModel = getModelByEntity(e);
        if (playerModel == null || mobModel == null || playerModel.getTeam() == null || PlayerManager.getModelByPlayer(mobModel.getSource()).getTeam() == null) return false;
        return playerModel.getTeam().equals(PlayerManager.getModelByPlayer(mobModel.getSource()).getTeam());
    }
}
