package siegward.kitpvp.Managers;

import org.bukkit.entity.Player;
import siegward.kitpvp.utils.KitType;
import siegward.kitpvp.utils.TeamType;

public class PlayerModel {
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

        this.resourceCurrent = Math.max(0, Math.min(resourceMax, resourceCurrent));
        player.setExp((float) (this.resourceCurrent / resourceMax));
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
