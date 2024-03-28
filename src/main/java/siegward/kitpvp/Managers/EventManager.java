package siegward.kitpvp.Managers;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import siegward.kitpvp.KitPvP;

public class EventManager {
    private static final KitPvP plugin = KitPvP.getPlugin();
    static int currentTime;
    static int record = -1;
    static Player bestPlayer = null;

    public static void stopAll(){
        currentTime = 0;
    }

    public static int getRecord(){
        return record;
    }
    public static void setRecord(int newRecord, Player p){
        record = newRecord;
        bestPlayer = p;
    }
    public static void startSoulHunt(int hours, int minutes, int seconds){
        record = 0;
        currentTime = seconds + minutes*60 + hours*60*60;

        BossBar progress = BossBar.bossBar(Component.text("" + ChatColor.GOLD + ChatColor.BOLD + "Охота на Души"),1f, BossBar.Color.GREEN, BossBar.Overlay.PROGRESS);
        BukkitRunnable mainEvent = new BukkitRunnable() {
            int totalTime = currentTime;
            @Override
            public void run() {
                //TODO отобразить на босбаре топ игрока и рекорд
                for (Player i : Bukkit.getOnlinePlayers()){
                    i.showBossBar(progress);
                }
                progress.progress((float) currentTime / totalTime);
                if (record == 0 && bestPlayer == null) {
                    progress.name(Component.text("" + ChatColor.GOLD + ChatColor.BOLD + "Охота на Души"));
                }else{
                    progress.name(Component.text("" + ChatColor.GOLD + ChatColor.BOLD + "Охота на Души" + ChatColor.GRAY + " | " + ChatColor.YELLOW + bestPlayer.getName() + ChatColor.GRAY + " - " + ChatColor.RED + record));
                }
                int souls = 0;
                Player topSouls = null;
                for (Player player : Bukkit.getOnlinePlayers()){
                    int count = 0;
                    for (ItemStack item : player.getInventory()){
                        if (item == null) continue;
                        if (item.getType().equals(Material.NETHER_STAR) && item.getItemMeta().displayName().equals(Component.text("Потерянная Душа").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.AQUA))){
                            count+= item.getAmount();
                        }
                    }
                    if (count > souls){
                        souls = count;
                        topSouls = player;
                    }
                }
                if (souls > 0 && topSouls != null){
                    topSouls.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING,30,0, false,false,true));
                }

                if (currentTime <= 0) {
                    for (Player i : Bukkit.getOnlinePlayers()){
                        if (bestPlayer != null) i.showTitle(Title.title(Component.text("" + ChatColor.GOLD + ChatColor.BOLD + "Охота на Души" + ChatColor.GREEN + " завершена!"), Component.text(ChatColor.WHITE + "Победил " + ChatColor.YELLOW + bestPlayer.getName() + ChatColor.WHITE + " набрав " + ChatColor.RED + record + ChatColor.WHITE +" Душ")));
                        else i.showTitle(Title.title(Component.text("" + ChatColor.GOLD + ChatColor.BOLD + "Охота на Души" + ChatColor.GREEN + " завершена!"),Component.text("")));
                        i.hideBossBar(progress);
                        this.cancel();
                    }
                    record = -1;
                    bestPlayer = null;
                }
                currentTime -=1;
            }
        };
        mainEvent.runTaskTimer(plugin,0,20);
    }
}
