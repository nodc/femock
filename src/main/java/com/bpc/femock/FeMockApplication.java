package com.bpc.femock;

import com.bpc.femock.server.Iso8583Server;
import com.solab.iso8583.IsoMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FeMockApplication implements CommandLineRunner {

    @Autowired
    private Iso8583Server<IsoMessage> server;

	public static void main(String[] args) {
		SpringApplication.run(FeMockApplication.class, args);
	}

    @Override
    public void run(final String... strings) throws Exception {
        server.init();
        server.start();
    }
}