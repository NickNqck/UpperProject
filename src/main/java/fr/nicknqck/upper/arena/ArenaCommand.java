package fr.nicknqck.upper.arena;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fkpi.teams.Team;
import fr.nicknqck.upper.Upper;
import fr.nicknqck.upper.utils.EventUtils;
import fr.nicknqck.upper.utils.ItemBuilder;
import fr.nicknqck.upper.utils.NMSUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ArenaCommand implements CommandExecutor, Listener {

    private boolean running = false;
    private final Map<UUID, Team> teamMap = new ConcurrentHashMap<>();
    private final Map<UUID, Marker> locationMap = new ConcurrentHashMap<>();
    @Getter
    private UUID uuidWinner = null;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player))return false;
        final Player sender = (Player) commandSender;
        if (strings.length == 1) {
            if (strings[0].equalsIgnoreCase("start")) {
                if (!sender.isOp()) {
                    commandSender.sendMessage("§cIl faut être op pour faire cette commande");
                    return true;
                }
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("§bUn combat va bientôt être organisés, chaque équipe à§c 10 minutes§b pour choisir son combattant !");
                System.out.println(sender.getName()+" a lancer le chrono");
                this.running = true;
                new TimeLeftRunnable(this).runTaskTimerAsynchronously(Upper.getInstance(), 0, 20);
                return true;
            }
            if (strings[0].equalsIgnoreCase("join")) {
                if (!running) {
                    Bukkit.getLogger().info(sender.getName()+" a essayer de join l'arène");
                    return false;
                }
                final Team team = Fk.getInstance().getFkPI().getTeamManager().getPlayerTeam(sender);
                if (team == null) {
                    sender.sendMessage("§cImpossible de rejoindre, vous n'avez pas d'équipe");
                    Bukkit.getLogger().info(sender.getName()+" a essayer de join mais n'a pas pu car team = null");
                    return true;
                }
                if (this.teamMap.containsValue(team)) {
                    sender.sendMessage("§cVotre équipe sera déjà représenter !");
                    return true;
                }
                this.teamMap.put(sender.getUniqueId(), team);
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage(team.getChatColor()+sender.getName()+"§b s'est engagé dans l'§carène§b.");
                return true;
            }
            if (strings[0].equalsIgnoreCase("reward")) {
                if (this.uuidWinner == null)return false;
                if (!this.uuidWinner.equals(sender.getUniqueId())) {
                    sender.sendMessage("§7Vous n'êtes pas le vainqueur de l'§carène§7.");
                    return true;
                }
                ((Player) commandSender).getWorld().dropItemNaturally(((Player) commandSender).getLocation(), new ItemStack(Material.NETHER_STALK, 6));
                ((Player) commandSender).getWorld().dropItemNaturally(((Player) commandSender).getLocation(), new ItemStack(Material.BLAZE_ROD, 2));
                ((Player) commandSender).getWorld().dropItemNaturally(((Player) commandSender).getLocation(), new ItemStack(Material.GHAST_TEAR, 2));
                commandSender.sendMessage("§7Les récompenses ont été jeté à vos pieds.");
                this.uuidWinner = null;
            }
            if (strings[0].equalsIgnoreCase("list")) {
                for (final World world : Bukkit.getWorlds()) {
                    sender.sendMessage("world = "+world.getName() + " '( "+world+" )'");
                }
                for (final UUID uuid : this.teamMap.keySet()) {
                    String string = uuid.toString();
                    final Player player = Bukkit.getPlayer(uuid);
                    if (player != null) {
                        string+=(" "+player.getName());
                    }
                    string+=(" team = "+this.teamMap.get(uuid));
                    commandSender.sendMessage(string);
                }
                return true;
            }
        }
        return false;
    }
    private synchronized void stopRunning() {
        if (this.teamMap.size() > 1) {
            final World arena = Bukkit.getWorld("mapSkyDef");
            int count = 0;
            for (UUID uuid : this.teamMap.keySet()) {
                final Player player = Bukkit.getPlayer(uuid);
                if (player == null)continue;
                count++;
                Location loc = player.getLocation();
                if (count == 1) {
                    loc = new Location(arena, -32, 219, 0.5, -90, 1.5f);
                } else if (count == 2) {
                    loc = new Location(arena, 2.5, 198, 0.5, 90, -2);
                } else if (count == 3) {
                    loc = new Location(arena, -75, 198, 0.5, -90, 2);
                }
                this.locationMap.put(uuid, new Marker(player, loc));
                player.sendMessage("§7Vous avez été téléportez dans l'§carène§7.");
                loc.getWorld().loadChunk(loc.getChunk());
                player.teleport(loc);
                player.getInventory().clear();
                player.getInventory().setContents(new ItemStack[] {
                        new ItemBuilder(Material.IRON_SWORD).toItemStack(),
                        new ItemBuilder(Material.COBBLESTONE, 64).toItemStack(),
                        new ItemBuilder(Material.BOW).toItemStack(),
                        new ItemBuilder(Material.GOLDEN_APPLE, 3).toItemStack(),
                        new ItemBuilder(Material.COBBLESTONE, 64).toItemStack(),
                        new ItemBuilder(Material.COOKED_MUTTON, 8).toItemStack(),
                        new ItemBuilder(Material.LAVA_BUCKET, 1).toItemStack(),
                        new ItemBuilder(Material.WATER_BUCKET, 1).toItemStack(),
                        new ItemBuilder(Material.IRON_PICKAXE, 1).addEnchant(Enchantment.DIG_SPEED, 2).toItemStack(),
                        new ItemBuilder(Material.IRON_AXE, 1).addEnchant(Enchantment.DIG_SPEED, 2).toItemStack(),
                        new ItemBuilder(Material.LAVA_BUCKET, 1).toItemStack(),
                        new ItemBuilder(Material.WATER_BUCKET, 1).toItemStack(),
                        new ItemBuilder(Material.LAVA_BUCKET, 1).toItemStack(),
                        new ItemBuilder(Material.WATER_BUCKET, 1).toItemStack(),
                        new ItemBuilder(Material.ARROW, 8).toItemStack()
                });
                player.getInventory().setHelmet(new ItemStack(Material.IRON_HELMET));
                player.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
                player.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
                player.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS));
            }
            Bukkit.broadcastMessage("");
            Bukkit.broadcastMessage("§bTout les participants ont été§c téléporter§b, ils reviendront la ou ils étaient et avec leurs stuff de départ une fois qu'un gagnant aura été désigner !");
            EventUtils.registerEvents(this);
        } else if (this.teamMap.size() == 1) {
            final UUID uuid = this.teamMap.keySet().stream().findFirst().get();
            final Team team = this.teamMap.get(uuid);
            Bukkit.broadcastMessage("");
            Bukkit.broadcastMessage("§bSeulement§c 1 participant§b a osé se joindre à l'§carène§b, par défaut l'équipe "+team.getChatColor()+team.getName()+"§b est désigner vainqueur.");
            this.uuidWinner = uuid;
            for (String teamPlayer : team.getPlayers()) {
                final Player player = Bukkit.getPlayer(teamPlayer);
                if (player != null) {
                    player.sendMessage("§bLe gagnant de l'§carène§b devra faire la commande§6 /arene reward§b pour obtenir les récompenses de l'§carène§b.");
                }
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void onRespawn(final PlayerRespawnEvent event) {
        if (!this.locationMap.containsKey(event.getPlayer().getUniqueId()))return;
        final Marker marker = this.locationMap.get(event.getPlayer().getUniqueId());
        this.locationMap.remove(event.getPlayer().getUniqueId());
        //Load le chunk au cas ou il n'est plus load
        if (marker.initLocation.getWorld() == null || !marker.initLocation.getWorld().isChunkLoaded(marker.initLocation.getBlockX() >> 4, marker.initLocation.getBlockZ() >> 4)) {
            marker.initLocation.getWorld().loadChunk(marker.initLocation.getBlockX() >> 4, marker.initLocation.getBlockZ() >> 4);
        }
        event.setRespawnLocation(marker.initLocation);
        //Pour lui redonner son stuff ect
        Bukkit.getScheduler().scheduleSyncDelayedTask(Upper.getInstance(), () -> {
            final Player player = event.getPlayer();
            player.getInventory().clear();
            player.getInventory().setHelmet(marker.armors[0]);
            player.getInventory().setChestplate(marker.armors[1]);
            player.getInventory().setLeggings(marker.armors[2]);
            player.getInventory().setBoots(marker.armors[3]);

            for (int i = 0; i < marker.inventory.length; i++) {
                ItemStack itemStack = marker.inventory[i];
                if (itemStack != null){
                    if (itemStack.getType().equals(Material.AIR))continue;
                    player.getInventory().setItem(i, itemStack);
                }
            }

            player.teleport(marker.initLocation);
            player.sendMessage("§7Vous avez été totalement rétablie de l'§carène§7.");
        }, 60);
        //Pour détecter le vainqueur
        Bukkit.getScheduler().scheduleSyncDelayedTask(Upper.getInstance(), () -> {
            if (this.locationMap.size() == 1) {
                final UUID uuid = this.locationMap.keySet().stream().findFirst().get();
                final Marker winMark = this.locationMap.get(uuid);
                new BukkitRunnable() {

                    @Override
                    public void run() {
                        final Player player = Bukkit.getPlayer(uuid);
                        if (player != null) {
                            locationMap.remove(player.getUniqueId());
                            player.getInventory().clear();
                            player.getInventory().setHelmet(winMark.armors[0]);
                            player.getInventory().setChestplate(winMark.armors[1]);
                            player.getInventory().setLeggings(winMark.armors[2]);
                            player.getInventory().setBoots(winMark.armors[3]);
                            int count = -1;
                            for (ItemStack itemStack : winMark.inventory) {
                                count++;
                                if (itemStack == null)continue;
                                player.getInventory().setItem(count, itemStack);
                            }
                            System.out.println("TP WINER 1 "+winMark.initLocation);
                            System.out.println("TP WINER 2 "+winMark.tpLocation);
                            player.teleport(winMark.initLocation);
                            player.sendMessage("§7Vous avez été totalement rétablie de l'§carène§7.");
                            player.sendMessage("§bIl faudra faire la commande§6 /arena reward§b pour obtenir les récompenses de l'§carène§b.");
                            Bukkit.broadcastMessage("");
                            Bukkit.broadcastMessage("§bLe joueur§6 "+player.getName()+"§b a gagné ses combats dans l'§carène§b.");
                            cancel();
                        }
                    }
                }.runTaskTimer(Upper.getInstance(), 20, 20);
                this.uuidWinner = uuid;
            }
        }, 80);
    }
    @EventHandler
    private void onMove(final PlayerMoveEvent event) {
        if (!this.locationMap.containsKey(event.getPlayer().getUniqueId())) return;
        if (event.getTo().getY() <= 195) {
            event.setCancelled(true);
            event.getPlayer().teleport(this.locationMap.get(event.getPlayer().getUniqueId()).tpLocation);
        }
    }
    @EventHandler
    private void onDamage(final EntityDamageEvent event) {
        if (!this.locationMap.containsKey(event.getEntity().getUniqueId()))return;
        if (event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
            event.setCancelled(true);
        }
    }
    private static class TimeLeftRunnable extends BukkitRunnable {

        private final ArenaCommand arenaCommand;
        private int time = 60*10;

        private TimeLeftRunnable(ArenaCommand arenaCommand) {
            this.arenaCommand = arenaCommand;
        }

        @Override
        public void run() {
            if (this.time <= 0 || !this.arenaCommand.running) {
                Bukkit.getScheduler().runTask(Upper.getInstance(), this.arenaCommand::stopRunning);
                cancel();
                return;
            }
            for (final Player player : Bukkit.getOnlinePlayers()) {
                NMSUtils.sendActionBar(player, "§bTemps avant combat dans l'§carène§b (§6/arena join§b):§c "+secondsTowardsBeautiful(time)+"§b (§c"+this.arenaCommand.teamMap.size()+"§b/§6"+Fk.getInstance().getFkPI().getTeamManager().getTeams().size()+"§b)");
            }
            this.time--;
        }
    }
    public static String secondsTowardsBeautiful(int seconds){
        int hours = seconds/3600;
        int minAndSec = seconds%3600;
        int min = minAndSec/60;
        int sec = minAndSec%60;

        if(hours == 0 && min == 0){
            return sec + "s";
        }

        if(hours == 0 && sec == 0){
            return min + "m";
        }

        if(min == 0 && sec == 0){
            return  hours + "h";
        }

        if (hours == 0) {
            return min+"m "+sec+"s";
        }

        if(sec == 0){
            return  hours + "h " + min + "m";
        }
        return  hours + "h " + min + "m " + sec + "s";
    }
    private static class Marker {

        private final Location initLocation;
        private final ItemStack[] inventory;
        private final ItemStack[] armors;
        private final Location tpLocation;

        private Marker(final Player player, Location tpLocation) {
            this.initLocation = player.getLocation();
            this.inventory = player.getInventory().getContents();
            this.armors = new ItemStack[] {
                    player.getInventory().getHelmet(),
                    player.getInventory().getChestplate(),
                    player.getInventory().getLeggings(),
                    player.getInventory().getBoots()
            };
            this.tpLocation = tpLocation;
        }
    }
}