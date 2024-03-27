package siegward.kitpvp;

public enum KitType {
    /*
    KNIGHT рыцарь
    ASSASSIN ассасин
    MERC наемник
    ROYAL королевский стрелок
    STEAMPUNK стимпанк
    CHAOTIC взрывной маг
    DARK темный маг
    PYRO огненный маг
    CREEPERMAN крипермен
    NECROMANCER некромант
    UNDEAD нежить
     */
    NONE(0,0,100),
    KNIGHT(0, 0, 200),
    ASSASSIN(10, 100,100),
    MERC(10, 100, 100),
    ROYAL(10, 100, 100),
    STEAMPUNK(10, 0, 200),
    ROBIN(10, 100, 100),
    CHAOTIC(10, 200, 200),
    SATANIST(10, 200, 200),
    PYRO(10, 200, 200),
    CREEPERMAN(10, 200, 200),
    NECROMANCER(10, 200, 200),
    UNDEAD(10, 200, 200);

    private final double defaultResourceDifferencePerSecond;
    private final double defaultStartingResource;
    private final int defaultMaxResource;

    KitType(double defaultResourceDifferencePerSecond, double defaultStartingResource, int defaultMaxResource) {
        this.defaultResourceDifferencePerSecond = defaultResourceDifferencePerSecond;
        this.defaultStartingResource = defaultStartingResource;
        this.defaultMaxResource = defaultMaxResource;
    }

    public double getDefaultResourceDifferencePerSecond(){
        return defaultResourceDifferencePerSecond;
    }

    public double getDefaultStartingResource() {
        return defaultStartingResource;
    }

    public int getDefaultMaxResource() {
        return defaultMaxResource;
    }
}
