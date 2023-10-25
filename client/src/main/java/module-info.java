module ru.adel.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires io.netty.transport;
    requires io.netty.codec;
    requires static lombok;
    requires org.slf4j;
    requires org.slf4j.simple;
    requires cloud.storage.netty.common.commands;

    opens ru.adel.client to javafx.fxml;
    exports ru.adel.client;
    exports ru.adel.client.controller;
    opens ru.adel.client.controller to javafx.fxml;
    exports ru.adel.client.handler;
    opens ru.adel.client.handler to javafx.fxml;
    exports ru.adel.client.connection;
    opens ru.adel.client.connection to javafx.fxml;
}