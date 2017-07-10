/*
 * rv_core
 * 
 * Created on 10 July 2017 at 2:42 PM.
 */

package com.riddlesvillage.core.sign;

import com.riddlesvillage.core.player.CorePlayer;
import org.bukkit.block.Sign;

public interface SignClickListener {

    void onClick(final CorePlayer player,
                 final Sign sign);
}