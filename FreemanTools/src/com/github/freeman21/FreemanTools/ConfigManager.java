package com.github.freeman21.FreemanTools;

import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
	
	private FreemanTools plugin;
	public List<Integer>FREEWORLD 					= 	null;
	public List<Integer>PAIDWORLD 					= 	null;
	public boolean SLAPPING_ENABLED 				= 	false;
	
	public ConfigManager(FreemanTools FreemanTools) {
		plugin = FreemanTools;
	}
	public void loadConfig() {
		FileConfiguration config = plugin.getConfig();

		config.options().copyDefaults(true);
		plugin.saveConfig();
	
	
	SLAPPING_ENABLED		= config.getBoolean("	slapping.enabled");
	}
}