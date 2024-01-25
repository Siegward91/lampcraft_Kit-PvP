package siegward.kitpvp;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class EntityManager {
    private static final KitPvP plugin = KitPvP.getPlugin();
    private static final List<MobModel> mobList = new ArrayList<>();


    public static void init(){
        BukkitRunnable mainHandler = new BukkitRunnable() {
            @Override
            public void run() {
                for (MobModel mob : mobList){
                    mob.incrementTicksLived(1);

                    if (mob.getTicksLiveMax() > 0 && mob.getTicksLived() > mob.getTicksLiveMax()){
                        LivingEntity l = mob.getEntity();
                        l.setHealth(l.getHealth() - (mob.getTicksLived() - mob.getTicksLiveMax())*0.1);
                    }

                    //частицы
                    if (mob.getEntity().getType().equals(EntityType.SILVERFISH)) {
                        if (mob.getTicksLived() <= 80) {
                            mob.getEntity().getWorld().spawnParticle(Particle.PORTAL, mob.getEntity().getLocation(), 1, 0, 0, 0, 0.8);
                        } else if (mob.getTicksLived() == 100) {
                            mob.getEntity().getWorld().spawnParticle(Particle.VILLAGER_HAPPY, mob.getEntity().getLocation(), 10, 0.2, 0.1, 0.2);
                        }
                    }

                }
            }
        };
        mainHandler.runTaskTimer(plugin,0,1);
    }

    public static void spawnNoAIMob(Player p, EntityType type, Component name, int ticksLiveMax){
        LivingEntity mob = (LivingEntity) p.getWorld().spawnEntity(p.getLocation(), type);
        mob.setAI(false);
        mob.setCollidable(false);
        mob.setSilent(true);
        mob.customName(name);
        mobList.add(new MobModel(mob, p, false,ticksLiveMax));
    }
    public static void spawnAliveMob(Player p, EntityType type, Component name, int ticksLiveMax, double speed, int armor, int maxHealth){
        LivingEntity mob = (LivingEntity) p.getWorld().spawnEntity(p.getLocation(), type);
        //TODO как сделать мобам скорость армор и тд
        mob.setMaxHealth(maxHealth);
        mob.customName(name);
        mobList.add(new MobModel(mob, p, true,ticksLiveMax));
    }
    public static void explodeC4(Player p){
        List<MobModel> tempMobList = new ArrayList<>();
        for (MobModel mob : mobList){
            if(mob.getSource().equals(p) && mob.getEntity().getType().equals(EntityType.SILVERFISH) && mob.getTicksLived() > 100){
                TNTPrimed tnt = (TNTPrimed) Bukkit.getWorld("world").spawnEntity(mob.getEntity().getLocation().add(0,1,0), EntityType.PRIMED_TNT);
                tnt.setFuseTicks(0);
                tnt.setSource(mob.getSource());
                tnt.setYield(2f);
                mob.getEntity().getWorld().spawnParticle(Particle.EXPLOSION_LARGE, mob.getEntity().getLocation(), 3);
                mob.getEntity().setHealth(0);
                tempMobList.add(mob);
            }
        }
        for (MobModel mob : tempMobList){
            mobList.remove(mob);
        }
    }
    public static void mobDeath(LivingEntity entity){
        mobList.remove(getModelByEntity(entity));
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
        if (playerModel == null || mobModel == null) return false;
        return playerModel.getTeam().equals(PlayerManager.getModelByPlayer(mobModel.getSource()).getTeam());
    }
}
class MobModel {
    private final LivingEntity entity;
    private final Player source;
    private final boolean alive;
    private int ticksLived;
    private int ticksLiveMax;

    public void incrementTicksLived(int speed){
        ticksLived += speed;
    }

    public MobModel(LivingEntity entity, Player source, boolean alive, int ticksLiveMax) {
        this.entity = entity;
        this.source = source;
        this.alive = alive;
        this.ticksLiveMax = ticksLiveMax;
        ticksLived = 0;
    }

    public int getTicksLived() {
        return ticksLived;
    }

    public void setTicksLived(int ticksLived) {
        this.ticksLived = ticksLived;
    }

    public int getTicksLiveMax() {
        return ticksLiveMax;
    }

    public void setTicksLiveMax(int ticksLiveMax) {
        this.ticksLiveMax = ticksLiveMax;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public Player getSource() {
        return source;
    }
    public boolean isAlive() {
        return alive;
    }
}
