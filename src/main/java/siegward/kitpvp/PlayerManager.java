package siegward.kitpvp;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.ArrayList;
import java.util.List;

public class PlayerManager {
    private static final KitPvP plugin = KitPvP.getPlugin();
    private static final List<PlayerModel> playerList = new ArrayList<>();
    private static final List<PlayerModel> playersToRemove = new ArrayList<>();


    public static void init(){
        Particle.DustTransition rageParticle = new Particle.DustTransition(Color.fromRGB(250, 60, 60), Color.fromRGB(140, 20, 20), 1.5F);

        BukkitRunnable mainHandler = new BukkitRunnable() {
            @Override
            public void run() {
                for (PlayerModel model : playerList){
                    switch (model.getKit()){
                        case KNIGHT:
                            if (model.getResourceDifferencePerSecond() < 0){
                                Bukkit.getWorld("world").spawnParticle(Particle.DUST_COLOR_TRANSITION, model.getPlayer().getLocation().add(0,0.6,0), 5,0.3,0.2,0.3, rageParticle);
                            }
                            break;

                        case MERC:
                            if (model.getPlayer().isBlocking()){
                                if (model.getResourceCurrent() > 0){
                                    //убавление ресурса пока удерживается блок
                                    model.setResourceDifferencePerSecond(-16);
                                }else{
                                    //кд щита
                                    model.getPlayer().setCooldown(Material.SHIELD, 60);
                                    ItemStack itemStack = model.getPlayer().getInventory().getItemInOffHand();
                                    ItemMeta itemMeta = model.getPlayer().getInventory().getItemInOffHand().getItemMeta();
                                    itemMeta.setUnbreakable(true);
                                    itemStack.setItemMeta(itemMeta);
                                    model.getPlayer().getInventory().setItemInOffHand(new ItemStack(Material.AIR, 1));
                                    BukkitRunnable delay = new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            model.getPlayer().getInventory().setItemInOffHand(itemStack);
                                            this.cancel();

                                        }
                                    };
                                    delay.runTaskTimer(plugin, 1, 1);
                                }
                            }else {
                                model.setResourceDifferencePerSecond(model.getKit().getDefaultResourceDifferencePerSecond());
                            }
                            break;
                    }
                    //стандартный реген ресурса
                    if (model.getResourceCurrent() == 0 && model.getResourceDifferencePerSecond() < 0) {
                        model.setResourceDifferencePerSecond(model.getKit().getDefaultResourceDifferencePerSecond());
                        switch (model.getKit()){
                            case SATANIST:
                                model.getPlayer().setFlying(false);
                                model.getPlayer().removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                                break;
                            case KNIGHT:
                                model.getPlayer().removePotionEffect(PotionEffectType.SPEED);
                                model.getPlayer().removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                                model.getPlayer().removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                                model.getPlayer().getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(0);
                                break;
                        }
                    }
                    model.setResourceCurrent(Math.max(0, Math.min(model.getResourceMax(), model.getResourceCurrent() + model.getResourceDifferencePerSecond()/20)));
                }
                for (PlayerModel model : playersToRemove){
                    playerList.remove(model);
                }
            }
        };
        mainHandler.runTaskTimer(plugin,0,1);
    }

    public static void addPlayer(Player p, KitType kit){
        if (getModelByPlayer(p) != null) removePlayer(p);
        playerList.add(new PlayerModel(p, kit.getDefaultStartingResource(), kit.getDefaultResourceDifferencePerSecond(), kit.getDefaultMaxResource(), kit));

    }

    public static void removePlayer(Player p){
        if (getModelByPlayer(p) != null) {
            playersToRemove.add(getModelByPlayer(p));
        }
        p.getInventory().clear();
        p.removePotionEffect(PotionEffectType.JUMP);
        p.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(0);
        p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
        p.setWalkSpeed(0.2f);
        p.setFlySpeed(0.1f);
        p.setLevel(0);
        p.setExp(0);
        p.setFlying(false);
        p.setAllowFlight(false);
    }

    public static PlayerModel getModelByPlayer(Player p){
        for (PlayerModel model : playerList){
            if (!model.getPlayer().equals(p)) continue;
            return model;
        }
        return null;
    }

    public static void setPlayerTeam(Player p, TeamType team){
        PlayerModel model = getModelByPlayer(p);
        if (model != null) model.setTeam(team);
    }

    public static boolean isInSameTeam(Player damager, Player target){
        PlayerModel damagerModel = getModelByPlayer(damager);
        PlayerModel targetModel = getModelByPlayer(target);
        if (damagerModel == null || targetModel == null || damagerModel.getTeam() == null || targetModel.getTeam() == null) return false;
        return damagerModel.getTeam().equals(targetModel.getTeam());
    }


}
class PlayerModel{
    private final Player player;
    private double resourceCurrent;
    private double resourceDifferencePerSecond;
    private int resourceMax;
    private KitType kit;
    private TeamType team;
    private int combatMode;
    private double costMultiplier;

    public PlayerModel(Player player, double resourceCurrent, double resourceDifferencePerSecond, int resourceMax, KitType kit) {
        this.player = player;
        this.resourceCurrent = resourceCurrent;
        this.resourceDifferencePerSecond = resourceDifferencePerSecond;
        this.resourceMax = resourceMax;
        this.kit = kit;
        team = null;
        costMultiplier = 1;
        combatMode = 0;
    }

    public double getCostMultiplier() {
        return costMultiplier;
    }

    public void setCostMultiplier(double costMultiplier) {
        this.costMultiplier = costMultiplier;
    }

    public int getCombatMode() {
        return combatMode;
    }
    public void setCombatMode(int combatMode) {
        this.combatMode = combatMode;
    }

    public double getResourceDifferencePerSecond() {
        return resourceDifferencePerSecond;
    }

    public void setResourceDifferencePerSecond(double resourceDifferencePerSecond) {
        this.resourceDifferencePerSecond = resourceDifferencePerSecond;
    }

    public TeamType getTeam() {
        return team;
    }

    public void setTeam(TeamType team) {
        this.team = team;
    }

    public Player getPlayer() {
        return player;
    }

    public double getResourceCurrent() {
        return resourceCurrent;
    }

    public void setResourceCurrent(double resourceCurrent) {
        this.resourceCurrent = resourceCurrent;
        player.setExp((float) (this.resourceCurrent/resourceMax));
    }

    public int getResourceMax() {
        return resourceMax;
    }

    public void setResourceMax(int resourceMax) {
        this.resourceMax = resourceMax;
    }

    public KitType getKit() {
        return kit;
    }

    public void setKit(KitType kit) {
        this.kit = kit;
    }

}