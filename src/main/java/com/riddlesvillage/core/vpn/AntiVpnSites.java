package com.riddlesvillage.core.vpn;

/**
 * Created by Matthew E on 6/14/2017.
 */
public enum AntiVpnSites {
    IP_INTEL("http://check.getipintel.net/check.php?ip=%ipAddress%&contact=%email%"),
    LEGACY_IP_HUB("http://legacy.iphub.info/api.php?ip=%ipAddress%&showtype=4");

    private String url;

    AntiVpnSites(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

}
