/*
 * RiddlesCore
 */

package com.riddlesvillage.core.player.profile;

import com.riddlesvillage.core.collect.EnhancedList;
import com.riddlesvillage.core.player.CorePlayer;
import com.riddlesvillage.core.player.CorePlayerList;
import com.riddlesvillage.core.player.OfflineCorePlayer;

import java.util.Collection;
import java.util.List;

/**
 * Delegate for {@code EnhancedList<AbstractCoreProfile>}
 *
 * @see EnhancedList
 * @see AbstractCoreProfile
 * @see CorePlayerList
 */
public class CoreProfileList extends EnhancedList<AbstractCoreProfile> {

    public CoreProfileList() {}

    public CoreProfileList(int initialCapacity) {
        super(initialCapacity);
    }

    public CoreProfileList(AbstractCoreProfile... elements) {
        super(elements);
    }

    public CoreProfileList(Collection<? extends AbstractCoreProfile> c) {
        super(c);
    }

    /**
     * Returns a list of online/offline user profiles found by their names.
     *
     * <p>Generates a {@link this} list of user profiles in either
     * their online form ({@link CorePlayer})
     * if they are online or their offline profile form ({@link
     * OfflineCorePlayer}) if they are offline.</p>
     *
     * @param   serializedString
     *          A String in the form of anything generated by {@link
     *          List#toString()}.
     * @return  A list of online or offline user profiles.
     * @throws  IllegalArgumentException
     *          In case the serialized String is not in appropriate form.
     * @throws  com.riddlesvillage.core.CoreException
     *          In case a database error occurs upon instantiating any
     *          of the {@link AbstractCoreProfile}s (if needed).
     * @see     OfflineCorePlayer#fromName(String)
     * @see     AbstractCoreProfile
     */
    public static CoreProfileList from(String serializedString) {
        if (!serializedString.startsWith("["))
            throw new IllegalArgumentException(
                    "Serialized String must be in the form of List#toString(), not " + serializedString);

        CoreProfileList players = new CoreProfileList();
        if (!serializedString.equals("[]")) {
            for (String player : serializedString
                    .substring(1)
                    .substring(0, serializedString.length() - 2)
                    .split(",")) {
                players.add(OfflineCorePlayer.fromName(player));
            }
        }

        return players;
    }
}