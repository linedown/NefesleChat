package ru.linedown.nefeslechat.classes;

public class WebSocketUtil {
    private final String url = OkHttpUtil.getWebsocketHeader() + OkHttpUtil.getBaseUrlWithoutApi()
            + OkHttpUtil.getAfterBaseUrl() + OkHttpUtil.getMessagingUrl();


}
