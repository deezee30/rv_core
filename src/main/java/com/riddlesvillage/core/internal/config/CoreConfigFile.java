/*
 * RiddlesCore
 */

package com.riddlesvillage.core.internal.config;

import com.riddlesvillage.core.Core;
import com.riddlesvillage.core.file.ConfigFile;
import org.bukkit.plugin.java.JavaPlugin;

abstract class CoreConfigFile extends ConfigFile {

	public CoreConfigFile() {}

	public CoreConfigFile(boolean instaLoad) {
		super(instaLoad);
	}

	@Override
	protected final JavaPlugin getPluginInstance() {
		return Core.get();
	}
}