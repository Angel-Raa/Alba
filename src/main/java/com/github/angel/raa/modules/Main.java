package com.github.angel.raa.modules;

import com.github.angel.raa.modules.core.Server;

import java.io.IOException;
;

public class Main {

    public static void main(String[] args) throws IOException {

        Server server = new Server(8080);
        server.start();
    }
}
