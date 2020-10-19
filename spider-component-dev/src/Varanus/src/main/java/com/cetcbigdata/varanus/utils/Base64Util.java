package com.cetcbigdata.varanus.utils;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * @program: service-zhiwen-es
 * @description: base64加解密
 * @author: Robin
 * @create: 2018-10-17 09:49
 **/
public class Base64Util {

    public static void main(String[] args) {
        System.out.println(Base64Util.decoder("dGhlbWVfaW5mbzrop4TojIPmgKfmlofku7YyMDA4"));
    }

    public static String encoder(String value) {
        BASE64Encoder encoder = new BASE64Encoder();
        try {
            return encoder.encode(value.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String decoder(String value) {
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            return new String(decoder.decodeBuffer(value), "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}