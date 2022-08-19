package com.microsoft.quick.auth.signin.http;

public class MicrosoftAPI {
    public static final int CONNECT_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 30000;
    public static final String MS_GRAPH_TK_REQUEST_PREFIX = "Bearer ";
    public static final String MS_GRAPH_ROOT_ENDPOINT = "https://graph.microsoft.com/";
    public static final String MS_GRAPH_USER_INFO_PATH = MS_GRAPH_ROOT_ENDPOINT + "v1.0/me";
    public static final String MS_GRAPH_USER_PHOTO = MS_GRAPH_ROOT_ENDPOINT + "v1.0/me/photo";
    public static final String MS_GRAPH_USER_PHOTO_LARGEST = MS_GRAPH_USER_PHOTO + "/$value";
}
