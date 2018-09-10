package android.net.vpn;

import android.app.Dialog;
import android.net.vpn.VpnProfile;
import android.view.View;

/**
 * The interface to act on a {@link VpnProfile}.
 */
public interface VpnProfileActor {
    VpnProfile getProfile();


    /**
     * Establishes a VPN connection.
     * @param dialog the connect dialog
     */
    void connect(Dialog dialog);

    /**
     * Tears down the connection.
     */
    void disconnect();


    void checkStatus();
}