package com.coen317.node;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Key {

    public static int generate(String name, int bits) {
		String sha1 = sha1(name);
		//int characters = (int) (Math.log(space)/Math.log(2));
        bits = Math.min(bits, sha1.length());
        //String sha1_new = org.apache.commons.codec.digest.DigestUtils.sha1Hex( name );
        //System.out.println("Compare: "+sha1_new);
		//System.out.println("sha1.length() = " + sha1.length() + ", sha1=" + sha1 + " bits=" + bits);
        int decimalKey = Integer.parseInt(sha1.substring(sha1.length()-bits, sha1.length()), 2);
        return decimalKey;
	}

    private static String sha1(String s) {
		String sha1 = null;
		try {
			MessageDigest mDigest = MessageDigest.getInstance("SHA1");
            byte[] result = mDigest.digest(s.getBytes());
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < result.length; i++) {
				sb.append(Integer.toString(result[i], 2).substring(1));
			}
			sha1 = sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return sha1;
	}

}
