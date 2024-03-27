package siegward.kitpvp;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;


public class commands implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender Sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("KitPvP")){
            if (args[0].equalsIgnoreCase("help") && Sender instanceof Player && Sender.isOp()){
                //plugin.getLogger().
                Sender.sendMessage(Component.text("/pvp kit <kit> <Player>").color(NamedTextColor.YELLOW));
                Sender.sendMessage(Component.text("/pvp use <ability> <Player>").color(NamedTextColor.YELLOW));
                Sender.sendMessage(Component.text("/pvp team <team> <Player>").color(NamedTextColor.YELLOW));
                Sender.sendMessage(Component.text("/pvp help").color(NamedTextColor.YELLOW));
            }else if (args[0].equalsIgnoreCase("kit") && args.length == 3) {
                Player p = Bukkit.getPlayer(args[2]);
                assert p != null;
                KitType kit;
                p.setAllowFlight(false);
                p.setFlying(false);

                switch (args[1]) {
                    //воины
                    case "assassin":
                        kit = KitType.ASSASSIN;
                        PlayerManager.addPlayer(p,kit);
                        p.setWalkSpeed(0.23f);
                        //p.showTitle(Title.title(Component.text("123"),Component.text("456"), Title.Times.times(Duration.ofSeconds(10),Duration.ZERO,Duration.ZERO)));
                        //p.sendActionBar(Component.text("123"));

                        break;
                    case "knight":
                        kit = KitType.KNIGHT;
                        PlayerManager.addPlayer(p,kit);
                        p.setWalkSpeed(0.18f);
                        break;
                    case "merc":
                        kit = KitType.MERC;
                        PlayerManager.addPlayer(p,kit);
                        break;

                    //стрелки
                    case "robin":
                        kit = KitType.ROBIN;
                        PlayerManager.addPlayer(p,kit);
                        p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 999999, 1, false, false, true));
                        break;
                    case "steampunk":
                        kit = KitType.STEAMPUNK;
                        PlayerManager.addPlayer(p,kit);
                        p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 999999, 1, false, false, true));
                        break;
                    case "royal":
                        kit = KitType.ROYAL;
                        PlayerManager.addPlayer(p,kit);
                        p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 999999, 1, false, false, true));
                        break;

                    //маги
                    case "chaotic":
                        kit = KitType.CHAOTIC;
                        PlayerManager.addPlayer(p,kit);
                        break;
                    case "satanist":
                        kit = KitType.SATANIST;
                        PlayerManager.addPlayer(p,kit);
                        p.setAllowFlight(true);
                        p.setFlySpeed(0.03f);
                        break;
                    case "pyro":
                        kit = KitType.PYRO;
                        PlayerManager.addPlayer(p,kit);
                        break;

                    //призыватели
                    case "creeperman":
                        kit = KitType.CREEPERMAN;
                        PlayerManager.addPlayer(p,kit);
                        break;
                    case "necromancer":
                        kit = KitType.NECROMANCER;
                        PlayerManager.addPlayer(p,kit);
                        break;
                    case "undead":
                        kit = KitType.UNDEAD;
                        PlayerManager.addPlayer(p,kit);
                        break;

                    //очистки
                    case "none":
                        kit = KitType.NONE;
                        PlayerManager.addPlayer(p,kit);
                        break;
                }
            }else if (args[0].equalsIgnoreCase("team") && args.length == 3){
                Player p = Bukkit.getPlayer(args[2]);
                switch (args[1]) {
                    case "red":
                        PlayerManager.setPlayerTeam(p,TeamType.RED);
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "team join RED " + p.getName());
                        break;
                    case "blue":
                        PlayerManager.setPlayerTeam(p,TeamType.BLUE);
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "team join BLUE " + p.getName());
                        break;
                    default:
                        PlayerManager.setPlayerTeam(p,null);
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "team leave " + p.getName());
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "team join " + p.getName() + "_p " + p.getName());
                        break;
                }
            }else if (args[0].equalsIgnoreCase("use") && args.length == 3){
                PlayerModel m = PlayerManager.getModelByPlayer(Bukkit.getPlayer(args[2]));
                assert m != null;

                //способности
                switch (args[1]){
                    case "knight.armor":
                        KitManager.knightArmor(m);
                        break;
                    case "assassin.pearl":
                        KitManager.assassinPearl(m);
                        break;
                    case "merc.dash":
                        KitManager.mercDash(m);
                        break;

                    case "royal.firework":
                        KitManager.royalFirework(m);
                        break;
                    case "robin.bolt":
                        KitManager.robinBolt(m);
                        break;
                    case "steampunk.rebuild":
                        KitManager.steampunkRebuild(m);
                        break;

                    case "chaotic.snowball":
                        KitManager.chaoticSnowball(m);
                        break;
                    case "chaotic.cluster":
                        KitManager.chaoticCluster(m);
                        break;
                    case "chaotic.c4":
                        KitManager.chaoticC4(m);
                        break;
                    case "chaotic.boom":
                        KitManager.chaoticExplodeC4(m);
                        break;
                    case "satanist.skull":
                        KitManager.satanistSkull(m);
                        break;
                    case "satanist.egg":
                        KitManager.satanistEgg(m);
                        break;
                    case "satanist.jump":
                        KitManager.satanistJump(m);
                        break;
                    case "pyro.firethrower":
                        KitManager.pyroFirethrower(m);
                        break;
                    case "pyro.fireball":
                        KitManager.pyroFireball(m);
                        break;
                    case "pyro.napalm":
                        KitManager.pyroNapalm(m);
                        break;

                    case "creeperman.summon":
                        KitManager.creepermanSummon(m);
                        break;
                    case "creeperman.explosions":
                        KitManager.creepermanExplosions(m);
                        break;
                    case "necromancer.summon":
                        KitManager.necromancerSummon(m);
                        break;
                    case "necromancer.pentagram":
                        KitManager.necromancerPentagram(m);
                        break;
                    case "undead.summon":
                        KitManager.undeadSummon(m);
                        break;
                    case "undead.disaster":
                        KitManager.undeadDisaster(m);
                        break;

                    case "heal":
                        KitManager.heal(m);
                        break;
                    case "soul":
                        KitManager.soul(m);
                        break;
                }

            }
        }
        return true;
    }
}
