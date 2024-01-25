package siegward.kitpvp;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.ArrayList;
import java.util.List;

public class PlayerManager {
    private static final KitPvP plugin = KitPvP.getPlugin();
    private static final List<PlayerModel> playerList = new ArrayList<>();

    public static void init(){
        BukkitRunnable mainHandler = new BukkitRunnable() {
            @Override
            public void run() {
                for (PlayerModel model : playerList){
                    switch (model.getKit()){
                        case KNIGHT:
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
                    if (model.getResourceCurrent() == 0) model.setResourceDifferencePerSecond(model.getKit().getDefaultResourceDifferencePerSecond());
                    model.setResourceCurrent(Math.max(0, Math.min(model.getResourceMax(), model.getResourceCurrent() + model.getResourceDifferencePerSecond())));
                }
            }
        };
        mainHandler.runTaskTimer(plugin,0,1);
    }

    public static void addPlayer(Player p, KitType kit){
        playerList.add(new PlayerModel(p, kit.getDefaultStartingResource(), kit.getDefaultResourceDifferencePerSecond(), kit.getDefaultMaxResource(), kit));

    }

    public static void removePlayer(Player p){
        playerList.remove(getModelByPlayer(p));
        p.setMaxHealth(20.0);
        p.setWalkSpeed(0.2f);
        p.setLevel(0);
        p.setExp(0);
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

    public PlayerModel(Player player, double resourceCurrent, double resourceDifferencePerSecond, int resourceMax, KitType kit) {
        this.player = player;
        this.resourceCurrent = resourceCurrent;
        this.resourceDifferencePerSecond = resourceDifferencePerSecond;
        this.resourceMax = resourceMax;
        this.kit = kit;
        this.team = null;
        combatMode = 0;
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
        player.setExp((float) resourceCurrent/resourceMax);
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