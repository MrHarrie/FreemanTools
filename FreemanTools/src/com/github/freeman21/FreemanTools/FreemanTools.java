/**
 * Plugin developed by Freeman21 with support of Meaglin
 * This plugin can only be used when approved by Freeman21
 * 
 */

package com.github.freeman21.FreemanTools;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.logging.Logger;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
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

	private Permission permission = null;
	@SuppressWarnings("unused")
	private Logger log = Logger.getLogger("FreemanTools");

	public void onEnable() {
		setupPermissions();
		setConfig(null);
		getServer().getPluginManager().registerEvents(this, this);
	}

	public void setConfig(FreemanTools FreemanTools) {
		// this.saveDefaultConfig();
		final ConfigurationSection config = getConfig();
		final Map<String, Object> def = new HashMap<String, Object>();
		def.put("Freemanslap.FallVelocity", 5);
		def.put("Freemanslap.SlapTool", 297);
		def.put("Freemanslap.Enable.Other", true);
		def.put("Freemanslap.Enable.Self", true);
		def.put("FreemanHeadDrop.Enable", true);
		def.put("FreemanHeadDrop.DropChance", 5);
		def.put("FreemanBlockHead.Enable", true);
		for (final Entry<String, Object> e : def.entrySet())
			if (!config.contains(e.getKey()))
				config.set(e.getKey(), e.getValue());
		saveConfig();
	}
	
	private boolean setupPermissions() {
		RegisteredServiceProvider<Permission> permissionProvider = getServer()
				.getServicesManager().getRegistration(
						net.milkbowl.vault.permission.Permission.class);
		if (permissionProvider != null) {
			permission = permissionProvider.getProvider();
		}
		return (permission != null);
	}

	public boolean has(Player player, String node) {
		return permission != null
				&& permission.has(player, "freemantools." + node);
	}

	@EventHandler
	public void playerHit(EntityDamageEvent subevent) {
		if (FreemanTools.this.getConfig()
				.getBoolean("Freemanslap.Enable.Other")) {
			if (!(subevent instanceof EntityDamageByEntityEvent))
				return;
			EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) subevent;
			if (!(event.getDamager() instanceof Player))
				return;
			// if(!(event.getEntity() instanceof Player)) return;

			Player player = (Player) event.getDamager();
			if (player.getItemInHand() == null
					|| player.getItemInHand().getTypeId() != FreemanTools.this
					.getConfig().getInt("Freemanslap.SlapTool"))
				return;
			if (!has(player, "slap.other"))
				return;// check if player has permission freemantools.slap.other

			PlayerInventory inventory = player.getInventory();
			ItemStack itemstack = new ItemStack(385, 1);
			HashMap<Integer, ItemStack> rest = inventory.removeItem(itemstack);
			if (rest.size() != 0) {
			} else {
				event.getEntity().setFireTicks(10000);
			}

			if (event.getEntity() instanceof Player) {
				Player target = (Player) event.getEntity();
				player.sendMessage("You have slapped a "
						+ target.getDisplayName() + "!");
				target.sendMessage("You were slapped by "
						+ player.getDisplayName() + "!");
			} else {
				player.sendMessage("You have slapped "
						+ event.getEntityType().getName() + "!");
			}

			// getLogger().info("Player + hit");
			Vector velocity = player
					.getLocation()
					.getDirection()
					.multiply(
							FreemanTools.this.getConfig().getInt(
									"Freemanslap.FallVelocity"));
			event.getEntity().setVelocity(velocity);
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void selfHit(PlayerInteractEvent event) throws InterruptedException {
		if (FreemanTools.this.getConfig().getBoolean("Freemanslap.Enable.Self")) {
			Player player = (Player) event.getPlayer();
			if (event.getAction() != Action.LEFT_CLICK_BLOCK)
				return;
			if (player.getItemInHand() == null
					|| player.getItemInHand().getTypeId() != FreemanTools.this
					.getConfig().getInt("Freemanslap.SlapTool"))
				return;
			if (!has(player, "slap.self"))
				return;// check if player has permission freemantools.slap.self
			player.sendMessage("you have slapped yourself!");
			Vector velocity = player
					.getLocation()
					.getDirection()
					.multiply(
							FreemanTools.this.getConfig().getInt(
									"Freemanslap.FallVelocity")
									* -0.4);
			player.setVelocity(velocity);
			event.setCancelled(true);
		}
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		if (cmd.getName().equalsIgnoreCase("blockhead")) { // If the player
			// typed /basic then
			// do the
			// following...
			if (FreemanTools.this.getConfig().getBoolean(
					"FreemanBlockHead.Enable")) {
				Player player = (Player) sender;
				if (!has(player, "blockhead"))
					return false;// check if player has permission
				// freemantools.slap.other
				PlayerInventory inventory = player.getInventory();
				int handAmount = player.getItemInHand().getAmount();
				short damagevalue = player.getItemInHand().getDurability();
				int handleft = handAmount -= 1;
				int slot = inventory.getHeldItemSlot();
				int item = player.getItemInHand().getTypeId();
				Map<Enchantment, Integer> ench = inventory.getItemInHand()
						.getEnchantments();

				if (handleft == 0) {
					ItemStack itemstack4 = new ItemStack(Material.AIR,
							handleft, damagevalue);
					inventory.setItem(slot, itemstack4);

				} else {
					ItemStack itemstack4 = new ItemStack(item, handleft,
							damagevalue);
					inventory.setItem(slot, itemstack4);

				}

				ItemStack itemstack3 = inventory.getHelmet();
				ItemStack itemstack2 = new ItemStack(item, 1, damagevalue);
				itemstack2.addEnchantments(ench);

				if (itemstack3 != null)
					if (inventory.getHelmet() != null) {
						Map<Enchantment, Integer> enchHelmet = inventory
								.getHelmet().getEnchantments();
						{
							if (enchHelmet != null) {
								itemstack3.addEnchantments(enchHelmet);
							}
							player.getWorld().dropItemNaturally(
									player.getLocation(), itemstack3);
						}
					}
				inventory.setHelmet(itemstack2);
				player.sendMessage("You now have a new blockhead");
				return true;
			} else {
				Player player = (Player) sender;
				player.sendMessage("This is not enabled");
			}

		}
		if (cmd.getName().equalsIgnoreCase("FreemanToolsVersion")) {
			Player player = (Player) sender;
			String version = getDescription().getVersion();
			player.sendMessage("FreemanTools is on version " + version);
			return true;
		}
		return false;
	}

	@EventHandler(ignoreCancelled = true)
	public void headDrop(EntityDeathEvent event) {
		if (FreemanTools.this.getConfig().getBoolean("FreemanHeadDrop.Enable")) {
			Player Killer = event.getEntity().getKiller();
			Entity Target = event.getEntity();
			Location Drop = Target.getLocation();

			if (Killer != null) {
				Random randomGenerator = new Random();
				int randomInt = randomGenerator.nextInt(100);
				if (randomInt < FreemanTools.this.getConfig().getInt(
						"FreemanHeadDrop.DropChance")) {
					if (Target instanceof Creeper) {
						ItemStack itemstack = new ItemStack(397, 1, (short) 4);
						Target.getWorld().dropItemNaturally(Drop, itemstack);
					} else if (Target instanceof Skeleton) {
						ItemStack itemstack = new ItemStack(397, 1, (short) 0);
						Target.getWorld().dropItemNaturally(Drop, itemstack);
					} else if (Target instanceof Zombie) {
						ItemStack itemstack = new ItemStack(397, 1, (short) 3);
						Target.getWorld().dropItemNaturally(Drop, itemstack);
					} else if (Target instanceof Player) {
						ItemStack itemstack = new ItemStack(397, 1, (short) 2);
						Target.getWorld().dropItemNaturally(Drop, itemstack);
					}
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void Freezeplayer(PlayerMoveEvent event) {

	}

	public void onDisable() {
	}

}