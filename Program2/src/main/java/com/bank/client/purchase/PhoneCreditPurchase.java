package com.bank.client.purchase;

import com.bank.Util.CommonUtil;
import com.bank.Util.ISOUtil;
import com.bank.Util.MQUtil;
import com.bank.client.Client;
import com.bank.client.ClientHelper;
import com.bank.client.interfaceClient.Purchase;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.GenericPackager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PhoneCreditPurchase implements Purchase {
    private static final Logger logger = LoggerFactory.getLogger(PhoneCreditPurchase.class);
    private String phoneNumber;
    private String name;
    private int amount;
    private String accountNumber;
    private String pinNumber;
    private String productCode;
    private ISOUtil isoUtil = new ISOUtil();

    public PhoneCreditPurchase(String phoneNumber, int amount, String productCode) {
        this.amount = amount;
        this.phoneNumber = phoneNumber;
        this.productCode = productCode;
    }

    @Override
    public String purchaseInquiry(String accountNumber, String pinNumber) {
        this.accountNumber = accountNumber;
        this.pinNumber = pinNumber;

        String message = buildInquiryISO();
//        String response = ClientHelper.sendData(message, "http://localhost:8080/purchase/phoneCredit/inquiry");

        MQUtil mqUtil = new MQUtil();
        mqUtil.sendToExchange("mainExchange", message);
        String response = CommonUtil.receiveFromSocket(Integer.parseInt(Client.getPort()));
        return response;
    }

    @Override
    public String purchase(String message) {
        message = buildISO(message);
//        String response = ClientHelper.sendData(message, "http://localhost:8080/purchase/phoneCredit/process");

        MQUtil mqUtil = new MQUtil();
        mqUtil.sendToExchange("mainExchange",message);
        String response = CommonUtil.receiveFromSocket(Integer.parseInt(Client.getPort()));

        return response;
    }

    private String buildInquiryISO(){
        try {
            InputStream is = getClass().getResourceAsStream("/fields.xml");
            GenericPackager packager = new GenericPackager(is);

            ISOMsg isoMsg = new ISOMsg();
            isoMsg.setPackager(packager);
            isoMsg.setMTI("0200");

            isoMsg.set(2, accountNumber);
            isoMsg.set(3, "381000");
            isoMsg.set(4, amount + "");
            isoMsg.set(7, new SimpleDateFormat("MMddHHmmss").format(new Date()));
            isoMsg.set(11, "000001");
            isoMsg.set(12, new SimpleDateFormat("HHmmss").format(new Date()));
            isoMsg.set(13, new SimpleDateFormat("MMdd").format(new Date()));
            isoMsg.set(15, new SimpleDateFormat("MMdd").format(new Date()));
            isoMsg.set(18, "0000");
            isoMsg.set(32, productCode);
            isoMsg.set(33, "00000000000");
            isoMsg.set(37, "000000000000");
            isoMsg.set(41, "12340001");
            isoMsg.set(42, "000000000000000");
            isoMsg.set(43, "0000000000000000000000000000000000000000");
            isoMsg.set(48, "0");
            isoMsg.set(49, "360");
            isoMsg.set(54, Client.getServer()+":"+Client.getPort());
            isoMsg.set(52, pinNumber);
            isoMsg.set(62, "0");
            isoMsg.set(102, "1234567890");

            byte[] result = isoMsg.pack();
            return new String(result);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }
    private String buildISO(String message){
        ISOMsg isoMessage = isoUtil.stringToISO(message);
        try {
            InputStream is = getClass().getResourceAsStream("/fields.xml");
            GenericPackager packager = new GenericPackager(is);

            ISOMsg isoMsg = new ISOMsg();
            isoMsg.setPackager(packager);
            isoMsg.setMTI("0200");

            isoMsg.set(2, isoMessage.getString(2));
            isoMsg.set(3, "181000");
            isoMsg.set(4, isoMessage.getString(4));
            isoMsg.set(7, new SimpleDateFormat("MMddHHmmss").format(new Date()));
            isoMsg.set(11, "000001");
            isoMsg.set(12, new SimpleDateFormat("HHmmss").format(new Date()));
            isoMsg.set(13, new SimpleDateFormat("MMdd").format(new Date()));
            isoMsg.set(15, new SimpleDateFormat("MMdd").format(new Date()));
            isoMsg.set(18, "0000");
            isoMsg.set(32, isoMessage.getString(32));
            isoMsg.set(33, "00000000000");
            isoMsg.set(37, "000000000000");
            isoMsg.set(41, "12340001");
            isoMsg.set(42, "000000000000000");
            isoMsg.set(43, "0000000000000000000000000000000000000000");
            isoMsg.set(48, "0");
            isoMsg.set(49, "360");
            isoMsg.set(54, isoMessage.getString(54));
            isoMsg.set(62, isoMessage.getString(62));
            isoMsg.set(63, "0");
            isoMsg.set(102, isoMessage.getString(102));

            byte[] result = isoMsg.pack();
            return new String(result);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
