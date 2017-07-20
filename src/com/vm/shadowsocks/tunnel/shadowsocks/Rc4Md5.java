package com.vm.shadowsocks.tunnel.shadowsocks;

import org.bouncycastle.crypto.StreamBlockCipher;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

public class Rc4Md5 implements ICrypt {
    String name;
    String password;

    public Rc4Md5(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public final static String CIPHER_SEED_CFB = "rc4-md";

    public static Map<String, String> getCiphers() {
        Map<String, String> ciphers = new HashMap<String, String>();
        ciphers.put(CIPHER_SEED_CFB, Rc4Md5.class.getName());
        return ciphers;
    }

    @Override
    public int getIVLength() {
        return 16;
    }

    @Override
    public int getKeyLength() {
        return 16;
    }
    // 1 加密
    public String encrypt(final String plaintext, final String key) {
        Integer[] S = new Integer[256]; // S盒
        Character[] keySchedul = new Character[plaintext.length()]; // 生成的密钥流
        StringBuffer ciphertext = new StringBuffer();

        ksa(S, key);
        rpga(S, keySchedul, plaintext.length());

        for (int i = 0; i < plaintext.length(); ++i) {
            ciphertext.append((char) (plaintext.charAt(i) ^ keySchedul[i]));
        }

        return ciphertext.toString();
    }

    // 1.1 KSA--密钥调度算法--利用key来对S盒做一个置换，也就是对S盒重新排列
    public void ksa(Integer[] s, String key) {
        for (int i = 0; i < 256; ++i) {
            s[i] = i;
        }

        int j = 0;
        for (int i = 0; i < 256; ++i) {
            j = (j + s[i] + key.charAt(i % key.length())) % 256;
            swap(s, i, j);
        }
    }

    // 1.2 RPGA--伪随机生成算法--利用上面重新排列的S盒来产生任意长度的密钥流
    public void rpga(Integer[] s, Character[] keySchedul, int plaintextLength) {
        int i = 0, j = 0;
        for (int k = 0; k < plaintextLength; ++k) {
            i = (i + 1) % 256;
            j = (j + s[i]) % 256;
            swap(s, i, j);
            keySchedul[k] = (char) (s[(s[i] + s[j]) % 256]).intValue();
        }
    }

    // 1.3 置换
    private void swap(int[] s, int i, int j) {
        Integer mTemp = s[i];
        s[i] = s[j];
        s[j] = mTemp;
    }

    private byte[] encrypt(byte[] data, int length) {
        return new byte[0];
    }

    private byte[] decrypt(byte[] data, int length) {
        return new byte[0];
    }

    @Override
    public byte[] encrypt(byte[] data) {
        return encrypt(data, data.length);
    }

    @Override
    public byte[] decrypt(byte[] data) {
        return decrypt(data, data.length);
    }

    @Override
    public void encrypt(byte[] data, ByteArrayOutputStream stream) {
        encrypt(data, data.length, stream);
    }

    @Override
    public void encrypt(byte[] data, int length, ByteArrayOutputStream stream) {
        byte[] rs = encrypt(data, length);
        try {
            stream.write(rs);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void decrypt(byte[] data, ByteArrayOutputStream stream) {
        decrypt(data, data.length, stream);
    }

    @Override
    public void decrypt(byte[] data, int length, ByteArrayOutputStream stream) {
        byte[] rs = decrypt(data, length);
        try {
            stream.write(rs);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
