package siegward.kitpvp.Managers;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class MobModel {
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
