package com.riddlesvillage.core.world.region;

import com.riddlesvillage.core.CoreException;
import com.riddlesvillage.core.region.Region;

public class ImproperSerializedRegionFormatException extends CoreException {

	public ImproperSerializedRegionFormatException(Region region) {
		super("The " + region.getType().toString().toLowerCase() + " region contains a parsing problem.");
	}

	public ImproperSerializedRegionFormatException(Region region, String cause) {
		super("The " + region.getType().toString().toLowerCase() + " region contains a parsing problem: " + cause);
	}
}
