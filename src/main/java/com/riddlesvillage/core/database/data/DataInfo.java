/*
 * rv_core
 * 
 * Created on 03 June 2017 at 10:10 PM.
 */

package com.riddlesvillage.core.database.data;

import com.riddlesvillage.core.Core;
import com.riddlesvillage.core.database.StatType;
import com.riddlesvillage.core.player.Rank;

import java.util.Collections;
import java.util.Optional;

public enum DataInfo implements StatType {

    UUID        ("_id",			null),
    NAME        ("name",		null),
    NAME_HISTORY("nameHistory",	Collections.emptyList()),
    IP_HISTORY  ("ipHistory", 	Collections.emptyList()),
    FIRST_LOGIN ("firstLogin",	null),
    LAST_LOGIN  ("lastLogin", 	null),
    LAST_LOGOUT ("lastLogout", 	null),
    PLAYING     ("playing", 	true),
    COINS       ("coins",		0),
    TOKENS      ("tokens",		0),
    RANK        ("rank",		Rank.DEFAULT),
    PREMIUM     ("premium",		false),
    LOCALE      ("locale",		Core.getSettings().getDefaultLocale());

    private final String stat;
    private final Optional<Object> Default;

    DataInfo(final String stat,
             final Object Default) {
        this.stat = stat;
        this.Default = Optional.ofNullable(Default);
    }

    @Override
    public String toString() {
        return stat;
    }

    @Override
    public String getStat() {
        return stat;
    }

    @Override
    public Optional<Object> getDefault() {
        return Default;
    }
}