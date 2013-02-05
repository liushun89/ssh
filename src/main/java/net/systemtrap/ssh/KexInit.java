package net.systemtrap.ssh;

import io.netty.buffer.ByteBuf;

public interface KexInit {

    ByteBuf cookie();

    NameList kexAlgorithms();

    NameList serverHostKeyAlgorithms();
    
    NameList encryptionAlgorithmsClientToServer();

    NameList encryptionAlgorithmsServerToClient();

    NameList macAlgorithmsClientToServer();

    NameList macAlgorithmsServerToClient();

    NameList compressionAlgorithmsClientToServer();

    NameList compressionAlgorithmsServerToClient();

    NameList languagesClientToServer();

    NameList languagesServerToClient();

    boolean  firstKexPacketFollows();

    int      extension();
}
