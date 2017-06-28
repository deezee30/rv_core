package example;

import com.riddlesvillage.core.vpn.AntiVpnSites;

/**
 * Created by Matthew E on 6/14/2017.
 */
class AntiVpnUrlExample {

    public void test() {
        AntiVpnSites.IP_INTEL.getUrl().replace("%ipAddress%", "localhost");
        //Just an idea
    }
}
