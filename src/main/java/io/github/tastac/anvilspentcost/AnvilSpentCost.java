package io.github.tastac.anvilspentcost;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class AnvilSpentCost extends JavaPlugin implements Listener {

    public static final HashMap<Player, Integer> map = new HashMap<>();
    public static String message;

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);

        this.saveDefaultConfig();

        message = this.getConfig().getString("message");
    }

    @EventHandler
    public void onInventoryOpened(InventoryOpenEvent event){
        if(event.getInventory() instanceof AnvilInventory) map.put((Player)event.getPlayer(), getPlayerExp((Player)event.getPlayer()));
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        if(event.getSlot() == 2 && event.getClickedInventory() instanceof AnvilInventory && (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY || event.getAction() == InventoryAction.PICKUP_ONE || event.getAction() == InventoryAction.PICKUP_SOME || event.getAction() == InventoryAction.PICKUP_HALF || event.getAction() == InventoryAction.PICKUP_ALL)){
            Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
                Player player = (Player)event.getWhoClicked();
                player.sendMessage(message.replace("{exp}", (map.get(player) - getPlayerExp(player) + "")));
                System.out.println((map.get(player) - getPlayerExp(player)));
                map.put(player, getPlayerExp(player));
            }, 1);
        }
    }

    @EventHandler
    public void onInventoryClosed(InventoryCloseEvent event){
        if(event.getInventory() instanceof AnvilInventory) map.remove(event.getPlayer());
    }

    public static int getExpToLevelUp(int level){
        if(level <= 15){
            return 2*level+7;
        } else if(level <= 30){
            return 5*level-38;
        } else {
            return 9*level-158;
        }
    }

    public static int getExpAtLevel(int level){
        if(level <= 16){
            return (int) (Math.pow(level,2) + 6*level);
        } else if(level <= 31){
            return (int) (2.5*Math.pow(level,2) - 40.5*level + 360.0);
        } else {
            return (int) (4.5*Math.pow(level,2) - 162.5*level + 2220.0);
        }
    }

    public static int getPlayerExp(Player player){
        int exp = 0;
        int level = player.getLevel();

        exp += getExpAtLevel(level);

        exp += Math.round(getExpToLevelUp(level) * player.getExp());

        return exp;
    }
}