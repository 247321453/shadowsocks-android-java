package com.vm.shadowsocks.tunnel;

public enum  TunnelMethod {
    rc4_md5("rc4-md5"),
    aes_128_cfb("aes-128-cfb"),
    aes_192_cfb("aes-192-cfb"),
    aes_256_cfb("aes-256-cfb"),
    aes_128_ofb("aes-128-ofb"),
    aes_192_ofb("aes-192-ofb"),
    bf_cfb("bf-cfb"),
    camellia_256_cfb("camellia-256-cfb"),
    camellia_128_cfb("camellia-128-cfb"),
    camellia_192_cfb("camellia-192-cfb"),
    seed_cfb("seed-cfb");

    private TunnelMethod(String val){
        value = val;
    }
    private String value;

    public String valueString(){
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
