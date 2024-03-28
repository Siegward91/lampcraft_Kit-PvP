package siegward.kitpvp.abilities;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import siegward.kitpvp.KitPvP;
import siegward.kitpvp.Managers.EntityManager;
import siegward.kitpvp.Managers.EventManager;
import siegward.kitpvp.Managers.PlayerManager;
import siegward.kitpvp.Managers.PlayerModel;
import siegward.kitpvp.utils.KitType;

import java.util.*;

public class KitManager {
    private static final KitPvP plugin = KitPvP.getPlugin();
    //воины
    public static void meleeHealingAfterKill(PlayerModel m) {
        Player p = m.getPlayer();
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
        Player p = m.getPlayer();
        Material item = p.getInventory().getItemInMainHand().getType();
        if (p.getCooldown(item) == 0){
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
    public static void knightTakingDamage(PlayerModel m, EntityDamageByEntityEvent e){
        Player p = m.getPlayer();
        if (m.getResourceCurrent() < m.getResourceMax() && m.getResourceDifferencePerSecond() == 0) {
            m.setResourceCurrent(Math.min(m.getResourceMax(), m.getResourceCurrent() + 100*(e.getDamage()/p.getMaxHealth())));
        }
        if (m.getResourceCurrent() == m.getResourceMax()){
            m.setResourceDifferencePerSecond(-20);
            p.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(1);
            p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, 1,false,false,true));
            p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 999999, 0,false,false,true));
            p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 999999, 1,false,false,true));

        }

    }
    public static void knightRageOnKill(PlayerModel m){
        if (m.getResourceDifferencePerSecond() == -20) {
            m.setResourceCurrent(m.getResourceMax());
        }
    }

    public static void assassinPearl(PlayerModel m){
        int cost = 60;
        int cooldown = 10;
        Player p = m.getPlayer();
        Material item = p.getInventory().getItemInMainHand().getType();
        if (p.getCooldown(item) == 0 && m.getResourceCurrent() >= cost){
            m.setResourceCurrent(m.getResourceCurrent() - cost);
            p.launchProjectile(EnderPearl.class, p.getEyeLocation().getDirection().multiply(1.5));
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
        int cost = 60;
        int cooldown = 10;
        Player p = m.getPlayer();
        Material item = p.getInventory().getItemInMainHand().getType();
        if (p.getCooldown(item) == 0 && m.getResourceCurrent() >= cost){
            m.setResourceCurrent(m.getResourceCurrent() - cost);

            Set<Entity> list = new HashSet<>();
            //область
            list.addAll(p.getEyeLocation().add(p.getEyeLocation().getDirection().multiply(2)).getNearbyEntities (2,2,2));
            list.remove(p);
            for (Entity entity: list){
                if ((entity instanceof Player && !(PlayerManager.isInSameTeam(p,(Player) entity))) || ((entity instanceof LivingEntity) && !(EntityManager.isInSameTeam(p,(LivingEntity) entity))) || !(entity instanceof ArmorStand)){
                    Vector v = p.getEyeLocation().getDirection();
                    double X1 = p.getEyeLocation().getDirection().getX();
                    double Z1 = p.getEyeLocation().getDirection().getZ();
                    double x2 = X1/Math.pow(X1*X1 + Z1*Z1, 0.5);
                    double z2 = Z1/Math.pow(X1*X1 + Z1*Z1, 0.5);
                    v.setY(0.2).setX(x2).setZ(z2);
                    entity.setVelocity(v.multiply(1.5));
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
                break;
            case 1:
                e.setCancelled(true);
                Random r = new Random();
                for (int i = 0;i <= 18; i ++){
                    Projectile j = p.launchProjectile(Arrow.class,e.getProjectile().getVelocity().multiply(0.3).rotateAroundX(r.nextDouble(-0.3,0.3)).rotateAroundY(r.nextDouble(-0.3,0.3)).rotateAroundZ(r.nextDouble(-0.3,0.3)));
                    //супермега идея, даже 2 варианта (не вызывать .damage или в момент попадания ставить .setMaximumNoDamageTicks на 0 а потом возвращать)
                    j.setShooter(p);
                    j.setMetadata("type", new FixedMetadataValue(plugin, "steampunk.shotgun"));
                    j.setShooter(p);
                }
                break;
        }
    }
    public static void steampunkRebuild(PlayerModel m){
        int cooldown = 40;
        Player p = m.getPlayer();
        Material item = p.getInventory().getItemInMainHand().getType();
        if (p.getCooldown(item) == 0){
            if (m.getResourceCurrent() == m.getResourceMax()){
                p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 1,false,false,true));
                p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 60, 2,false,false,true));
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
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(),"cmi kit steampunk.shotgun " + p.getName());
                        break;
                    case 1:
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(),"cmi kit steampunk.snipe " + p.getName());
                        break;
                }
            }
            p.setCooldown(item,cooldown);
        }
    }

    public static void robinBolt(PlayerModel m){
        int cost = 60;
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

            Set<LivingEntity> list = new HashSet<>();
            Vector v = p.getEyeLocation().getDirection().multiply(1.5);
            BukkitRunnable arrow = new BukkitRunnable() {
                Location l = p.getEyeLocation();
                int time = 0;
                @Override
                public void run() {
                    if (time < 120) {
                        //частицы
                        Bukkit.getWorld("world").spawnParticle(Particle.EXPLOSION_LARGE,l,1);


                        list.addAll(l.getNearbyLivingEntities(1.5));
                        list.remove(p);
                        for (LivingEntity i : list) {
                            //надеюсь правильно
                            if (i instanceof Player || i.getType().equals(EntityType.PLAYER)){
                                //в OnDamage уже проверка стоит
                                i.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING,60,0, false,false,true));
                                i.damage(5.0 + (45.0/120)*time);
                                i.setLastDamageCause(new EntityDamageEvent(p, EntityDamageEvent.DamageCause.PROJECTILE, 10));
                            }else {
                                if (!(EntityManager.isInSameTeam(p, i))){
                                    //с мобами работает (странный cmi)
                                    i.damage(10.0, p);
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
    public static void magesTakesManaAfterKill(PlayerModel m) {
        m.setResourceDifferencePerSecond(m.getResourceDifferencePerSecond()*10);
        m.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 60, 1,false,false,true));
        m.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 1,false,false,true));
        BukkitRunnable PassiveManaRegeneration = new BukkitRunnable() {
            int timer = 0;

            @Override
            public void run() {

                if (timer >= 60) {
                    m.setResourceDifferencePerSecond(m.getKit().getDefaultResourceDifferencePerSecond());
                    m.getPlayer().setFlying(false);
                    this.cancel();
                }
                timer++;
            }
        };
        PassiveManaRegeneration.runTaskTimer(plugin, 0, 1);

    }

    public static void chaoticSnowball(PlayerModel m){
        int cost = 65;
        Player p = m.getPlayer();
        Material item = p.getInventory().getItemInMainHand().getType();
        if (p.getCooldown(item) == 0 && m.getResourceCurrent() >= cost){
            m.setResourceCurrent(m.getResourceCurrent() - cost);
            Projectile j = p.launchProjectile(Snowball.class, p.getEyeLocation().getDirection().multiply(0.8));
            j.setMetadata("type", new FixedMetadataValue(plugin, "chaotic.snowball"));
            j.setShooter(p);
        }
    }
    public static void chaoticCluster(PlayerModel m){
        int cooldown = 20;
        int cost = 100;
        Player p = m.getPlayer();
        Material item = p.getInventory().getItemInMainHand().getType();
        if (p.getCooldown(item) == 0 && m.getResourceCurrent() >= cost){
            m.setResourceCurrent(m.getResourceCurrent() - cost);
            Projectile j = p.launchProjectile(Snowball.class, p.getEyeLocation().getDirection().multiply(0.5));
            j.setInvulnerable(true);
            j.setMetadata("type", new FixedMetadataValue(plugin, "chaotic.cluster"));
            j.setShooter(p);
            p.setCooldown(item,cooldown);
        }
    }
    public static void chaoticC4(PlayerModel m){
        int cooldown = 20;
        int cost = 50;
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
        Player p = m.getPlayer();
        Material item = p.getInventory().getItemInMainHand().getType();
        if (p.getCooldown(item) == 0){
            if (EntityManager.explodeC4(p) > 0) {
                p.setCooldown(item, cooldown);
            }
        }
    }

    public static void satanistSkull(PlayerModel m){
        int cost = 35;
        Player p = m.getPlayer();
        Material item = p.getInventory().getItemInMainHand().getType();
        if (p.getCooldown(item) == 0 && m.getResourceCurrent() >= cost){
            m.setResourceCurrent(m.getResourceCurrent() - cost);
            Projectile j = p.launchProjectile(WitherSkull.class, p.getEyeLocation().getDirection().multiply(2.5));
            j.setShooter(p);
        }
    }
    public static void satanistEgg(PlayerModel m){
        int cooldown = 10;
        int cost = 60;
        Player p = m.getPlayer();
        Material item = p.getInventory().getItemInMainHand().getType();
        if (p.getCooldown(item) == 0 && m.getResourceCurrent() >= cost){
            m.setResourceCurrent(m.getResourceCurrent() - cost);
            Projectile j = p.launchProjectile(Egg.class, p.getEyeLocation().getDirection().multiply(0.6));
            j.setMetadata("type", new FixedMetadataValue(plugin, "satanist.egg"));
            j.setShooter(p);
            p.setCooldown(item,cooldown);

        }
    }
    public static void satanistJump(PlayerModel m){
        int cooldown = 20*8;
        int cost = 20;
        Player p = m.getPlayer();
        Material item = p.getInventory().getItemInMainHand().getType();
        if (p.getCooldown(item) == 0 && m.getResourceCurrent() >= cost){
            m.setResourceCurrent(m.getResourceCurrent() - cost);
            p.setVelocity(new Vector(0,1.2,0));
            p.setCooldown(item,cooldown);
        }
    }

    public static void pyroFireball(PlayerModel m){
        int cooldown = 10;
        int cost = 50;
        Player p = m.getPlayer();
        Material item = p.getInventory().getItemInMainHand().getType();
        if (p.getCooldown(item) == 0 && m.getResourceCurrent() >= cost) {
            m.setResourceCurrent(m.getResourceCurrent() - cost);
            Projectile j = p.launchProjectile(Fireball.class, p.getEyeLocation().getDirection().multiply(0.8));
            j.setMetadata("type", new FixedMetadataValue(plugin, "pyro.fireball"));
            j.setShooter(p);
            p.setCooldown(item,cooldown);
        }
    }
    public static void pyroFirethrower(PlayerModel m){
        int cost = 10;
        Player p = m.getPlayer();
        Material item = p.getInventory().getItemInMainHand().getType();
        if (p.getCooldown(item) == 0 && m.getResourceCurrent() >= cost) {
            m.setResourceCurrent(m.getResourceCurrent() - cost);
            Random r = new Random();
            BukkitRunnable fireThrower = new BukkitRunnable() {
                int timer = 0;
                @Override
                public void run() {
                    if (timer >= 2) this.cancel();
                    Projectile j = p.launchProjectile(SmallFireball.class, p.getEyeLocation().add(0,-1,0).getDirection().multiply(1.5).rotateAroundX(r.nextDouble(-0.2, 0.2)).rotateAroundY(r.nextDouble(-0.1, 0.1)).rotateAroundZ(r.nextDouble(-0.2, 0.2)));
                    j.setMetadata("type", new FixedMetadataValue(plugin, "pyro.firethrower"));
                    j.setShooter(p);

                    BukkitRunnable fireRange = new BukkitRunnable() {
                        @Override
                        public void run() {
                            j.remove();
                        }
                    };
                    fireRange.runTaskLater(plugin,5);

                    timer++;

                }
            };
            fireThrower.runTaskTimer(plugin,0,2);
        }
    }
    public static void pyroNapalm(PlayerModel m){
        int cooldown = 20*18;
        int cost = 50;
        Player p = m.getPlayer();
        Material item = p.getInventory().getItemInMainHand().getType();
        if (p.getCooldown(item) == 0 && m.getResourceCurrent() >= cost){
            m.setResourceCurrent(m.getResourceCurrent() - cost);
            Projectile j = p.launchProjectile(Egg.class, p.getEyeLocation().getDirection().multiply(0.9));
            j.setMetadata("type", new FixedMetadataValue(plugin, "pyro.napalm"));
            j.setShooter(p);
            p.setCooldown(item,cooldown);

        }
    }

    //призыватели
    public static void creepermanSummon(PlayerModel m){
        int defaultCost = 65;
        Player p = m.getPlayer();
        int currentCost = (int) (defaultCost*m.getCostMultiplier());
        Material item = p.getInventory().getItemInMainHand().getType();
        if (p.getCooldown(item) == 0 && m.getResourceCurrent() >= currentCost) {
            m.setResourceCurrent(m.getResourceCurrent() - currentCost);
            EntityManager.spawnCreeper(p, EntityType.CREEPER, Component.text("Огурец " + p.getName()),20*30, 0.6, 2, 3, 10,20, 1.0);
            m.setCostMultiplier(m.getCostMultiplier() + 1);
            p.setLevel((int) (defaultCost*m.getCostMultiplier()));
            BukkitRunnable increasingCost = new BukkitRunnable() {
                final int startingCost = p.getLevel();
                int timer = 0;
                @Override
                public void run() {
                    if (startingCost != p.getLevel()){
                        this.cancel();
                    }else if(timer >= 100){
                        m.setCostMultiplier(1);
                        p.setLevel((int) (defaultCost*m.getCostMultiplier()));
                        this.cancel();
                    }
                    timer++;
                }
            };
            increasingCost.runTaskTimer(plugin,0,1);
        }
    }
    public static void creepermanExplosions(PlayerModel m){
        int cooldown = 20*18;
        int cost = 50;
        Player p = m.getPlayer();
        Material item = p.getInventory().getItemInMainHand().getType();
        if (p.getCooldown(item) == 0 && m.getResourceCurrent() >= cost){
            m.setResourceCurrent(m.getResourceCurrent() - cost);
            Particle.DustTransition smoke = new Particle.DustTransition(Color.fromRGB(50, 50, 50), Color.fromRGB(200, 200, 200), 1.0F);
            p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 140, 1, false, false, true));
            p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 140, 2, false, false, true));
            m.setCostMultiplier(100);
            BukkitRunnable effect = new BukkitRunnable() {
                int timer = 0;
                @Override
                public void run() {
                    if (p.isDead()) this.cancel();
                    Bukkit.getWorld("world").spawnParticle(Particle.DUST_COLOR_TRANSITION, p.getLocation().add(0,1,0), 3,0.2,0.3,0.2, smoke);
                    if (timer == 60 || timer == 80 || timer == 100 || timer == 120){
                        TNTPrimed tnt = (TNTPrimed) Bukkit.getWorld("world").spawnEntity(p.getLocation().add(0,1,0), EntityType.PRIMED_TNT);
                        tnt.setFuseTicks(0);
                        tnt.setSource(p);
                        tnt.setYield(2.5f);
                        p.setVelocity(p.getEyeLocation().getDirection().add(new Vector(0,0.2,0)).multiply(0.5));
                    }else if (timer > 130){
                        m.setCostMultiplier(1);
                        this.cancel();
                    }
                    timer++;
                }
            };
            effect.runTaskTimer(plugin,0,1);

            p.setCooldown(item,cooldown);

        }
    }

    public static void necromancerSummon(PlayerModel m){
        int defaultCost = 35;
        Player p = m.getPlayer();
        int currentCost = (int) (defaultCost*m.getCostMultiplier());
        Material item = p.getInventory().getItemInMainHand().getType();
        if (p.getCooldown(item) == 0 && m.getResourceCurrent() >= currentCost) {
            m.setResourceCurrent(m.getResourceCurrent() - currentCost);
            EntityManager.spawnSkeleton(p, EntityType.STRAY, Component.text("Стрелок " + p.getName()),20*40, 0.0, 2, 5, 20, 1.0);
            m.setCostMultiplier(m.getCostMultiplier() + 1);
            p.setLevel((int) (defaultCost*m.getCostMultiplier()));
            BukkitRunnable increasingCost = new BukkitRunnable() {
                final int startingCost = p.getLevel();
                int timer = 0;
                @Override
                public void run() {
                    if (startingCost != p.getLevel()){
                        this.cancel();
                    }else if(timer >= 200){
                        m.setCostMultiplier(1);
                        p.setLevel((int) (defaultCost*m.getCostMultiplier()));
                        this.cancel();
                    }
                    timer++;
                }
            };
            increasingCost.runTaskTimer(plugin,0,1);
        }
    }
    public static void necromancerPentagram(PlayerModel m){
        int cooldown = 20*30;
        int cost = 80;
        Player p = m.getPlayer();
        Material item = p.getInventory().getItemInMainHand().getType();
        if (p.getCooldown(item) == 0 && m.getResourceCurrent() >= cost) {
            m.setResourceCurrent(m.getResourceCurrent() - cost);

            Particle.DustTransition delayParticle = new Particle.DustTransition(Color.fromRGB(0, 0, 0), Color.fromRGB(0, 0, 0), 1.0F);
            Particle.DustTransition finalParticle = new Particle.DustTransition(Color.fromRGB(100, 15, 130), Color.fromRGB(0, 0, 0), 1.5F);

            Location location = p.getLocation().add(0,0.2,0);
            Vector v = p.getEyeLocation().getDirection();
            double X1 = p.getEyeLocation().getDirection().getX();
            double Z1 = p.getEyeLocation().getDirection().getZ();
            double x2 = X1/Math.pow(X1*X1 + Z1*Z1, 0.5);
            double z2 = Z1/Math.pow(X1*X1 + Z1*Z1, 0.5);
            v.setY(0).setX(x2).setZ(z2);
            BukkitRunnable pentagramDelay = new BukkitRunnable() {
                int timer = 0;
                @Override
                public void run() {
                    Set<LivingEntity> list = new HashSet<>();
                    if (timer < 40){
                        //частицы
                        for (int i = 0; i < (timer+1)*9; i+=3) {
                            Vector vec = v.clone().multiply(7).rotateAroundY(Math.toRadians(i));
                            Bukkit.getWorld("world").spawnParticle(Particle.DUST_COLOR_TRANSITION, location.clone().add(vec), 1,delayParticle);

                        }
                        for (int i = 0; i < (timer+1)*9; i+=4) {
                            Vector vec = v.clone().multiply(4).rotateAroundY(Math.toRadians(-i));
                            Bukkit.getWorld("world").spawnParticle(Particle.DUST_COLOR_TRANSITION, location.clone().add(vec), 1,delayParticle);

                        }
                    }else if (timer < 280){
                        //частицы
                        for (int i = 0; i < 360; i+=3) {
                            Vector vec = v.clone().multiply(7).rotateAroundY(Math.toRadians(i));
                            Bukkit.getWorld("world").spawnParticle(Particle.DUST_COLOR_TRANSITION, location.clone().add(vec), 1,finalParticle);
                        }
                        for (int i = 0; i < 360; i+=4) {
                            Vector vec = v.clone().multiply(4).rotateAroundY(Math.toRadians(-i));
                            Bukkit.getWorld("world").spawnParticle(Particle.DUST_COLOR_TRANSITION, location.clone().add(vec), 1,finalParticle);
                        }
                        Bukkit.getWorld("world").spawnParticle(Particle.SPELL_WITCH, location.clone(), 30, 3,0,3);

                        //действие
                        list.addAll(location.clone().getNearbyLivingEntities(6,2));
                        for (LivingEntity l : list){
                            if ((l instanceof Player && PlayerManager.getModelByPlayer((Player) l) == null) || (!(l instanceof Player) && EntityManager.getModelByEntity(l) == null)) continue;
                            else if ((l.equals(p)) || (l instanceof Player && PlayerManager.isInSameTeam(p,(Player) l)) || ((!(l instanceof Player)) && EntityManager.getModelByEntity(l).getSource().equals(p))){
                                l.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 40, 1,false,false,true));
                            }else {
                                l.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20, 3,false,false,true));
                                l.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 30, 0,false,false,true));
                            }
                        }
                    }else{
                        this.cancel();
                    }
                    timer++;
                }
            };
            pentagramDelay.runTaskTimer(plugin,0,1);

            p.setCooldown(item,cooldown);
        }

    }

    public static void undeadSummon(PlayerModel m){
        int defaultCost = 20;
        Player p = m.getPlayer();
        int currentCost = (int) (defaultCost*m.getCostMultiplier());
        Material item = p.getInventory().getItemInMainHand().getType();
        if (p.getCooldown(item) == 0 && m.getResourceCurrent() >= currentCost) {
            m.setResourceCurrent(m.getResourceCurrent() - currentCost);
            EntityManager.spawnZombie(p, EntityType.HUSK, Component.text("Зомби " + p.getName()),20*30, 0.3, 8, 2, 10.0,20, 0.2);
            m.setCostMultiplier(m.getCostMultiplier() + 1);
            p.setLevel((int) (defaultCost*m.getCostMultiplier()));
            BukkitRunnable increasingCost = new BukkitRunnable() {
                final int startingCost = p.getLevel();
                int timer = 0;
                @Override
                public void run() {
                    if (startingCost != p.getLevel()){
                        this.cancel();
                    }else if(timer >= 160){
                        m.setCostMultiplier(1);
                        p.setLevel((int) (defaultCost*m.getCostMultiplier()));
                        this.cancel();
                    }
                    timer++;
                }
            };
            increasingCost.runTaskTimer(plugin,0,1);
        }
    }
    public static void undeadDisaster(PlayerModel m){
        int cooldown = 20*20;
        int cost = 40;
        Player p = m.getPlayer();
        Material item = p.getInventory().getItemInMainHand().getType();
        if (p.getCooldown(item) == 0 && m.getResourceCurrent() >= cost) {
            m.setResourceCurrent(m.getResourceCurrent() - cost);

            Vector v = new Vector(4,0,0);

            p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 160, 0,false,false,true));
            p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 160, 0,false,false,true));
            BukkitRunnable effect = new BukkitRunnable() {
                final Set<LivingEntity> list = new HashSet<>();
                int timer = 0;
                @Override
                public void run() {
                    if (timer < 160){
                        Location location = p.getLocation();
                        //частицы
                        for (int i = 0; i < 360; i+=12) {
                            Vector vec = v.clone().rotateAroundY(Math.toRadians(i));
                            Bukkit.getWorld("world").spawnParticle(Particle.SPELL_MOB, location.clone().add(vec).add(0,0.8,0), 0, ((double) 187 / 255), ((double) 255 / 255), ((double) 0 / 255), 1);
                        }

                        //действие
                        list.addAll(location.clone().getNearbyLivingEntities(4,2));
                        list.remove(p);
                        for (LivingEntity l : list){
                            if ((l instanceof Player && PlayerManager.getModelByPlayer((Player) l) == null) || (!(l instanceof Player) && EntityManager.getModelByEntity(l) == null)) continue;
                            else if ((!(l instanceof Player)) && EntityManager.getModelByEntity(l).getSource().equals(p)){
                                l.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 40, 0,false,true,true));
                                l.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, 1,false,true,true));
                            }else if (l instanceof Player && !(PlayerManager.isInSameTeam(p,(Player) l))){
                                l.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20, 1,false,false,true));
                                l.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20, 0,false,false,true));
                            }
                        }
                    }else{
                        this.cancel();
                    }
                    timer++;
                }
            };
            effect.runTaskTimer(plugin,0,1);

            p.setCooldown(item,cooldown);
        }
    }

    public static void heal(PlayerModel m){
        int cooldown = 400;
        Player p = m.getPlayer();
        Material item = p.getInventory().getItemInMainHand().getType();
        if (p.getCooldown(item) == 0 && p.getHealth() < p.getMaxHealth()) {
            p.setHealth(p.getMaxHealth());
            p.setCooldown(item, cooldown);
        }
    }

    public static void soul(PlayerModel m){
        int index = 0;
        List<Integer> indexes = new ArrayList<Integer>();
        int count = 0;
        Player p = m.getPlayer();
        if (m.getKit().equals(KitType.NONE)){
            p.sendActionBar(Component.text("Без класса нельзя сдать Души)))").color(NamedTextColor.DARK_RED));
            return;
        }
        if (!p.getInventory().contains(Material.NETHER_STAR)) {
            p.sendActionBar(Component.text("У вас нет потерянных Душ!").color(NamedTextColor.RED));
            return;
        }
        for (ItemStack item : p.getInventory()){
            if (item == null) {
                index++;
                continue;
            }
            if (item.getType().equals(Material.NETHER_STAR) && item.getItemMeta().displayName().equals(Component.text("Потерянная Душа").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.AQUA))){
                indexes.add(index);
                count+= item.getAmount();

            }
            index++;
        }
        int record = EventManager.getRecord();
        if (record >= 0 && count <= record) {
            p.sendActionBar(Component.text("Нужно больше " + record + " душ, чтобы побить рекорд!").color(NamedTextColor.DARK_RED));
        }else {
            for (int i : indexes) {
                p.getInventory().clear(i);
            }
            if (record >= 0) {
                EventManager.setRecord(count, p);
            }
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cmi money give " + p.getName() + " " + count);
            for (Player i : Bukkit.getOnlinePlayers()) {
                if (10 <= count && count < 20)
                    i.showTitle(Title.title(Component.text(" "), Component.text(ChatColor.WHITE + p.getName() + " сжег " + ChatColor.YELLOW + count + ChatColor.WHITE + " Душ")));
                else if (20 <= count && count < 30)
                    i.showTitle(Title.title(Component.text(" "), Component.text(ChatColor.YELLOW + p.getName() + " сжег " + ChatColor.RED + count + ChatColor.YELLOW + " Душ!")));
                else if (30 <= count && count < 40) {
                    i.showTitle(Title.title(Component.text(" "), Component.text(ChatColor.RED + p.getName() + " сжег " + ChatColor.DARK_RED + ChatColor.BOLD + count + ChatColor.RED + " Душ!!!")));
                    i.playSound(i.getLocation(), Sound.ENTITY_WITHER_SPAWN, 2, 1);
                } else if (40 <= count && count < 50) {
                    i.showTitle(Title.title(Component.text(" "), Component.text(ChatColor.DARK_RED + "☠ " + ChatColor.RED + p.getName() + " сжег " + ChatColor.DARK_RED + ChatColor.BOLD + count + ChatColor.RED + " Душ!!!" + ChatColor.DARK_RED + " ☠")));
                    i.playSound(i.getLocation(), Sound.ENTITY_WITHER_SPAWN, 2, 1);
                } else if (count >= 50) {
                    i.showTitle(Title.title(Component.text(" "), Component.text("" + ChatColor.DARK_RED + "☠☠☠ " + ChatColor.BOLD + p.getName() + " СЖЕГ " + count + " ДУШ!!!" + ChatColor.DARK_RED + " ☠☠☠")));
                    i.playSound(i.getLocation(), Sound.ENTITY_ENDER_DRAGON_DEATH, 2, 1);
                }
            }

        }
    }
}
