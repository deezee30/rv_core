/*
 * RiddlesCore
 */

package com.riddlesvillage.core.player.profile;

import com.google.common.collect.ImmutableList;
import com.riddlesvillage.core.CoreException;
import com.riddlesvillage.core.Messaging;
import com.riddlesvillage.core.Core;
import com.riddlesvillage.core.database.Database;
import com.riddlesvillage.core.database.DatabaseAPI;
import com.riddlesvillage.core.database.data.DataInfo;
import com.riddlesvillage.core.player.CorePlayer;
import com.riddlesvillage.core.player.OfflineCorePlayer;
import com.riddlesvillage.core.player.statistic.*;
import com.riddlesvillage.core.service.timer.Timer;
import com.riddlesvillage.core.util.UUIDUtil;
import org.bson.Document;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * An abstract version of expanded {@link CoreProfile}.
 *
 * <p>This class should be used for any scenario where a
 * virtual server user is involved who is expected to be
 * interacted with a Mongo database, regardless whether
 * he could be online or offile.  This profile is designed
 * to store the fundamental data for online and offline
 * players, who are eligible for interaction with the
 * database.</p>
 *
 * <p>Provided that the sub class holds this data for
 * an online player, this class is expected to be instantiated
 * only once, per online player, per server.  Otherwise,
 * there is no limit on how many profiles can be made.</p>
 *
 * <p>Upon instantiation, the essential data is requested
 * from the database.  If the user was not found in the
 * database (or the database connection is closed), either
 * the {@link UUID} or name of the user can end up being
 * {@code null} (but not both).  This still allows offline
 * profiles to store it in some sort of cache.  An example
 * of profile caching system can be found in the sub class
 * {@link OfflineCorePlayer}.
 *
 * To check if the {@code UUID} and name of the player
 * are both real and thus making the user a valid user, simply
 * check with {@link #hasPlayed()}.</p>
 *
 * @see CoreProfile
 * @see CorePlayer
 * @see OfflineCorePlayer
 */
public abstract class AbstractCoreProfile implements StatisticHolder, PremiumHolder, CoinsHolder, TokensHolder, RankedPlayer {

	/*
	 * In case the player is offline and the UUID or name has
	 * not been found in the general database, either the UUID
	 * or name can result to being null, as it can be used to
	 * store for cache purposes.
	 *
	 * If both values are null, profile will not be generated.
	 */
	private volatile UUID uuid = null;
	private volatile String name = null;

	/**
	 * Used for timing this class.  Can be accessed and interacted
	 * with via sub classes.  It is recommented to stop the timer
	 * after all loading is done via {@link Timer#forceStop()}.
	 */
	protected volatile transient Timer
			timer			= new Timer();
	protected transient boolean
			played			= false;
	private transient StatisticHolder
			statHolder		= this;

	/**
	 * @see #AbstractCoreProfile(UUID, String)
	 */
	protected AbstractCoreProfile(UUID uuid) {
		this(uuid, null);
	}

	/**
	 * @see #AbstractCoreProfile(UUID, String)
	 */
	protected AbstractCoreProfile(String name) {
		this(null, name);
	}

	/**
	 * Instantiates a user instance used to store essential data.
	 *
	 * <p>Either the {@param uuid} or {@param name} provided
	 * can be {@code null} but not both.  This class attempts
	 * to find the user in the pre-defined database collection.
	 * If the database connection is not set up or the user
	 * has not been found, the profile data will not be loaded.</p>
	 *
	 * <p>As loading users or online players could potentially
	 * be quite a heavy task, this class times the load time for
	 * all instantiations of this class and the sub classes
	 * using {@link #timer}.  It is recommented to stop the timer
	 * in order for it to debug how many milliseconds it took to
	 * load all constructors.</p>
	 *
	 * @param   uuid
	 *          A {@code UUID} that the user may have.
	 * @param   name
	 *          A name that the user may have.
	 * @throws  CoreException
	 *          In case a database error occurs.
	 * @see     Timer
	 */
	protected AbstractCoreProfile(UUID uuid, String name) {
		if (uuid == null && name == null) {
			Messaging.log("Created a fake player but both ID and name are null");
			return;
		}

		this.uuid = uuid;
		this.name = name;

		// Record how long it takes to load the profile
		timer.start();

		// Called from subclass when load is complete
		timer.onFinishExecute(() -> Messaging.debug(
				"Generated player profile (%s) '%s' with ID '%s' in %sms",
				AbstractCoreProfile.this.getClass().getSimpleName(),
				AbstractCoreProfile.this.name,
				AbstractCoreProfile.this.uuid,
				timer.getTime(TimeUnit.MILLISECONDS)
		));

		DataInfo data = DataInfo.UUID;
		Object value = uuid;

		// if uuid is null, use name instead
		if (uuid == null) {
			data = DataInfo.NAME;
			value = name;
		}

		DatabaseAPI.retrieveDocument(Database.getMainCollection(), data, value, (document, throwable) -> {
			Core.logIf(throwable != null, "Error loading '%s' ('%s'):", name, uuid, throwable);

			Optional<Document> downloadedDoc = Optional.ofNullable(document);
			if (downloadedDoc.isPresent()) {
				played = true;

				this.name = document.getString(DataInfo.NAME.getStat());
				this.uuid = UUIDUtil.fromString(document.getString(DataInfo.UUID.getStat()));

				if (Database.getMainCollection().equals(getCollection()) || getCollection() == null) {
					// Apply already downloaded stats
					finishLoading(downloadedDoc);
				} else {
					// Async download custom stats from database
					refreshStats();
				}
			} else {
				// player never played before
				finishLoading(Optional.<Document>empty());
			}
		});
	}

	private void finishLoading(Optional<Document> document) {
		// finish loading on the main thread
		Bukkit.getScheduler().scheduleSyncDelayedTask(Core.get(), () -> {
			onLoad(document);
			if (isOnline()) played = true;
			timer.forceStop();
		});
	}

	protected abstract void onLoad(Optional<Document> document);

	protected void refreshStats() {
		// Async download custom stats from database
		DatabaseAPI.retrieveDocument(getCollection(), DataInfo.UUID, uuid, ((result, t) -> {
			Core.logIf(t != null, "Error loading '%s' ('%s'): %s", name, uuid, t);
			finishLoading(Optional.ofNullable(result));
		}));
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof CoreProfile && equals((CoreProfile) other);
	}

	public boolean equals(CoreProfile other) {
		return other != null && other.getName().equals(name);
	}

	// == User Data ====================================================== //

	@Override
	public final UUID getUuid() {
		return uuid;
	}

	@Override
	public final String getName() {
		return name;
	}

	@Override
	public final boolean hasPlayed() {
		return played;
	}

	// == Defaults ====================================================== //

	@Override
	public String getDisplayName() {
		CorePlayer player = toCorePlayer();
		if (player == null) {
			return (getUuid() == null ?
					OfflineCorePlayer.fromName(getName()) :
					OfflineCorePlayer.fromUuid(getUuid())
			).getDisplayName();
		} else {
			return player.getDisplayName();
		}
	}

	@Override
	public String getIp() {
		CorePlayer player = toCorePlayer();
		if (player == null) {
			return (getUuid() == null ?
					OfflineCorePlayer.fromName(getName()) :
					OfflineCorePlayer.fromUuid(getUuid())
			).getIp();
		} else {
			return player.getIp();
		}
	}

	@Override
	public List<String> getIpHistory() {
		CorePlayer player = toCorePlayer();
		if (player == null) {
			return (getUuid() == null ?
					OfflineCorePlayer.fromName(getName()) :
					OfflineCorePlayer.fromUuid(getUuid())
			).getIpHistory();
		} else {
			return player.getIpHistory();
		}
	}

	@Override
	public List<String> getNameHistory() {
		CorePlayer player = toCorePlayer();
		if (player == null) {
			return (getUuid() == null ?
					OfflineCorePlayer.fromName(getName()) :
					OfflineCorePlayer.fromUuid(getUuid())
			).getNameHistory();
		} else {
			return player.getNameHistory();
		}
	}

	// == Statistics ====================================================== //

	/**
	 * Sets the {@code StatisticHolder} to be used instead of
	 * the default one provided ({@link #getStatisticValues()}).
	 *
	 * @see StatisticHolder
	 */
	public final void setStatisticHolder(StatisticHolder statHolder) {
		this.statHolder = statHolder;
	}

	@Override
	public ImmutableList<String> getStatisticValues() {

		/*
		 * If statHolder equals to the current instance (this
		 * instance of CoreProfile), then return custom lines
		 * for statistics.  Otherwise let statHolder decide
		 * which statistics to return.
		 */
		if (!equals(statHolder)) {
			return statHolder.getStatisticValues();
		}

		if (!played) {
			try {
				throw new CoreException("Player " + name + " never played before!");
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}

		return new ImmutableList.Builder<String>()
				.add("~")
				.add("~&e======= " + getDisplayName() + " &e=======")
				.add("~&eRank: " + getRank().getDisplayName())
				.add("~&eCoins: &3" + getCoins())
				.add("~&eTokens: &3" + getTokens())
				.add("~&ePremium: " + (isPremium() ? "&2True" : "&4False"))
				.add("~&eCurrently " + (isOnline() ? "&2Online" : "&4Offline"))
				.add("~")
				.build();
	}
}