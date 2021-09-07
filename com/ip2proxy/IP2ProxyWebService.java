package com.ip2proxy;

import java.util.regex.*;
import java.net.URL;
import java.net.URLEncoder;
import java.lang.StringBuffer; // JDK 1.4 does not support StringBuilder so can't use that

import com.google.gson.*;

public class IP2ProxyWebService {
    private static final Pattern pattern = Pattern.compile("^[\\dA-Z]{10}$");
    private static final Pattern pattern2 = Pattern.compile("^PX\\d+$");

    private String _APIKey = "";
    private String _Package = "";
    private boolean _UseSSL = true;

    public IP2ProxyWebService() {

    }

    /**
     * This function initializes the params for the web service.
     *
     * @param APIKey  IP2Proxy Web Service API key
     * @param Package IP2Proxy Web Service package (PX1 to PX11)
     * @throws IllegalArgumentException If an invalid parameter is specified
     */
    public void Open(String APIKey, String Package) throws IllegalArgumentException {
        Open(APIKey, Package, true);
    }

    /**
     * This function initializes the params for the web service.
     *
     * @param APIKey  IP2Proxy Web Service API key
     * @param Package IP2Proxy Web Service package (PX1 to PX11)
     * @param UseSSL  Set to true to call the web service using SSL
     * @throws IllegalArgumentException If an invalid parameter is specified
     */
    public void Open(String APIKey, String Package, boolean UseSSL) throws IllegalArgumentException {
        _APIKey = APIKey;
        _Package = Package;
        _UseSSL = UseSSL;

        CheckParams();
    }

    /**
     * This function validates the API key and package params.
     */
    private void CheckParams() throws IllegalArgumentException {
        if ((!pattern.matcher(_APIKey).matches()) && (!_APIKey.equals("demo"))) {
            throw new IllegalArgumentException("Invalid API key.");
        } else if (!pattern2.matcher(_Package).matches()) {
            throw new IllegalArgumentException("Invalid package name.");
        }
    }

    /**
     * This function to query IP2Proxy data.
     *
     * @param IPAddress IP Address you wish to query
     * @return IP2Proxy data
     * @throws IllegalArgumentException If an invalid parameter is specified
     * @throws RuntimeException         If an exception occurred at runtime
     */
    public JsonObject IPQuery(String IPAddress) throws IllegalArgumentException, RuntimeException {
        try {
            String myurl;
            String myjson;
            CheckParams(); // check here in case user haven't called Open yet

            StringBuffer bf = new StringBuffer();
            bf.append("http");
            if (_UseSSL) {
                bf.append("s");
            }
            bf.append("://api.ip2proxy.com/?key=").append(_APIKey).append("&package=").append(_Package).append("&ip=").append(URLEncoder.encode(IPAddress, "UTF-8"));

            myurl = bf.toString();

            myjson = Http.get(new URL(myurl));

            JsonParser parser = new JsonParser();
            JsonObject myresult = parser.parse(myjson).getAsJsonObject();
            return myresult;
        } catch (IllegalArgumentException ex) {
            throw ex;
        } catch (Exception ex2) {
            throw new RuntimeException(ex2);
        }
    }

    /**
     * This function to check web service credit balance.
     *
     * @return Credit balance
     * @throws IllegalArgumentException If an invalid parameter is specified
     * @throws RuntimeException         If an exception occurred at runtime
     */
    public JsonObject GetCredit() throws IllegalArgumentException, RuntimeException {
        try {
            String myurl;
            String myjson;
            CheckParams(); // check here in case user haven't called Open yet

            StringBuffer bf = new StringBuffer();
            bf.append("http");
            if (_UseSSL) {
                bf.append("s");
            }
            bf.append("://api.ip2proxy.com/?key=").append(_APIKey).append("&check=true");

            myurl = bf.toString();

            myjson = Http.get(new URL(myurl));

            JsonParser parser = new JsonParser();
            JsonObject myresult = parser.parse(myjson).getAsJsonObject();
            return myresult;
        } catch (IllegalArgumentException ex) {
            throw ex;
        } catch (Exception ex2) {
            throw new RuntimeException(ex2);
        }
    }
}