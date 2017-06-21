package com.riddlesvillage.core.world.region;

import com.riddlesvillage.core.CoreException;

public class ImproperSerializedRegionFormatException extends CoreException {

	public ImproperSerializedRegionFormatException(Region region) {
		super("The " + region.getType().toString() + " region contains a parsing problem.");
	}

	public ImproperSerializedRegionFormatException(Region region, String cause) {
		super("The " + region.getType().toString() + " region contains a parsing problem " + cause + ".");

	}
}
