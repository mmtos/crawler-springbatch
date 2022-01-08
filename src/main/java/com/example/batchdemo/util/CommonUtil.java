package com.example.batchdemo.util;

import org.apache.commons.codec.binary.Hex;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CommonUtil {

	public static String encodeByMD5(String original){
		String add = "sejong";
		String beforeEncoded = original + add;

		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
			md.update(beforeEncoded.getBytes());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return Hex.encodeHexString(md.digest());
	}
}
