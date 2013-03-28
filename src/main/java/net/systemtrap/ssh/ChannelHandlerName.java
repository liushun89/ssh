package net.systemtrap.ssh;

final class ChannelHandlerName {

    /**
     * The key to indentify the BinaryChunkSplitter.
     */
    static final String BINARY_CHUNK_SPLITTER_KEY =
            BinaryChunkSplitter.class.getSimpleName();

    /**
     * The key to indentify the VersionExchangeHandler.
     */
    static final String VERSION_EXCHANGE_HANDLER_KEY =
            VersionExchangeHandler.class.getSimpleName();

    /**
     * The key to indentify the VersionExchangeReader.
     */
    static final String VERSION_EXCHANGE_READER_KEY =
            VersionExchangeReader.class.getSimpleName();

    /**
     * The key to indentify the VersionExchangeWriter.
     */
    static final String VERSION_EXCHANGE_WRITER_KEY =
            VersionExchangeWriter.class.getSimpleName();
}
