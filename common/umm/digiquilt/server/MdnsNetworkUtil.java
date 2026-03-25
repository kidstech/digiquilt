package umm.digiquilt.server;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import javax.jmdns.JmDNS;

/**
 * Utility methods for creating JmDNS instances on modern network stacks.
 */
public final class MdnsNetworkUtil {

    private MdnsNetworkUtil() {
        // Utility class
    }

    /**
     * Create JmDNS bound to a preferred multicast-capable interface when
     * possible, with fallback to default interface selection.
     *
     * @return a JmDNS instance
     * @throws IOException if JmDNS cannot be created
     */
    public static JmDNS createJmDNS() throws IOException {
        InetAddress preferredAddress = findPreferredMdnsAddress();

        if (preferredAddress != null) {
            try {
                return JmDNS.create(preferredAddress);
            } catch (IOException e) {
                // Fall back to default interface selection if explicit binding
                // fails on this machine.
                System.err.println("Could not bind mDNS to "
                        + preferredAddress + "; falling back to default.");
            }
        }

        return JmDNS.create();
    }

    /**
     * Find a multicast-capable non-loopback address suitable for JmDNS.
     *
     * @return preferred address, or null if none could be found
     */
    static InetAddress findPreferredMdnsAddress() {
        InetAddress fallbackAddress = null;

        try {
            Enumeration<NetworkInterface> interfaces =
                NetworkInterface.getNetworkInterfaces();

            if (interfaces == null) {
                return null;
            }

            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();

                if (!networkInterface.isUp()
                        || networkInterface.isLoopback()
                        || !networkInterface.supportsMulticast()) {
                    continue;
                }

                Enumeration<InetAddress> addresses =
                    networkInterface.getInetAddresses();

                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();

                    if (address.isLoopbackAddress()
                            || address.isAnyLocalAddress()
                            || address.isLinkLocalAddress()) {
                        continue;
                    }

                    // Prefer site-local IPv4 addresses because they are the
                    // most common and stable choice for local mDNS traffic.
                    if (address instanceof Inet4Address
                            && address.isSiteLocalAddress()) {
                        return address;
                    }

                    if (fallbackAddress == null) {
                        fallbackAddress = address;
                    }
                }
            }
        } catch (SocketException e) {
            return null;
        }

        return fallbackAddress;
    }
}