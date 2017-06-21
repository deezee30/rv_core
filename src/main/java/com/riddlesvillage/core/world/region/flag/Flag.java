package com.riddlesvillage.core.world.region.flag;

import com.riddlesvillage.core.collect.EnhancedList;

public class Flag {

	public static Flag
			BUILD				= new Flag("BUILD", true),
			BREAK				= new Flag("BREAK", true),
			TALK				= new Flag("TALK", true),
			COMMAND				= new Flag("COMMAND", true),
			PVP					= new Flag("PVP", true),
			PVE					= new Flag("PVE", true),
			DAMAGE				= new Flag("DAMAGE", true),
			BLOCK_INTERACTION	= new Flag("BLOCK_INTERACTION", true),
			ITEM_INTERACTION	= new Flag("ITEM_INTERACTION", true),
			ENTITY_INTERACTION	= new Flag("ENTITY_INTERACTION", true),
			ITEM_DROP			= new Flag("ITEM_DROP", true),
			EXP_DROP			= new Flag("EXP_DROP", true),
			PASSIVE_MOB_SPAWN	= new Flag("PASSIVE_MOB_SPAWN", true),
			NEUTRAL_MOB_SPAWN	= new Flag("NEUTRAL_MOB_SPAWN", true),
			AGGRESSIVE_MOB_SPAWN= new Flag("AGGRESSIVE_MOB_SPAWN", true),
			EXPLOSION			= new Flag("EXPLOSION", true),
			HEALTH_REGENERATION = new Flag("HEALTH_REGENERATION", true),
			HUNGER_LOSS			= new Flag("HUNGER_LOSS", true),
			POTION_SPLASH		= new Flag("POTION_SPLASH", true),
			LIGHTER				= new Flag("LIGHTER", true),
			VEHICLE_PLACE		= new Flag("VEHICLE_PLACE", true),
			VEHICLE_DESTROY		= new Flag("VEHICLE_DESTROY", true),
			SLEEP				= new Flag("SLEEP", true),
			BLOCK_FREEZE		= new Flag("BLOCK_FREEZE", true),
			BLOCK_MELT			= new Flag("BLOCK_MELT", true),
			GRASS_SPREAD		= new Flag("GRASS_SPREAD", true),
			MYCELIUM_SPREAD		= new Flag("MYCELIUM_SPREAD", true),
			VINE_GROWTH			= new Flag("VINE_GROWTH", true),
			ENDERMAN_BUILD		= new Flag("ENDERMAN_BUILD", true);

	private static EnhancedList<Flag> ALL_FLAGS = new EnhancedList<>();

	protected transient int id;
	protected String flag;
	protected transient boolean val;

	private Flag(String flag, boolean val) {
		id = ALL_FLAGS.size();
		this.flag = flag.toUpperCase();
		this.val = val;
		ALL_FLAGS.add(this);
	}

	public int getId() {
		return id;
	}

	public String getFlag() {
		return flag;
	}

	public boolean isAllowed() {
		return val;
	}

	@Override
	public String toString() {
		return flag;
	}

	public static Flag from(String flag) {
		for (Flag f : ALL_FLAGS) {
			if (f.getFlag().toUpperCase().equals(flag.toUpperCase())) {
				return f;
			}
		}

		return null;
	}

	public static Flag from(int id) {
		return ALL_FLAGS.get(id);
	}

	public static Flag create(String flag, boolean def) {
		return new Flag(flag, def);
	}
}