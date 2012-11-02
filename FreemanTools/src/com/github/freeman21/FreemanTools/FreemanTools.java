/**
 * Plugin developed by Freeman21 with support of Meaglin
 * This plugin can only be used when approved by Freeman21
 * 
 */

package com.github.freeman21.FreemanTools;

import java.util.HashMap;
import java.util.Random;
import java.util.logging.Logger;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;


public class FreemanTools extends JavaPlugin implements Listener {
	
	public static final int velocityFactor = 5;
	private Permission permission = null;
	@SuppressWarnings("unused")
	private Logger log = Logger.getLogger("FreemanTools");

	public void onEnable(){
		setupPermissions();
		getServer().getPluginManager().registerEvents(this, this);
	}

	
	private boolean setupPermissions()
    {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        return (permission != null);
    }

	public boolean has(Player player, String node) {
		return permission != null && permission.has(player, "freemantools." + node);
	}
	
	@EventHandler
	public void playerHit(EntityDamageEvent subevent) {
			
			if(!(subevent instanceof EntityDamageByEntityEvent)) return;
			EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) subevent;
			
			if(!(event.getDamager() instanceof Player)) return;
			
			if(!(event.getEntity() instanceof Player)) return;
			
			Player player = (Player) event.getDamager();
			Player target = (Player) event.getEntity();
		
			if(player.getItemInHand() == null || player.getItemInHand().getTypeId() != Material.STICK.getId()) return;
			
			if(!has(player, "slap.other")) return;//check if player has permission freemantools.slap.other
			PlayerInventory inventory = player.getInventory();
			ItemStack itemstack = new ItemStack(385, 1);
			HashMap<Integer, ItemStack> rest = inventory.removeItem(itemstack);
			
			if(rest.size() != 0) {
			}
			else{
				target.setFireTicks(10000);
			}
			player.sendMessage("you have slapped " + target.getDisplayName() + "!");
			target.sendMessage("you were slapped by " + player.getDisplayName() + "!");
			//getLogger().info("Player + hit");
			Vector velocity = player.getLocation().getDirection().multiply(velocityFactor);
			target.setVelocity(velocity);
			event.setCancelled(true);
		}
	
	@EventHandler(ignoreCancelled = true,priority = EventPriority.HIGHEST)
	public void selfHit(PlayerInteractEvent event){
		Player player = (Player) event.getPlayer();
		if(event.getAction() != Action.LEFT_CLICK_BLOCK) return;
		if(player.getItemInHand() == null || player.getItemInHand().getTypeId() != Material.STICK.getId()) return;
		if(!has(player, "slap.self")) return;//check if player has permission freemantools.slap.self
		
		player.sendMessage("you have slapped yourself!");
		Vector velocity = player.getLocation().getDirection().multiply(velocityFactor * -0.4);
		player.setVelocity(velocity);
		event.setCancelled(true);
	}
	
	@EventHandler(ignoreCancelled = true)
	public void headDrop(EntityDeathEvent event)
		{
		Player Killer = event.getEntity().getKiller();
		Entity Target = event.getEntity();
		Location Drop = Target.getLocation();
		if(Killer != null) 
			{
				Random randomGenerator = new Random();
				int randomInt = randomGenerator.nextInt(100);
			
				if (randomInt >95 ){
					if (Target instanceof Creeper)
					{
						ItemStack itemstack = new ItemStack(397, 1,(short)4);
						Target.getWorld().dropItemNaturally(Drop, itemstack);
					}
					else if (Target instanceof Skeleton)
					{
						ItemStack itemstack = new ItemStack(397, 1,(short)0);
						Target.getWorld().dropItemNaturally(Drop, itemstack);
					}
					else if (Target instanceof Zombie)
					{
						ItemStack itemstack = new ItemStack(397, 1,(short)2);
						Target.getWorld().dropItemNaturally(Drop, itemstack);
					}
					else if (Target instanceof Player)
					{
						ItemStack itemstack = new ItemStack(397, 1,(short)3);
						Target.getWorld().dropItemNaturally(Drop, itemstack);
					}
				}
			}
		}
	
	@EventHandler (ignoreCancelled = true)
	public void Freezeplayer(PlayerMoveEvent event){
		
	}
	
	public void onDisable(){
	}
	
}