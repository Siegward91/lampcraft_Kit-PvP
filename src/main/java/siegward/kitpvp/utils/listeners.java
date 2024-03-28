package siegward.kitpvp.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import siegward.kitpvp.KitPvP;
import siegward.kitpvp.Managers.*;
import siegward.kitpvp.abilities.KitManager;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class listeners implements Listener {
    KitPvP plugin = KitPvP.getPlugin();
    @EventHandler
    public void OnJoin(PlayerJoinEvent e){
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "team add " + e.getPlayer().getName() + "_p");
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "team modify " + e.getPlayer().getName() + "_p friendlyFire false");
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "team modify " + e.getPlayer().getName() + "_p collisionRule never");
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "team join " + e.getPlayer().getName() + "_p " + e.getPlayer().getName());
        PlayerManager.addPlayer(e.getPlayer(), KitType.NONE);
    }

    @EventHandler
    public void OnLeave(PlayerQuitEvent e){
        PlayerManager.removePlayer(e.getPlayer());
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "team remove " + e.getPlayer().getName() + "_p");

    }

    @EventHandler
    public void OnRespawn(PlayerRespawnEvent e){
        Player p = e.getPlayer();
        PlayerManager.removePlayer(p);
        p.setHealth(p.getMaxHealth());
        PlayerManager.addPlayer(e.getPlayer(),KitType.NONE);
    }

    @EventHandler
    public void OnDamage(EntityDamageByEntityEvent e){
        if (e.getEntity() instanceof Player){
            Player target = (Player) e.getEntity();
            PlayerModel targetModel = PlayerManager.getModelByPlayer(target);
            if (targetModel == null) return;
            if (e.getDamager() instanceof Player){
                Player damager = (Player) e.getDamager();
                PlayerModel damagerModel = PlayerManager.getModelByPlayer(damager);
                if (damagerModel == null) return;
                if (PlayerManager.isInSameTeam(damager,target)){
                    e.setCancelled(true);
                    return;
                }
                //TODO сделать комбат мод ну или забить
            }

            if (e.getDamager().getType().equals(EntityType.PRIMED_TNT)){
                TNTPrimed tnt = (TNTPrimed) e.getDamager();
                if (tnt.getSource().equals(target) && targetModel.getKit().equals(KitType.CREEPERMAN)){
                    e.setCancelled(true);
                }
            }

            if (targetModel.getKit().equals(KitType.MERC) && target.isBlocking() && e.getFinalDamage() == 0) {
                KitManager.mercShieldBlocking(targetModel, e);
            }
            if (targetModel.getKit().equals(KitType.KNIGHT)) {
                KitManager.knightTakingDamage(targetModel, e);
            }
        }else if (e.getEntity() instanceof Fireball){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void OnDeath(PlayerDeathEvent e){
        Player p = e.getPlayer();
        PlayerModel playerModel = PlayerManager.getModelByPlayer(p);
        if (playerModel == null) return;

        int count = 0;
        for (ItemStack item : p.getInventory()){
            if (item == null) continue;
            if (item.getType().equals(Material.NETHER_STAR) && item.getItemMeta().displayName().equals(Component.text("Потерянная Душа").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.AQUA))){
                count += item.getAmount();
            }
        }
        if (Bukkit.getOnlinePlayers().size() > 1) {
            ItemStack soul = new ItemStack(Material.NETHER_STAR, count + 1);
            ItemMeta soulItemMeta = soul.getItemMeta();
            soulItemMeta.displayName(Component.text("Потерянная Душа").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.AQUA));
            soul.setItemMeta(soulItemMeta);
            p.getWorld().dropItem(p.getLocation(), soul);

        }
        PlayerManager.removePlayer(p);

        if (playerModel.getKit().equals(KitType.CREEPERMAN) || playerModel.getKit().equals(KitType.NECROMANCER) || playerModel.getKit().equals(KitType.UNDEAD)){
            EntityManager.spawnVex(playerModel.getPlayer(), EntityType.VEX, Component.text("Дух " + playerModel.getPlayer().getName()), 20*30,4,10,10,1);
        }

        if (p.getKiller() != null) {
            Player killer = p.getKiller();
            PlayerModel killerModel = PlayerManager.getModelByPlayer(killer);

            if (killerModel == null) return;
            if (killerModel.getKit().equals(KitType.ASSASSIN) || killerModel.getKit().equals(KitType.KNIGHT) || killerModel.getKit().equals(KitType.MERC)) {
                KitManager.meleeHealingAfterKill(killerModel);
            }
            if (killerModel.getKit().equals(KitType.CHAOTIC) || killerModel.getKit().equals(KitType.PYRO) || killerModel.getKit().equals(KitType.SATANIST)) {
                KitManager.magesTakesManaAfterKill(killerModel);
            }
            if (killerModel.getKit().equals(KitType.KNIGHT)) {
                KitManager.knightRageOnKill(killerModel);
            }
        }
    }
    @EventHandler
    public void OnShoot(EntityShootBowEvent e){
        if (!(e.getEntity() instanceof Player)) return;
        PlayerModel model = PlayerManager.getModelByPlayer((Player) e.getEntity());
        if (model == null) return;
        if (model.getKit().equals(KitType.STEAMPUNK)) {
            KitManager.steampunkShoot(model, e);
        }
    }

    @EventHandler
    public void OnProjHit(ProjectileHitEvent e){
        //damage = damage * (1 - (Math.min(20, Math.max(def/5, def - (4*damage)/8)))/25)
        Projectile j = e.getEntity();
        if (j instanceof Snowball){
            if (j.getMetadata("type").get(0).asString().equals("chaotic.snowball")) {

                TNTPrimed tnt = (TNTPrimed) Bukkit.getWorld("world").spawnEntity(j.getLocation(), EntityType.PRIMED_TNT);
                tnt.setFuseTicks(1);
                tnt.setYield(2f);
                tnt.setSource((Entity) j.getShooter());
                j.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, j.getLocation(), 3);
            } else if (j.getMetadata("type").get(0).asString().equals("chaotic.cluster")) {
                Vector v = new Vector(0.1,0.145,0.1);
                for (int i = 0; i < 5; i++){
                    Snowball snowball = (Snowball) Bukkit.getWorld("world").spawnEntity(j.getLocation(), EntityType.SNOWBALL);
                    snowball.setVelocity(v.rotateAroundY(Math.toRadians(72)).add(new Vector(0,0.02,0)));
                    snowball.setInvulnerable(true);
                    snowball.setMetadata("type", new FixedMetadataValue(plugin, "chaotic.snowball"));
                    snowball.setShooter(j.getShooter());
                }
            }
        }else if(j instanceof Egg){
            if (j.getMetadata("type").get(0).asString().equals("satanist.egg")) {
                e.setCancelled(true);
                Location location = e.getEntity().getLocation();
                BukkitRunnable areaEffect = new BukkitRunnable() {
                    int timer = 0;
                    @Override
                    public void run() {
                        if (timer >= 80) this.cancel();
                        Set<LivingEntity> list = new HashSet<>();
                        list.addAll(location.getNearbyLivingEntities(2.5,1));
                        Bukkit.getWorld("world").spawnParticle(Particle.SQUID_INK,location,20,1,0.2, 1,0);
                        Player p = (Player) j.getShooter();
                        for (LivingEntity l : list) {
                            if (!l.isDead()) {
                                l.setHealth(Math.max(0, l.getHealth() - ((double) 6 / 20)));
                                l.setLastDamageCause(new EntityDamageEvent(Objects.requireNonNull(p), EntityDamageEvent.DamageCause.POISON, (double) 6 / 20));

                            }
                        }
                        timer++;
                    }
                };
                areaEffect.runTaskTimer(plugin,0,1);
            }else if (j.getMetadata("type").get(0).asString().equals("pyro.napalm")) {
                e.setCancelled(true);
                Location location = e.getEntity().getLocation();
                BukkitRunnable areaEffect = new BukkitRunnable() {
                    int timer = 0;
                    @Override
                    public void run() {
                        if (timer >= 140) this.cancel();
                        Set<LivingEntity> list = new HashSet<>();
                        list.addAll(location.getNearbyLivingEntities(4,1));
                        Bukkit.getWorld("world").spawnParticle(Particle.FLAME,location,50,2.5,0.3, 2.5,0.1);
                        Player p = (Player) j.getShooter();
                        for (LivingEntity l : list) {
                            if (l.equals(p)){
                                l.setHealth(Math.min(l.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue(), l.getHealth() + (double) 2/20));
                                l.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 100, 0,false,false,true));
                            }else if (l instanceof Player && !PlayerManager.isInSameTeam(p, (Player) l)){
                                if (!l.isDead()) {
                                    l.setHealth(Math.max(0, l.getHealth() - ((double) 3 / 20)));
                                    l.setLastDamageCause(new EntityDamageEvent(Objects.requireNonNull(p), EntityDamageEvent.DamageCause.MAGIC, ((double) 3 / 20)));
                                    if (timer%20 == 0){
                                        l.setFireTicks(100);
                                    }
                                }
                            }else if (!(l instanceof Player) && !EntityManager.isInSameTeam(p, l)){
                                if (!l.isDead()) {
                                    l.setHealth(Math.max(0, l.getHealth() - ((double) 3 / 20)));
                                    l.setLastDamageCause(new EntityDamageEvent(Objects.requireNonNull(p), EntityDamageEvent.DamageCause.MAGIC, ((double) 3 / 20)));
                                    if (timer%20 == 0){
                                        l.setFireTicks(100);
                                    }
                                }
                            }
                        }
                        timer++;
                    }
                };
                areaEffect.runTaskTimer(plugin,0,1);
            }
            //TODO менеджер еффектов и снарядов?
        }else if (j instanceof Arrow){
            if (!j.getMetadata("type").isEmpty() && j.getMetadata("type").get(0).asString().equalsIgnoreCase("steampunk.shotgun")) {
                if (e.getHitEntity() != null) {
                    LivingEntity entity = (LivingEntity) e.getHitEntity();
                    //попытка сделать дробовик нормальным(имба обосраная)

                    e.setCancelled(true);
                    double trueDamage = 2;
                    double damage = trueDamage * (1 - (Math.min(20, Math.max( entity.getAttribute(Attribute.GENERIC_ARMOR).getValue() / 5, entity.getAttribute(Attribute.GENERIC_ARMOR).getValue() - (4*trueDamage)/8)))/25);
                    entity.setHealth(Math.max(0, entity.getHealth() - damage));
                    entity.setLastDamageCause(new EntityDamageEvent((Player) j.getShooter(), EntityDamageEvent.DamageCause.PROJECTILE, 2));

                }
                if (e.getHitBlock() != null){
                    e.getEntity().remove();
                }
            }
        }else if (j instanceof SmallFireball){
            if (j.getMetadata("type").get(0).asString().equals("pyro.firethrower")) {
                if (e.getHitEntity() != null) {
                    LivingEntity entity = (LivingEntity) e.getHitEntity();
                    Player p = (Player) j.getShooter();
                    e.setCancelled(true);
                    if ((entity instanceof Player && !PlayerManager.isInSameTeam(p, (Player) entity))) {
                        entity.setHealth(Math.max(0, entity.getHealth() - 1));
                        entity.setLastDamageCause(new EntityDamageEvent(Objects.requireNonNull(p), EntityDamageEvent.DamageCause.PROJECTILE, 4));
                        entity.setFireTicks(40);
                    }else if (!(entity instanceof Player) && !EntityManager.isInSameTeam(p, entity)) {
                        entity.setHealth(Math.max(0, entity.getHealth() - 1));
                        entity.setLastDamageCause(new EntityDamageEvent(Objects.requireNonNull(p), EntityDamageEvent.DamageCause.PROJECTILE, 4));
                        entity.setFireTicks(40);
                    }
                }
            }

        }else if (j instanceof Fireball && !(j instanceof WitherSkull)){
            if (j.getMetadata("type").get(0).asString().equalsIgnoreCase("pyro.fireball")) {
                Set<LivingEntity> list = new HashSet<>();
                list.addAll(e.getEntity().getLocation().getNearbyLivingEntities( 3));
                list.remove((Player) j.getShooter());
                for (LivingEntity l : list) {
                    if (l.getFireTicks() < 1) {
                        l.setHealth(Math.max(0, l.getHealth() - 6));
                        l.setLastDamageCause(new EntityDamageEvent((Player) j.getShooter(), EntityDamageEvent.DamageCause.PROJECTILE, 4));
                    } else {
                        l.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 1, false, false, true));
                    }
                    l.setFireTicks(120);
                }
            }
        }
    }
    
    @EventHandler
    public void OnEntityDeath(EntityDeathEvent e){
        LivingEntity entity = e.getEntity();
        if (!(e.getEntity() instanceof Player)) {
            MobModel model = EntityManager.getModelByEntity(entity);
            if (model != null) {
                EntityManager.mobDeath(model);
            }
        }
    }

    @EventHandler
    public void OnEntitySpawn(EntitySpawnEvent e){
        if (e.getEntity().getType().equals(EntityType.CHICKEN)){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void OnPlayerFlight(PlayerToggleFlightEvent e){
        Player p = e.getPlayer();
        PlayerModel m = PlayerManager.getModelByPlayer(p);
        if (m != null && m.getKit().equals(KitType.SATANIST) && !p.getGameMode().equals(GameMode.CREATIVE)){
            if (m.getResourceCurrent() < (double) m.getResourceMax() /10) {
                e.setCancelled(true);
            }else if (!p.isFlying()){
                m.setResourceDifferencePerSecond(-20);
                p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 9999999, 1,false,false,true));
            }else{
                m.setResourceDifferencePerSecond(m.getKit().getDefaultResourceDifferencePerSecond());
                p.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
            }
        }
    }
    @EventHandler
    public void OnBlockExplode(EntityExplodeEvent e) {
        e.blockList().clear();
    }
}
