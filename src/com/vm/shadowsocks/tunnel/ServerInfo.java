package com.vm.shadowsocks.tunnel;

import android.net.Uri;

public class ServerInfo {
    public String host;
    public int port;
    public String password;
    public TunnelMethod method;

    public ServerInfo() {
    }

    public boolean isValid() {
        return isValidUrl(getProxyUrl());
    }

    private boolean isValidUrl(String url) {
        try {
            if (url == null || url.isEmpty())
                return false;
            if (url.startsWith("ss://")) {//file path
                return true;
            } else { //url
                Uri uri = Uri.parse(url);
                if (!"http".equals(uri.getScheme()) && !"https".equals(uri.getScheme()))
                    return false;
                if (uri.getHost() == null)
                    return false;
            }
            return true;
        } catch (Exception e) {
            //ignore
        }
        return false;
    }

    public String getProxyUrl() {
        return "ss://" + method.valueString() + ":" + password + "@" + host + ":" + port;
    }
}
