/*
 * rv_core
 * 
 * Created on 03 June 2017 at 10:10 PM.
 */

package com.riddlesvillage.core.database.data;

import com.riddlesvillage.core.CoreSettings;
import com.riddlesvillage.core.database.StatType;
import com.riddlesvillage.core.player.Rank;

import java.util.Collections;

public enum DataInfo implements StatType {

	UUID		("_id",				null),
	NAME		("name",			null),
	NAME_HISTORY("nameHistory",		Collections.emptyList()),
	IP_HISTORY	("ipHistory", 		Collections.emptyList()),
	FIRST_LOGIN	("firstLogin",		null),
	LAST_LOGIN	("lastLogin", 		null),
	LAST_LOGOUT	("lastLogout", 		null),
	PLAYING		("playing", 		true),
	COINS		("coins",			0),
	TOKENS		("tokens",			0),
	RANK		("rank",			Rank.DEFAULT),
	PREMIUM		("premium",			false),
	LOCALE		("locale",			CoreSettings.DEFAULT_LOCALE);

	private final String stat;
	private final Object def;

	DataInfo(String stat, Object def) {
		this.stat = stat;
		this.def = def;
	}

	@Override
	public String getStat() {
		return stat;
	}

	@Override
	public Object getDefault() {
		return def;
	}

	@Override
	public String toString() {
		return stat;
	}
}