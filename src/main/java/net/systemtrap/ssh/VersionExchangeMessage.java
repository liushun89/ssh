package net.systemtrap.ssh;

import com.google.common.base.Optional;

public interface VersionExchangeMessage {

    Optional<String> comments();

    /**
     * Determine if the version exchange message is valid towards the transport
     * protocol specification.
     *
     * @return
     *   message is valid
     */
    boolean isValid();

    String softwareVersion();

}