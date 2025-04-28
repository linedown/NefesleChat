package ru.linedown.nefeslechat.classes;

import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.HttpUrl;
import okhttp3.JavaNetCookieJar;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import ru.linedown.nefeslechat.entity.AuthorizationForm;
import ru.linedown.nefeslechat.entity.RegistrationForm;

public class OkHttpUtil {
    private static OkHttpClient okHttpClient =  new OkHttpClient.Builder().cookieJar(new JavaNetCookieJar
            (new CookieManager(null, CookiePolicy.ACCEPT_ALL))).build();
    private static final String baseUrl = "http://linedown.ru:3254/api";
    private static final String baseUrlWithoutApi = "linedown.ru";
    private static final String domainRegistation = "/auth/register";
    private static final String domainAuthorization = "/auth";
    private static final String userProfilePath = "/user-profile";
    private static final String myProfilePath = "/my-profile";
    private static long user_id;
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static String JWTToken = "";
    static List<Cookie> cookies;

    public static Response processAuthentification(AuthorizationForm af) throws IOException {
        boolean regFlag = af instanceof RegistrationForm;
        String jsonStr;
        if(regFlag) jsonStr = new Gson().toJson((RegistrationForm) af);
        else jsonStr = new Gson().toJson(af);

        RequestBody requestbody = RequestBody.create(jsonStr, JSON);
        Request request;
        if(regFlag) request = new Request.Builder().url(baseUrl + domainRegistation).post(requestbody).build();
        else request = new Request.Builder().url(baseUrl + domainAuthorization).post(requestbody).build();

        Response response = okHttpClient.newCall(request).execute();
        cookies = okHttpClient.cookieJar().loadForRequest(request.url());
        for(Cookie cookie : cookies) if(cookie.name().equals("JWT")) JWTToken = cookie.value();
        //for(Cookie cookie : cookies) Log.d("CookieTest: ", "Кука: " + cookie.domain());
        return response;
    }

    public static UserDetailsDTO getCurrentUser() throws IOException {
        Request request = new Request.Builder().url(baseUrl + myProfilePath).get().build();

        Response response = okHttpClient.newCall(request).execute();
        String responseBody = response.body().string();

        return new Gson().fromJson(responseBody, UserDetailsDTO.class);
    }

    public static UserDetailsDTO getOtherUser(int id) throws IOException {
        Request request = new Request.Builder().url(baseUrl + userProfilePath + "/" + id).get().build();

        Response response = okHttpClient.newCall(request).execute();
        String responseBody = response.body().string();

        return new Gson().fromJson(responseBody, UserDetailsDTO.class);
    }

    public static String getJWTToken(){
        return JWTToken;
    }

    public static void setJWTToken(String token){
        JWTToken = token;
        okHttpClient.cookieJar().saveFromResponse(new HttpUrl.Builder().scheme("http")
                        .host(baseUrlWithoutApi).build(),
                List.of(new Cookie.Builder().name("JWT").value(JWTToken).domain(baseUrlWithoutApi).build()));
    }

}
