package com.bank.test;

import com.bank.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class atm2 {
    private static final Logger logger = LoggerFactory.getLogger(atm2.class);

    public static void main(String[] args) {
        Client atm = new Client("localhost",8091);
        atm.main(new String[0]);

    }


}