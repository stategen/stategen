/*
 * Copyright (C) 2018  niaoge<78493244@qq.com>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.stategen.framework.util;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * The Class RSAUtil.
 */
public class RSAUtil {
    final static org.slf4j.Logger logger   = org.slf4j.LoggerFactory.getLogger(RSAUtil.class);

    private static String         RSA_NAME = "RSA";
    public static final String RSA_KEYPAIR_PUBLIC    = "RSA_KEYPAIR_PUBLIC";
    public static final String RSA_KEYPAIR_PRIVATE   = "RSA_KEYPAIR_PRIVATE";
    public static final String RSA_KEYPAIR = "RSA_KEYPAIR"; 
    //每天过期1次
    public static final long RSA_KEYPAIR_SECONDS = 3600*24L;
        

    private static Cipher getRSACipher() throws NoSuchAlgorithmException, NoSuchPaddingException {
        Cipher cipher = Cipher.getInstance(RSA_NAME);
        return cipher;
    }

    /**
     * 生成密钥对
     * @param filePath 生成密钥的路径
     * @return
     */
    public static RSAKeyPair generateKeyPair() {
        try {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(RSA_NAME);
            // 密钥位数
            keyPairGen.initialize(1024);
            // 密钥对
            KeyPair keyPair = keyPairGen.generateKeyPair();
            return new RSAKeyPair(keyPair);
        } catch (Exception e) {
            logger.error(
                new StringBuilder("在运行时产生错误信息,此错误信息表示该相应方法已将相关错误catch了，请尽快修复!\n以下是具体错误产生的原因:").append(e.getMessage()).append(" \n").toString(), e);
        }
        return null;
    }

    /**
     * 得到公钥
     * 
     * @param key
     *            密钥字符串（经过base64编码）
     * @throws Exception
     */
    public static PublicKey getPublicKey(String key) {
        byte[] keyBytes;
        keyBytes = Base64Util.decodeFromString(key);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_NAME);
            PublicKey publicKey = keyFactory.generatePublic(keySpec);
            return publicKey;
        } catch (NoSuchAlgorithmException e) {
            logger.error("", e);
        } catch (InvalidKeySpecException e) {
            logger.error("", e);
        }
        return null;
    }

    /**
     * 得到私钥
     * 
     * @param key
     *            密钥字符串（经过base64编码）
     * @throws Exception
     */
    public static PrivateKey getPrivateKey(String key) {
        byte[] keyBytes;
        keyBytes = Base64Util.decodeFromString(key);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_NAME);
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
            return privateKey;
        } catch (NoSuchAlgorithmException e) {
            logger.error("", e);
        } catch (InvalidKeySpecException e) {
            logger.error("", e);
        }
        return null;
    }

    /**
     * 使用公钥对明文进行加密，返回BASE64编码的字符串
     * @param publicKey
     * @param plainText
     * @return
     * @throws NoSuchPaddingException 
     * @throws NoSuchAlgorithmException 
     * @throws InvalidKeyException 
     * @throws BadPaddingException 
     * @throws IllegalBlockSizeException 
     */
    public static String encrypt(PublicKey publicKey, String plainText) {
        try {
            Cipher cipher = getRSACipher();
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] enBytes = cipher.doFinal(plainText.getBytes());
            return Base64Util.encodeToString(enBytes);
        } catch (InvalidKeyException e) {
            logger.error("", e);
        } catch (NoSuchAlgorithmException e) {
            logger.error("", e);
        } catch (NoSuchPaddingException e) {
            logger.error("", e);
        } catch (IllegalBlockSizeException e) {
            logger.error("", e);
        } catch (BadPaddingException e) {
            logger.error("", e);
        }
        return null;
    }

    /**
     * 使用keystore对明文进行加密
     * @param publicKeystore 公钥
     * @param plainText      明文
     * @return
     * @throws Exception 
     */
    public static String encrypt(String publicKeyString, String plainText) throws Exception {
        PublicKey publicKey = getPublicKey(publicKeyString);
        if (publicKey != null) {
            return encrypt(publicKey, plainText);
        }
        return null;
    }

    /**
     * 使用私钥对明文密文进行解密
     * @param privateKey
     * @param base64String
     * @return
     * @throws InvalidKeyException 
     * @throws NoSuchPaddingException 
     * @throws NoSuchAlgorithmException 
     * @throws BadPaddingException 
     * @throws IllegalBlockSizeException 
     */
    public static String decrypt(PrivateKey privateKey, String base64String) {
        try {
            Cipher cipher = getRSACipher();
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] deBytes = cipher.doFinal(Base64Util.decodeFromString(base64String));
            return new String(deBytes);
        } catch (InvalidKeyException e) {
            logger.error("", e);
        } catch (NoSuchAlgorithmException e) {
            logger.error("", e);
        } catch (NoSuchPaddingException e) {
            logger.error("", e);
        } catch (IllegalBlockSizeException e) {
            logger.error("", e);
        } catch (BadPaddingException e) {
            logger.error("", e);
        }
        return null;
    }

    /**
     * 使用keystore对密文进行解密
     * @param privateKeystore  私钥
     * @param base64String  密文
     * @return
     * @throws Exception 
     */
    public static String decrypt(String privateKeyString, String base64String) {
        PrivateKey privateKey = getPrivateKey(privateKeyString);
        if (privateKey != null) {
            return decrypt(privateKey, base64String);
        }
        return null;
    }

}
