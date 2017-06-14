package com.riddlesvillage.core.hologram;

import net.minecraft.server.v1_11_R1.EntityArmorStand;
import net.minecraft.server.v1_11_R1.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_11_R1.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_11_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Matthew E on 6/11/2017.
 */
public class Hologram implements IHologram {
    private Location location;
    private List<String> lineStringList;
    private Map<Player, List<EntityArmorStand>> holoPlayerListMap;

    public Hologram(Location location) {
        this.location = location;
        this.lineStringList = new ArrayList<>();
        this.holoPlayerListMap = new HashMap<>();
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public List<String> getLineList() {
        return lineStringList;
    }

    @Override
    public void display(Player... players) {
        for (Player player : players) {
            if (!holoPlayerListMap.containsKey(player)) {
                holoPlayerListMap.remove(player);
            }
            List<EntityArmorStand> hologramEntityList = getHologramEntityList();
            holoPlayerListMap.put(player, hologramEntityList);
            for (EntityArmorStand entityArmorStand : hologramEntityList) {
                ((CraftPlayer)player).getHandle().playerConnection.sendPacket(new PacketPlayOutSpawnEntityLiving(entityArmorStand));
            }
        }
    }

    public void display(Player player, String... replace) {
        if (!holoPlayerListMap.containsKey(player)) {
            holoPlayerListMap.remove(player);
        }
        List<EntityArmorStand> hologramEntityList = getHologramEntityList(replace);
        holoPlayerListMap.put(player, hologramEntityList);
        for (EntityArmorStand entityArmorStand : hologramEntityList) {
            ((CraftPlayer)player).getHandle().playerConnection.sendPacket(new PacketPlayOutSpawnEntityLiving(entityArmorStand));
        }
    } 

    @Override
    public void display(Player[] players, String... replace) {
        for (Player player : players) {
            display(player, replace);
        }
    }

    @Override
    public void display(String... replace) {
        Player[] players = new Player[Bukkit.getOnlinePlayers().size()];
        for (int i = 0; i < players.length; i++) {
            players[i] = new ArrayList<>(Bukkit.getOnlinePlayers()).get(i);
        }
        this.display(players, replace);
    }   

    @Override
    public void hide(Player... players) {
       for (Player player : players) {
           List<EntityArmorStand> entityArmorStandList = holoPlayerListMap.get(player);
           for (EntityArmorStand entityArmorStand : entityArmorStandList) {
               entityArmorStand.getBukkitEntity().remove();
           }
           entityArmorStandList.clear();
           holoPlayerListMap.remove(player);
       }
    }

    private List<EntityArmorStand> getHologramEntityList() {
        List<EntityArmorStand> spawnEntityLivingList = new ArrayList<>();
        double increment = 0.0D;
        for (String lineString : this.lineStringList) {
            WorldServer worldServer = ((CraftWorld)getLocation().getWorld()).getHandle();
            EntityArmorStand entityArmorStand = new EntityArmorStand(worldServer);
            entityArmorStand.setLocation(getLocation().getX(), getLocation().getY()-increment, getLocation().getZ(), 0, 0);
            if (lineString.startsWith(ChatColor.BOLD.toString())) {
                increment += 0.4;
            } else {
                increment += 0.3;
            }
            entityArmorStand.setCustomName(lineString);
            entityArmorStand.setCustomNameVisible(true);
            spawnEntityLivingList.add(entityArmorStand);
        }
        return spawnEntityLivingList;
    }
    
  private List<EntityArmorStand> getHologramEntityList(String... replace) {
        List<EntityArmorStand> spawnEntityLivingList = new ArrayList<>();
        double increment = 0.0D;
        for (String lineString : this.lineStringList) {
            if (replace != null) {
                for (int i = 0; i < replace.length; i++) {
                    lineString = lineString.replaceAll("\\{"+ i + "}", replace[i]);
                }
            }
            WorldServer worldServer = ((CraftWorld)getLocation().getWorld()).getHandle();
            EntityArmorStand entityArmorStand = new EntityArmorStand(worldServer);
            entityArmorStand.setLocation(getLocation().getX(), getLocation().getY()-increment, getLocation().getZ(), 0, 0);
            if (lineString.startsWith(ChatColor.BOLD.toString())) {
                increment += 0.4;
            } else {
                increment += 0.3;
            }
            entityArmorStand.setCustomName(lineString);
            entityArmorStand.setCustomNameVisible(true);
            spawnEntityLivingList.add(entityArmorStand);
        }
        return spawnEntityLivingList;
    }

    @Override
    public void display() {
        Player[] players = new Player[Bukkit.getOnlinePlayers().size()];
        for (int i = 0; i < players.length; i++) {
            players[i] = new ArrayList<>(Bukkit.getOnlinePlayers()).get(i);
        }
        this.display(players);
    }

    @Override
    public void addLine(String line) {
        this.lineStringList.add(line);
        Player[] players = new Player[Bukkit.getOnlinePlayers().size()];
        for (int i = 0; i < players.length; i++) {
            players[i] = new ArrayList<>(Bukkit.getOnlinePlayers()).get(i);
        }
        this.hide(players);
        this.holoPlayerListMap.clear();
        this.display(players);
    }
}
