package siegward.kitpvp;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

import java.util.Objects;

public class listeners implements Listener {
    KitPvP plugin = KitPvP.getPlugin();
    @EventHandler
    public void OnJoin(PlayerJoinEvent e){

    }

    @EventHandler
    public void OnRespawn(PlayerRespawnEvent e){
        Player p = e.getPlayer();
        p.setHealth(p.getMaxHealth());
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
                //TODO вынести проверку и добавить ласт демедж
            }

            if (targetModel.getKit().equals(KitType.MERC) && target.isBlocking() && e.getFinalDamage() == 0) {
                KitManager.mercShieldBlocking(targetModel, e);
            }
            if (targetModel.getKit().equals(KitType.KNIGHT)) {
                KitManager.knightTakingDamage(targetModel);
            }
        }
        if(e.getDamager() instanceof Arrow){
            LivingEntity entity = (LivingEntity) e.getEntity();
            Projectile j = (Projectile) e.getDamager();
            //попытка сделать дробовик нормальным(имба обосраная)
            if (j.getMetadata("type").get(0).asString().equalsIgnoreCase("steampunk.shotgun")){
                e.setCancelled(true);
                j.remove();
                entity.setHealth(Math.max(0,entity.getHealth() - e.getDamage()));
                entity.setLastDamageCause(new EntityDamageEvent(Objects.requireNonNull(Bukkit.getPlayer(j.getMetadata("source").get(0).asString())), EntityDamageEvent.DamageCause.PROJECTILE, e.getDamage()));
            }
        }
    }

    @EventHandler
    public void OnDeath(PlayerDeathEvent e){
        Player p = e.getPlayer();
        PlayerModel playerModel = PlayerManager.getModelByPlayer(p);
        if (playerModel == null) return;
        //if (playerModel.getCombatMode() > 0){
        ItemStack soul = new ItemStack(Material.NETHER_STAR,1);
        ItemMeta soulItemMeta = soul.getItemMeta();
        soulItemMeta.displayName(Component.text("Потерянная душа").color(NamedTextColor.AQUA));
        p.getWorld().dropItem(p.getLocation(),soul);
        //}
        PlayerManager.removePlayer(p);


        if (p.getKiller() != null) {
            Player killer = p.getKiller();
            PlayerModel killerModel = PlayerManager.getModelByPlayer(killer);

            if (killerModel == null) return;
            if (killerModel.getKit().equals(KitType.ASSASSIN) || killerModel.getKit().equals(KitType.KNIGHT) || killerModel.getKit().equals(KitType.MERC)) {
                KitManager.meleeHealingAfterKill(killerModel);
            }
            if (killerModel.getKit().equals(KitType.KNIGHT) && killerModel.getResourceDifferencePerSecond() == -20) {
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
        Projectile j = e.getEntity();
        if (j instanceof Snowball){
            if (j.getMetadata("type").get(0).asString().equals("chaotic.snowball")) {

                TNTPrimed tnt = (TNTPrimed) Bukkit.getWorld("world").spawnEntity(j.getLocation(), EntityType.PRIMED_TNT);
                tnt.setFuseTicks(0);
                tnt.setSource(Bukkit.getPlayer(j.getMetadata("source").get(0).asString()));
                tnt.setYield(1.5f);
                j.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, j.getLocation(), 3);
            } else if (j.getMetadata("type").get(0).asString().equals("chaotic.cluster")) {
                Vector v = new Vector(0.1,0.145,0.1);
                for (int i = 0; i < 5; i++){
                    Snowball snowball = (Snowball) Bukkit.getWorld("world").spawnEntity(j.getLocation(), EntityType.SNOWBALL);
                    snowball.setVelocity(v.rotateAroundY(Math.toRadians(72)).add(new Vector(0,0.02,0)));
                    snowball.setInvulnerable(true);
                    snowball.setMetadata("type", new FixedMetadataValue(plugin, "chaotic.snowball"));
                    snowball.setMetadata("source", new FixedMetadataValue(plugin, j.getMetadata("source").get(0).asString()));
                }
            }
        }else if(j instanceof Egg){
            if (j.getMetadata("type").get(0).asString().equals("satanist.egg")) {

            }
            //TODO сделать зоной где будет выдаваться переделанный эффект иссушения наверное
        }
    }
    @EventHandler
    public void OnEntityDeath(EntityDeathEvent e){
        LivingEntity entity = e.getEntity();
        MobModel model = EntityManager.getModelByEntity(entity);
        if (model != null) {
            EntityManager.mobDeath(entity);
            //TODO как различать мобов если некоторые должны делать что то при смерти?(список тегов для мобМодели)
        }
    }
}
