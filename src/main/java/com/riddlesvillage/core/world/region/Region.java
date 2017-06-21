/*
 * MaulssLib
 * 
 * Created on 07 February 2015 at 8:21 PM.
 */

package com.riddlesvillage.core.world.region;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.reflect.TypeToken;
import com.riddlesvillage.core.RiddlesCore;
import com.riddlesvillage.core.collect.EnhancedList;
import com.riddlesvillage.core.packet.wrapper.WrapperPlayServerBlockChange;
import com.riddlesvillage.core.player.CorePlayer;
import com.riddlesvillage.core.world.FlooredVectorLoop;
import com.riddlesvillage.core.world.Vector3D;
import com.riddlesvillage.core.world.region.flag.Flag;
import com.riddlesvillage.core.world.region.flag.FlagMap;
import com.riddlesvillage.core.world.region.type.RegionType;
import org.apache.commons.lang3.Validate;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONAware;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Used as a three-dimensional set of three-dimensional {@link Vector3D}s
 * that provides essential functions for working with points inside the
 * geometrically symmetrical region.
 *
 * <p>Since {@link Vector3D} consists of (x, y, z) {@code double} values,
 * it can contain any real number, meaning the depth of the region can be
 * infinite, except it's not possible to do that virtually.  This doesn't
 * necessarily mean the points in the region cannot be whole numbers.
 *  However all recorded <b>points</b> that can be found in this region instance
 * will always be {@code int}s, since it's impossible to work with {@code
 * double}s like that.  All points in the region dynamically become floored
 * using {@link Vector3D#getFloorX()}, {@link Vector3D#getFloorY()} and {@link
 * Vector3D#getFloorZ()}.</p>
 *
 * <p>Of course, an implementation of this three-dimensional region can
 * make use of its functions while still working with a two-dimensional
 * region, simply by keeping all {@code y} coordinates the same.</p>
 *
 * @author  Maulss
 * @since 	JDK 1.8
 * @see 	Cloneable
 * @see 	Iterable<Vector3D>
 * @see		Vector3D
 * @see		FlooredVectorLoop
 */
public abstract class Region implements
		Comparable<Region>, Cloneable, Serializable,
		Iterable<Vector3D>, ConfigurationSerializable, JSONAware {

	private static final long serialVersionUID = 3272199880401532227L;

	private final String world;
	private final FlagMap flags = new FlagMap();
	private final int priority;

	protected transient int minX;
	protected transient int minY;
	protected transient int minZ;
	protected transient int maxX;
	protected transient int maxY;
	protected transient int maxZ;

	protected Region(String world) {
		this(world, -1);
	}

	protected Region(String world, int priority) {
		this(world, Maps.newHashMap(), priority);
	}

	protected Region(String world, Map<Flag, Boolean> flags) {
		this(world, flags, -1);
	}

	protected Region(String world, Map<Flag, Boolean> flags, int priority) {
		this.world = Validate.notNull(world);
		if (flags != null) this.flags.putAll(flags);
		this.priority = priority;
	}

	public abstract void calculate();

	public void init() {
		calculate();

		minX = Math.min(
				getMinBounds().getFloorX(),
				getMaxBounds().getFloorX()
		);

		minY = Math.min(
				getMinBounds().getFloorY(),
				getMaxBounds().getFloorY()
		);

		minZ = Math.min(
				getMinBounds().getFloorZ(),
				getMaxBounds().getFloorZ()
		);

		maxX = Math.max(
				getMinBounds().getFloorX(),
				getMaxBounds().getFloorX()
		);

		maxY = Math.max(
				getMinBounds().getFloorY(),
				getMaxBounds().getFloorY()
		);

		maxZ = Math.max(
				getMinBounds().getFloorZ(),
				getMaxBounds().getFloorZ()
		);
	}

	/**
	 * @return The volume of the region in {@code int} units squared form.
	 */
	public abstract int getVolume();

	/**
	 * @return  The lowest corner of how the region was rendered, no matter
	 * 			if it's inside or outside the actual (non) polygonal region.
	 */
	public abstract Vector3D getMinBounds();

	/**
	 * @return 	The highest corner of how the region was rendered, no matter
	 * 			if it's inside or outside the actual (non) polygonal region.
	 */
	public abstract Vector3D getMaxBounds();

	public int getMinBoundX() {
		return minX;
	}

	public int getMinBoundY(){
		return minY;
	}

	public int getMinBoundZ() {
		return minZ;
	}

	public int getMaxBoundX() {
		return maxX;
	}

	public int getMaxBoundY() {
		return maxY;
	}

	public int getMaxBoundZ() {
		return maxZ;
	}

	/**
	 * Checks if a {@link Vector3D} point is actually located inside the region
	 * (set of {@code Vector}s).
	 *
	 * <p><b>NB:</b> The {@code Vector} should be checked to be inside the
	 * polygonal or non-plogynal region itself, instead of checking the bounds.
	 *  That means that if the region was a {@code SphericalRegion} object,
	 * then the check would involve checking if the point is inside the
	 * sphere, instead of the bounds encasing it in the form of a cuboid.</p>
	 *
	 * @param 	vector
	 * 			The vector to check whether it's inside the region
	 * @return 	True if the point is inside of the region.  False if otherwise
	 */
	public abstract boolean contains(Vector3D vector);

	/**
	 * Checks if a different {@link Region} object contains within this region
	 * by making sure all points in {@param region} are also points of the region
	 * in {@code this} instance.
	 *
	 * <p>For {@param region} to be a sub region of {@code this} region, the
	 * {@link #getVolume()} of {@code this} region must definitely be bigger or
	 * equal to {@param region}'s {@link Region#getVolume()}</p>
	 *
	 * @param   region
	 * 			The region to check whether it intersects this super region
	 * @return  True if {@param region} is inside {@code this} region.  False if otherwise
	 * @see		#contains(Vector3D)
	 */
	public synchronized boolean contains(Region region) {
		if (equals(region)) return true;
		if (!world.equals(region.world)) return false;

		// check if there are any points outside of the region
		for (Vector3D point : this) {
			if (!contains(point)) return false;
		}

		return true;
	}

	/**
	 * Checks if a different {@link Region} object intersects or collides with this
	 * region by making a check to see if at least a single point in {@param region}
	 * has the same coordinates as {@code this} region.
	 *
	 * @param   region
	 * 			The region to check whether it intersects this region
	 * @return  True if {@param region} collides with {@code this} region.  False if otherwise
	 * @see     #contains(Vector3D)
	 */
	public synchronized boolean intersects(Region region) {
		if (equals(region)) return true;
		if (!world.equals(region.world)) return false;

		// check if any point is in region
		for (Vector3D point : this) {
			if (contains(point)) return true;
		}

		return false;
	}

	/**
	 * Fill the region with a certain type of {@link Material}
	 * by looping through the X axis, the Y axis and the Z axis.
	 *
	 * @param material The material to fill the region with.
	 * @return The amount of blocks that have been changed.
	 * @see Material
	 */
	public final synchronized int fill(Material material) {
		return fill(material, 1f);
	}

	/**
	 * Fill the region with a certain type of {@link Material}
	 * by looping through the X axis, the Y axis and the Z axis,
	 * randomly.
	 *
	 * The chance of block being changed will have to be in
	 * {@code float} form, and between {@code 0} and {@code 1}.
	 *
	 * @param material The material to fill the region with.
	 * @param chance   The chance of the block being changed
	 * @return The amount of blocks that have been changed.
	 * @see Material
	 */
	public final synchronized int fill(Material material, float chance) {
		return fill(getBlocks(), material, chance);
	}

	/**
	 * Fill the walls of the region with a certain type of {@link Material}
	 * by looping through the X axes, the Y axes and the Z axes.
	 *
	 * @param material The material to fill the walls with.
	 * @return The amount of blocks that have been changed.
	 * @see Material
	 */
	public final synchronized int fillWalls(Material material) {
		return fillWalls(material, 1f);
	}

	/**
	 * Fill the walls of the region with a certain type of {@link Material}
	 * by looping through the X axes, the Y axes and the Z axes.
	 *
	 * The chance of block being changed will have to be in
	 * {@code float} form, and between {@code 0} and {@code 1}.
	 *
	 * @param material The material to fill the walls with.
	 * @param chance   The chance of the block being changed
	 * @return The amount of blocks that have been changed.
	 * @see Material
	 */
	public final synchronized int fillWalls(Material material, float chance) {
		return fill(getWallBlocks(), material, chance);
	}

	/**
	 * Fill the edges of the region with a certain type of {@link Material}
	 * by looping through the X axes, the Y axes and the Z axes.
	 *
	 * @param material The material to fill the edges with.
	 * @return The amount of blocks that have been changed.
	 * @see Material
	 */
	public final synchronized int fillEdges(Material material) {
		return fillEdges(material, 1f);
	}

	/**
	 * Fill the edges of the region with a certain type of {@link Material}
	 * by looping through the X axes, the Y axes and the Z axes.
	 *
	 * The chance of block being changed will have to be in
	 * {@code float} form, and between {@code 0} and {@code 1}.
	 *
	 * @param material The material to fill the edges with.
	 * @param chance   The chance of the block being changed
	 * @return The amount of blocks that have been changed.
	 * @see Material
	 */
	public final synchronized int fillEdges(Material material, float chance) {
		return fill(getEdgeBlocks(), material, chance);
	}

	/**
	 * Get all the {@link Block}s in the entire region.
	 *
	 * @return The blocks that were found in the region.
	 * @see Block
	 */
	public final synchronized EnhancedList<Block> getBlocks() {
		return toBlocks(getPoints());
	}

	/**
	 * Gets all the points in the walls of all sides of the of region.
	 *
	 * @return The blocks that were found in the walls of the region.
	 * @see Vector3D
	 */
	public abstract EnhancedList<Vector3D> getWalls();

	/**
	 * Gets all the {@link Block}s in the walls of all sides of the of region.
	 *
	 * @return The blocks that were found in the walls of the region.
	 * @see Block
	 */
	public final synchronized EnhancedList<Block> getWallBlocks() {
		return toBlocks(getWalls());
	}

	/**
	 * Gets all the points in the edges of all sides of the of region.
	 *
	 * @return The blocks that were found in the edges of the region.
	 * @see Vector3D
	 */
	public abstract EnhancedList<Vector3D> getEdges();

	/**
	 * Gets all the {@link Block}s in the edges of all sides of the of region.
	 *
	 * @return The blocks that were found in the edges of the region.
	 * @see Block
	 */
	public final synchronized EnhancedList<Block> getEdgeBlocks() {
		return toBlocks(getEdges());
	}

	/**
	 * Adds a flag to the region
	 *
	 * @param flag The registered flag to add
	 * @return This region instance
	 */
	public final synchronized Region addFlag(Flag flag, boolean allow) {
		if (Validate.notNull(flag).isAllowed() == allow) return this;

		if (flags.containsKey(flag)) {
			if (flags.get(flag) != allow) {
				flags.put(flag, allow);
			}
		} else {
			flags.put(flag, allow);
		}

		return this;
	}

	public ImmutableMap<Flag, Boolean> getFlags() {
		return flags.getImmutableEntries();
	}

	public final synchronized boolean isAllowed(Flag flag) {
		return flags.isAllowed(flag);
	}

	public final synchronized int getPriority() {
		return priority;
	}

	public final synchronized boolean hasPriority() {
		return priority != -1;
	}

	public final synchronized boolean hasPriorityOver(Region other) {
		if (!hasPriority()) return false;

		Optional<Region> priority = Regions.getPrioritized(this, other);

		return priority.isPresent() && priority.get().equals(this);

	}

	/**
	 * Show the region to specific {@link CorePlayer}s using
	 * fake packets to update the blocks client side.
	 *
	 * @param material The material to use to display the corners.
	 * @param delay   The time in seconds to show this region.
	 * @param players The players to show this to.
	 * @see org.bukkit.entity.Player#sendBlockChange(Location,
	 * org.bukkit.Material, byte)
	 */
	public synchronized void showEdges(Material material, long delay, CorePlayer... players) {
		sendBlockUpdates(material, 0, players);

		new BukkitRunnable() {

			@Override
			public void run() {
				sendBlockUpdates(null, -1, players);
			}
		}.runTaskLater(RiddlesCore.getInstance(), delay);
	}

	private synchronized void sendBlockUpdates(Material material, int data, CorePlayer... players) {
		for (Block block : getEdgeBlocks()) {

			if (material == null) material = block.getType();
			if (data < 0) data = block.getData();

			Location loc = block.getLocation();
			int x = loc.getBlockX();
			int y = loc.getBlockY();
			int z = loc.getBlockZ();

			for (CorePlayer player : players) {
				WrapperPlayServerBlockChange packet = new WrapperPlayServerBlockChange();

				packet.setLocation(new BlockPosition(x, y, z));
				packet.setBlockData(WrappedBlockData.createData(material, data));

				packet.sendPacket(player.getPlayer());
			}
		}
	}

	public final String getWorld() {
		return world;
	}

	public abstract ImmutableList<Vector3D> getPoints();

	public abstract RegionType getType();

	public synchronized List<Chunk> getChunks() {
		List<Chunk> res = new ArrayList<>();

		World w = Bukkit.getWorld(world);

		int x1 = (int) getMinBounds().getX() & ~0xf;
		int x2 = (int) getMaxBounds().getX() & ~0xf;
		int z1 = (int) getMinBounds().getZ() & ~0xf;
		int z2 = (int) getMaxBounds().getZ() & ~0xf;

		for (int x = x1; x <= x2; x += 16) {
			for (int z = z1; z <= z2; z += 16) {
				res.add(w.getChunkAt(x >> 4, z >> 4));
			}
		}

		return res;
	}

	/**
	 * @return  The average center point in this region.  Obtained by checking
	 * 			the {@link #getMinBounds()} and {@link #getMaxBounds()}
	 * @see 	Vector3D#get3DCentroid(Vector3D...)
	 */
	public final synchronized Vector3D getCentroid() {
		return Vector3D.get3DCentroid(getMinBounds(), getMaxBounds());
	}

	/**
	 * @return	A completely random point within the region.  The value
	 * 			returned here must strictly return {@code true} if used in
	 * 			{@link #contains(Vector3D)}
	 * @see		EnhancedList#getRandomElement()
	 */
	public final synchronized Vector3D getRandomPoint() {
		return new EnhancedList<>(getPoints()).getRandomElement();
	}

	public final synchronized int getMinX() {
		return minX;
	}

	public final int getMinY() {
		return minY;
	}

	public final int getMinZ() {
		return minZ;
	}

	public final int getMaxX() {
		return maxX;
	}

	public final int getMaxY() {
		return maxY;
	}

	public final int getMaxZ() {
		return maxZ;
	}

	/**
	 * Loops through every point ({@link Vector3D}) in this region and returns
	 * them for use.
	 *
	 * <p>  This method was specifically built for accessing stored
	 * points inside a region which would work as follows:
	 * {@code
	 * final List{@literal <}Vector{@literal >} points = new ArrayList<>(regionObject.getVolume());
	 * regionObject.loop(new FlooredVectorLoop() {
	 *
	 *     {@literal @}Override
	 *     public void loop(Vector vector) {
	 *         points.add(vector);
	 *     }
	 * });
	 * }
	 * Or if using {@code Java 1.8}, lamda expressions can simplify this
	 * even more: {@code regionObject.loop(points :: add);}</p>
	 *
	 * @param   loop
	 * 			The runnable that runs code for each vector returned
	 * @see		FlooredVectorLoop
	 */
	public final synchronized void loop(FlooredVectorLoop loop) {
		Validate.notNull(loop);

		for (Vector3D point : this) {
			loop.loop(point);
		}
	}

	/**
	 * Joins {@code this} region with {@param other} region provided they suffice
	 * the requirements to be joined:
	 * <ul>
	 *     <li>Both regions must be intersecting each other, not just touching.</li>
	 *     <li>One region can not be inside the other.</li>
	 *     <li>If one region {@link #equals(Object)} the other, first region is returned.</li>
	 * </ul>
	 *
	 * <p>The returned region will have a volume of {@code v1 + v2 - v3} - {@code v1}
	 * being the volume of region 1, {@code v2} being the volume of region 2 and
	 * {@code v3} being the volume of the intersecting part of both regions.</p>
	 *
	 * <p>The returned region will have its minimum bounds set to the minimum
	 * value taken from both bounds using {@link Vector3D#getMinimum(Vector3D , Vector3D)}
	 * and will have its maximum bounds set to the maximum value taken from both
	 * bounds using {@link Vector3D#getMaximum(Vector3D , Vector3D)}.</p>
	 *
	 * @param 	other
	 * 			The other region to join {@code this} region with.
	 * @return	The new colliding {@link Region} object that contains the points
	 * 			of both regions.  If both regions equal to each other, {@code this}
	 * 			instance is returned.
	 * @throws 	RegionBoundsException
	 *			If either {@code this} region or {@param other} is inside one
	 *			another.  This is checked by {@link #contains(Region)}.
	 * @throws  RegionBoundsException
	 * 			If the regions are separated, ie; both regions have to intersect
	 * 			(collide) with each other in order to be joined.  Otherwise these
	 * 			two regions are two separate regions and should be kept that way.
	 * @see		#getMinBounds()
	 * @see		#getMaxBounds()
	 */
	@Beta
	public abstract Region joinWith(Region other) throws RegionBoundsException;

	@Override
	public final synchronized Iterator<Vector3D> iterator() {
		return getPoints().iterator();
	}

	public final synchronized String toJson() {
		return Regions.REGION_GSON.toJson(this, type());
	}

	@Override
	public final synchronized String toJSONString() {
		return toJson();
	}

	@Override
	public synchronized String toString() {
		return toJson();
	}

	@Override
	public final synchronized boolean equals(Object o) {
		return this == o
				|| !(o == null
				|| getClass() != o.getClass())
				&& toString().equals(o.toString());
	}

	@Override
	public final synchronized int hashCode() {
		return Objects.hash(toString());
	}

	@Override
	public final synchronized int compareTo(Region o) {
		return o.priority - priority;
	}

	private EnhancedList<Block> toBlocks(List<Vector3D> points) {
		World world = Bukkit.getWorld(this.world);
		return points
				.stream()
				.map(vec -> world.getBlockAt(vec.toLocation(world)))
				.collect(Collectors.toCollection(EnhancedList::new));
	}

	private static int fill(List<Block> blocks, Material material, float chance) {
		int changed = 0;

		for (Block block : blocks) {
			if (Math.random() <= chance && !block.getType().equals(material)) {
				block.setType(material);
				changed++;
			}
		}

		return changed;
	}

	public static <R extends Region> Type type() {
		return new TypeToken<R>() {}.getType();
	}
}