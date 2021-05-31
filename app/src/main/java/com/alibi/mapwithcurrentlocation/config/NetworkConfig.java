package com.alibi.mapwithcurrentlocation.config;

public class NetworkConfig {

    //Production Url
    public static String _BASE_URL_PROD = "https://lw-node.herokuapp.com/tasks/";
    //Development Url
    public static String _BASE_URL_DEV = "https://lw-node.herokuapp.com/tasks/";
    private static final String _ENV = "DEV"; // DEV / PRODUCTION

    public static String GET_BASE_URL() {
        if (_ENV.equals("PRODUCTION"))
            return _BASE_URL_PROD;
        else
            return _BASE_URL_DEV;
    }


}
