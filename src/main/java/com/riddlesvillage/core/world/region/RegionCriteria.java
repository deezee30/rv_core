/*
 * rv_core
 * 
 * Created on 19 June 2017 at 12:07 PM.
 */

package com.riddlesvillage.core.world.region;

import com.riddlesvillage.core.collect.EnhancedList;
import com.riddlesvillage.core.world.Vector3D;
import com.riddlesvillage.core.world.region.type.RegionType;
import org.apache.commons.lang3.Validate;
import org.bukkit.World;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public final class RegionCriteria implements Iterable<Region> {

	private RegionList regions = new RegionList();

	private Optional<List<World>> worlds = Optional.empty();
	private Optional<List<Predicate<Region>>> predicates = Optional.empty();
	private Optional<List<RegionType>> types = Optional.empty();
	private Optional<List<Vector3D>> coords = Optional.empty();

	public RegionCriteria() {}

	public RegionCriteria(RegionList regions) {
		in(regions);
	}

	public RegionCriteria in(RegionList regions) {
		regions.addAll(Validate.notNull(regions));
		return this;
	}

	public RegionCriteria inWorlds(World... worlds) {
		this.worlds = Optional.of(new EnhancedList<>(Validate.noNullElements(worlds)));
		return this;
	}

	public RegionCriteria byPredicates(Predicate<Region>... predicates) {
		this.predicates = Optional.of(new EnhancedList<>(Validate.noNullElements(predicates)));
		return this;
	}

	public RegionCriteria byTypes(RegionType... types) {
		this.types = Optional.of(new EnhancedList<>(Validate.noNullElements(types)));
		return this;
	}

	public RegionCriteria at(Vector3D... coords) {
		this.coords = Optional.of(new EnhancedList<>(Validate.noNullElements(coords)));
		return this;
	}

	public RegionList search() {
		if (regions.size() < 2) return regions;

		if (worlds.isPresent())
			forEach(region -> worlds.get().
					forEach(world -> regions.removeIf(
							!region.getWorld().equals(world),
							region
					))
			);

		if (predicates.isPresent())
			forEach(region -> predicates.get().
					forEach(predicate -> regions.removeIf(
							!predicate.test(region),
							region
					))
			);

		if (types.isPresent())
			forEach(region -> regions.removeIf(
					!types.get().contains(region.getType()),
					region
			));

		if (coords.isPresent())
			forEach(region -> coords.get()
					.forEach(coord -> regions.removeIf(
							!region.contains(coord),
							region
					))
			);

		return regions;
	}

	@Override
	public Iterator<Region> iterator() {
		return regions.iterator();
	}
}