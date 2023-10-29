module cloud.storage.netty.common.commands {
    requires static lombok;
    requires io.netty.transport;
    requires io.netty.codec;
    requires io.netty.buffer;

    exports ru.adel;
    exports ru.adel.command;
    exports ru.adel.decoder;
    exports ru.adel.encoder;
    exports ru.adel.encoder.impl;
    exports ru.adel.decoder.impl;
}