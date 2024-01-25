package siegward.kitpvp;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

public class KitManager {
    private static final KitPvP plugin = KitPvP.getPlugin();
    //воины
    public static void meleeHealingAfterKill(PlayerModel m) {
        Player p = m.getPlayer();
        //TODO лучше делать подобные проверки? на этапе лисенера или после вызова метода?
        BukkitRunnable PassiveHealing = new BukkitRunnable() {
            int timer = 0;

            @Override
            public void run() {
                if (timer < 8) {
                    p.setHealth(Math.min(p.getMaxHealth(), p.getHealth() + p.getMaxHealth() / 16));
                } else {
                    this.cancel();
                }
                timer++;
            }
        };
        PassiveHealing.runTaskTimer(plugin, 0, 5);

    }

    public static void knightArmor(PlayerModel m){
        int cooldown = 20*12;
        int cost = 20;
        Player p = m.getPlayer();
        Material item = p.getInventory().getItemInMainHand().getType();
        if ((p.getCooldown(item) == 0 && m.getResourceCurrent() >= cost) || (p.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE))){
            if (!p.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
                m.setResourceCurrent(m.getResourceCurrent() - cost);;
            }
            double Before = p.getAbsorptionAmount();
            p.setAbsorptionAmount(Before + 12.0);
            BukkitRunnable timer = new BukkitRunnable() {
                int timer = 0;
                @Override
                public void run() {
                    if (p.getAbsorptionAmount() <= Before || timer >= 20*5){
                        if (timer >= 20*5){
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
    }
    public static void knightTakingDamage(PlayerModel m){
        Player p = m.getPlayer();
        if (m.getResourceCurrent() == m.getResourceMax()){
            m.setResourceDifferencePerSecond(-20);
            p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 250, 1,false,false,true));
            p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 250, 0,false,false,true));
            p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 250, 1,false,false,true));
        }

    }
    public static void knightRageOnKill(PlayerModel m){
        Player p = m.getPlayer();
        p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 250, 1,false,false,true));
        p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 250, 0,false,false,true));
        p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 250, 1,false,false,true));
        m.setResourceCurrent(m.getResourceMax());
    }

    public static void assassinPearl(PlayerModel m){
        int cost = 60;
        int cooldown = 10;
        Player p = m.getPlayer();
        Material item = p.getInventory().getItemInMainHand().getType();
        if (p.getCooldown(item) == 0 && m.getResourceCurrent() >= cost){
            m.setResourceCurrent(m.getResourceCurrent() - cost);
            p.launchProjectile(EnderPearl.class, p.getEyeLocation().getDirection().multiply(3));
            p.setCooldown(item,cooldown);

        }
    }

    public static void mercDash(PlayerModel m){
        int cost = 40;
        int cooldown = 20;
        Player p = m.getPlayer();
        Material item = p.getInventory().getItemInMainHand().getType();
        if (p.getCooldown(item) == 0 && m.getResourceCurrent() >= cost){
            m.setResourceCurrent(m.getResourceCurrent() - cost);
            p.setVelocity(p.getEyeLocation().getDirection().add(p.getEyeLocation().getDirection().setX(0).setY(0.2).setZ(0)).multiply(1.5));
            p.setCooldown(item,cooldown);
        }
    }
    public static void mercShieldBlocking(PlayerModel m, EntityDamageByEntityEvent e){
        Player p = m.getPlayer();
        if (!p.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)){
            p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100, 0,false,false,true));
            p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 0,false,false,true));
        }else if (p.getPotionEffect(PotionEffectType.INCREASE_DAMAGE).getAmplifier() == 0){
            p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100, 1,false,false,true));
            p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 0,false,false,true));
            p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 0,false,false,true));
        }else if (p.getPotionEffect(PotionEffectType.INCREASE_DAMAGE).getAmplifier() == 1){
            p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, p.getPotionEffect(PotionEffectType.INCREASE_DAMAGE).getDuration() + 60, 1,false,false,true));
            p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, p.getPotionEffect(PotionEffectType.DAMAGE_RESISTANCE).getDuration() + 60, 0,false,false,true));
            p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, p.getPotionEffect(PotionEffectType.SPEED).getDuration() + 60, 0,false,false,true));
        }

    }


    //стрелки
    public static void royalFirework(PlayerModel m){
        int cost = 40;
        int cooldown = 10;
        Player p = m.getPlayer();
        Material item = p.getInventory().getItemInMainHand().getType();
        if (p.getCooldown(item) == 0 && m.getResourceCurrent() >= cost){
            m.setResourceCurrent(m.getResourceCurrent() - cost);

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
    }

    public static void steampunkShoot(PlayerModel m, EntityShootBowEvent e){
        Player p = m.getPlayer();
        int mode = e.getBow().getEnchantmentLevel(Enchantment.QUICK_CHARGE);
        switch (mode){
            case 0:
                e.getProjectile().setVelocity(e.getProjectile().getVelocity().multiply(2));
                e.getProjectile().setMetadata("type", new FixedMetadataValue(plugin, "steampunk.snipe"));
                e.getProjectile().setMetadata("source", new FixedMetadataValue(plugin, p.getName()));
                break;
            case 1:
                e.setCancelled(true);
                Random r = new Random();
                for (int i = 0;i <= 18; i ++){
                    Projectile j = p.launchProjectile(Arrow.class,e.getProjectile().getVelocity().multiply(0.5).rotateAroundX(r.nextDouble(-0.3,0.3)).rotateAroundY(r.nextDouble(-0.3,0.3)).rotateAroundZ(r.nextDouble(-0.3,0.3)));
                    //TODO супермега идея, даже 2 варианта (не вызывать .damage или в момент попадания ставить .setMaximumNoDamageTicks на 0 а потом возвращать)
                    j.setShooter(p);
                    j.setMetadata("type", new FixedMetadataValue(plugin, "steampunk.shotgun"));
                    j.setMetadata("source", new FixedMetadataValue(plugin, p.getName()));
                }
                break;
        }
    }
    public static void steampunkRebuild(PlayerModel m){
        int cooldown = 20;
        Player p = m.getPlayer();
        Material item = p.getInventory().getItemInMainHand().getType();
        if (p.getCooldown(item) == 0){
            if (m.getResourceCurrent() == m.getResourceMax()){
                p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, 1,false,false,true));
                p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 40, 2,false,false,true));
            }
            m.setResourceCurrent(0);
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
    }

    public static void robinBolt(PlayerModel m){
        int cost = 75;
        int cooldown = 20;
        Player p = m.getPlayer();
        Material item = p.getInventory().getItemInMainHand().getType();
        if (p.getCooldown(item) == 0 && m.getResourceCurrent() >= cost && (p.getInventory().contains(Material.ARROW) || p.getInventory().contains(Material.TIPPED_ARROW))){
            if (p.getInventory().contains(Material.ARROW)){
                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(),"clear " + p.getName() + " minecraft:arrow 1");
            }else{
                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(),"clear " + p.getName() + " minecraft:tipped_arrow 1");
            }
            m.setResourceCurrent(m.getResourceCurrent() - cost);

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
                            LivingEntity enemy = (LivingEntity) i;
                            //надеюсь правильно
                            if (enemy instanceof Player){
                                Player enemyPlayer = (Player) enemy;
                                if (PlayerManager.isInSameTeam(p,enemyPlayer)) {
                                    enemy.damage(10.0, p);
                                }
                            }else {
                                if (EntityManager.isInSameTeam(p, enemy)){
                                    enemy.damage(10.0, p);
                                }
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
    }
    //маги
    public static void chaoticSnowball(PlayerModel m){
        int cost = 60;
        Player p = m.getPlayer();
        Material item = p.getInventory().getItemInMainHand().getType();
        if (p.getCooldown(item) == 0 && m.getResourceCurrent() >= cost){
            m.setResourceCurrent(m.getResourceCurrent() - cost);
            Projectile j = p.launchProjectile(Snowball.class, p.getEyeLocation().getDirection());
            j.setMetadata("type", new FixedMetadataValue(plugin, "chaotic.snowball"));
            j.setMetadata("source", new FixedMetadataValue(plugin, p.getName()));
        }
    }
    public static void chaoticCluster(PlayerModel m){
        int cooldown = 20;
        int cost = 120;
        Player p = m.getPlayer();
        Material item = p.getInventory().getItemInMainHand().getType();
        if (p.getCooldown(item) == 0 && m.getResourceCurrent() >= cost){
            m.setResourceCurrent(m.getResourceCurrent() - cost);
            Projectile j = p.launchProjectile(Snowball.class, p.getEyeLocation().getDirection());
            j.setMetadata("type", new FixedMetadataValue(plugin, "chaotic.cluster"));
            j.setMetadata("source", new FixedMetadataValue(plugin, p.getName()));
            p.setCooldown(item,cooldown);
        }
    }
    public static void chaoticC4(PlayerModel m){
        int cooldown = 20;
        int cost = 100;
        Player p = m.getPlayer();
        Material item = p.getInventory().getItemInMainHand().getType();
        if (p.getCooldown(item) == 0 && m.getResourceCurrent() >= cost){
            m.setResourceCurrent(m.getResourceCurrent() - cost);
            EntityManager.spawnNoAIMob(p, EntityType.SILVERFISH, Component.text("C4 " + p.getName()).color(NamedTextColor.YELLOW), 20*60*10);
            p.setCooldown(item,cooldown);
        }
    }
    public static void chaoticExplodeC4(PlayerModel m){
        int cooldown = 20*5;
        int cost = 10;
        Player p = m.getPlayer();
        Material item = p.getInventory().getItemInMainHand().getType();
        if (p.getCooldown(item) == 0 && m.getResourceCurrent() >= cost){
            m.setResourceCurrent(m.getResourceCurrent() - cost);
            EntityManager.explodeC4(p);
            p.setCooldown(item,cooldown);
        }
    }

    public static void satanistSkull(PlayerModel m){
        int cost = 40;
        Player p = m.getPlayer();
        Material item = p.getInventory().getItemInMainHand().getType();
        if (p.getCooldown(item) == 0 && m.getResourceCurrent() >= cost){
            m.setResourceCurrent(m.getResourceCurrent() - cost);
            p.launchProjectile(WitherSkull.class, p.getEyeLocation().getDirection());
        }
    }
    public static void satanistEgg(PlayerModel m){
        int cooldown = 10;
        int cost = 70;
        Player p = m.getPlayer();
        Material item = p.getInventory().getItemInMainHand().getType();
        if (p.getCooldown(item) == 0 && m.getResourceCurrent() >= cost){
            m.setResourceCurrent(m.getResourceCurrent() - cost);
            Projectile j = p.launchProjectile(Egg.class, p.getEyeLocation().getDirection().multiply(0.5));
            j.setMetadata("type", new FixedMetadataValue(plugin, "satanist.egg"));
            j.setMetadata("source", new FixedMetadataValue(plugin, p.getName()));
            p.setCooldown(item,cooldown);

        }
    }

    //призыватели
    public static void creepermanSummon(PlayerModel m){
        //TODO еще разок обсудить мобов у призывателей и цену призыва
        int cost = 90;
        Player p = m.getPlayer();
        Material item = p.getInventory().getItemInMainHand().getType();
        if (p.getCooldown(item) == 0 && m.getResourceCurrent() >= cost) {
            m.setResourceCurrent(m.getResourceCurrent() - cost);
            //EntityManager.spawnAliveMob(p, EntityType.CREEPER, Component.text("Огурец " + p.getName()),20*30, хз, хз, 20);
        }
    }
    public static void creepermanBombJump(PlayerModel m){

    }

    public static void necromancerSummon(PlayerModel m){
        int cost = 80;
        Player p = m.getPlayer();
        Material item = p.getInventory().getItemInMainHand().getType();
        if (p.getCooldown(item) == 0 && m.getResourceCurrent() >= cost) {
            m.setResourceCurrent(m.getResourceCurrent() - cost);
            //EntityManager.spawnAliveMob(p, EntityType.STRAY, Component.text("Огурец " + p.getName()),20*30, хз, хз, 20);
        }
    }
    public static void necromancerPentagram(PlayerModel m){

    }

    public static void undeadSummon(PlayerModel m){
        int cost = 60;
        Player p = m.getPlayer();
        Material item = p.getInventory().getItemInMainHand().getType();
        if (p.getCooldown(item) == 0 && m.getResourceCurrent() >= cost) {
            m.setResourceCurrent(m.getResourceCurrent() - cost);
            //EntityManager.spawnAliveMob(p, EntityType.HUSK, Component.text("Огурец " + p.getName()),20*30, хз, хз, 20);
        }
    }
    public static void undeadDisaster(PlayerModel m){

    }
    //TODO дописать оставшиеся скилы
}
