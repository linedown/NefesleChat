package ru.linedown.nefeslechat.utils;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.Collections;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import okhttp3.CipherSuite;
import okhttp3.ConnectionSpec;
import okhttp3.Cookie;
import okhttp3.HttpUrl;
import okhttp3.JavaNetCookieJar;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.TlsVersion;
import ru.linedown.nefeslechat.entity.ChatDTO;
import ru.linedown.nefeslechat.entity.MessageAllInfoDTO;
import ru.linedown.nefeslechat.entity.TaskDTO;
import ru.linedown.nefeslechat.entity.UserDetailsDTO;
import ru.linedown.nefeslechat.entity.UserInListDTO;
import ru.linedown.nefeslechat.entity.AuthorizationForm;
import ru.linedown.nefeslechat.entity.RegistrationForm;

public class OkHttpUtil {
    private static OkHttpClient okHttpClient =  new OkHttpClient.Builder().connectionSpecs(Collections
                    .singletonList(new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                    .tlsVersions(TlsVersion.TLS_1_3)
                    .cipherSuites(
                            CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                            CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                            CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256)
                    .build()))
            .cookieJar(new JavaNetCookieJar
            (new CookieManager(null, CookiePolicy.ACCEPT_ALL))).build();
    private static final String baseUrl = "https://messenger.nefesle.ru:3254/api";
    @Getter
    private static final String baseUrlWithoutApi = "messenger.nefesle.ru";
    private static final String domainRegistation = "/auth/register";
    private static final String domainAuthorization = "/auth";
    private static final String userProfilePath = "/user-profile";
    private static final String myProfilePath = "/my-profile";
    private static final String searchPath = "/users?last-name=";
    @Getter
    private static final String websocketHeader = "wss://";
    @Getter
    private static final String afterBaseUrl = ":3254/api";
    @Getter
    private static final String messagingUrl = "/messenger";
    @Getter
    private static final String userUrl = "/app/user/";
    @Getter
    private static final String topicUrl = "/topic/user/";
    private static final String logoutUrl = "/auth/logout";
    private static final String statusUrl = "/get-online-status/";
    private static final String chatsUrl = "/chats";
    @Getter
    private static final String groupSubscribeChatsUrl = "/topic/chat/";
    @Getter
    private static final String chatUrl = "/app/chat/";
    private static final String singleChatUrl = "/get-singlechat-id/";
    private static final String tasksUrl = "/tasks";
    private static final String createTaskUrl = "/create";
    private static final String changeTaskUrl = "/change-status";
    private static final String deleteTaskUrl = "/delete/";
    @Getter
    private static final String getSelfChatUrl = "/get-selfchat-id";

    @Getter
    @Setter
    private static String textMessage;
    private static int user_id;
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static String JWTToken = "";
    static List<Cookie> cookies;
    @Getter
    @Setter
    private static int myId;
    @Getter
    private static String statusStr;

    public static Response processAuthentification(AuthorizationForm af) throws IOException {
        boolean regFlag = af instanceof RegistrationForm;
        String jsonStr;
        if(regFlag) jsonStr = new Gson().toJson((RegistrationForm) af);
        else jsonStr = new Gson().toJson(af);

        RequestBody requestbody = RequestBody.create(jsonStr, JSON);
        Request request;
        if(regFlag) request = new Request.Builder().url(baseUrl + domainRegistation).
                post(requestbody).build();
        else request = new Request.Builder().url(baseUrl + domainAuthorization).post(requestbody).build();

        Response response = okHttpClient.newCall(request).execute();
        cookies = okHttpClient.cookieJar().loadForRequest(request.url());
        for(Cookie cookie : cookies) if(cookie.name().equals("JWT")) JWTToken = cookie.value();
        return response;
    }

    public static UserDetailsDTO getCurrentUser() throws IOException {
        Request request = new Request.Builder().url(baseUrl + myProfilePath).get().build();

        Response response = okHttpClient.newCall(request).execute();
        ResponseBody responseBody = response.body();
        String responseStr = responseBody.string();
        response.close();
        responseBody.close();

        return new Gson().fromJson(responseStr, UserDetailsDTO.class);
    }

    public static UserDetailsDTO getOtherUser(int id) throws IOException {
        Request request = new Request.Builder().url(baseUrl + userProfilePath + "/" + id).get().build();

        Response response = okHttpClient.newCall(request).execute();
        ResponseBody responseBody = response.body();
        String responseBodyStr = responseBody.string();
        response.close();
        responseBody.close();

        Request requestStatus = new Request.Builder().url(baseUrl + statusUrl + user_id).get().build();
        Response responseStatus = okHttpClient.newCall(requestStatus).execute();
        ResponseBody responseBodyStatus = responseStatus.body();
        statusStr = responseBodyStatus.string();

        responseBodyStatus.close();
        responseStatus.close();

        return new Gson().fromJson(responseBodyStr, UserDetailsDTO.class);
    }

    public static List<UserInListDTO> getListUsers(String searchRequest) throws IOException {
        Request request = new Request.Builder().url(baseUrl + searchPath + searchRequest).get().build();

        Response response = okHttpClient.newCall(request).execute();
        ResponseBody responseBody = response.body();

        List<UserInListDTO> users = new Gson().fromJson(responseBody.string(), new TypeToken<List<UserInListDTO>>(){}.getType());

        response.close();
        responseBody.close();

        return users;
    }

    public static List<ChatDTO> getListChats() throws IOException {
        Request request = new Request.Builder().url(baseUrl + chatsUrl).get().build();
        Response response = okHttpClient.newCall(request).execute();
        ResponseBody responseBody = response.body();
        List<ChatDTO> chats = new Gson().fromJson(responseBody.string(), new TypeToken<List<ChatDTO>>(){}.getType());
        Log.d("GetListChats", chats.toString());

        responseBody.close();
        response.close();

        return chats;
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

    public static void setUserId(int id){
        user_id = id;
    }

    public static int getUserId(){
        return user_id;
    }
    public static void clearCookies() throws IOException {
        RequestBody requestBody = RequestBody.create(new byte[0]);
        Request request = new Request.Builder().url(baseUrl + logoutUrl).post(requestBody).build();
        Response response = okHttpClient.newCall(request).execute();
        response.close();
    }

    public static List<MessageAllInfoDTO> getMessagesInChat(int chatId) throws IOException {
        String urlWithChat = "/chat/" + chatId + "/messages?page=0";

        Request request = new Request.Builder().url(baseUrl + urlWithChat).get().build();
        Response response = okHttpClient.newCall(request).execute();
        ResponseBody responseBody = response.body();
        List<MessageAllInfoDTO> messages = Collections.emptyList();
        messages = new Gson().fromJson(responseBody.string(), new TypeToken<List<MessageAllInfoDTO>>(){}.getType());
        //Log.d("GetMessages", messages.toString());
        response.close();

        Collections.reverse(messages);

        return messages;

    }

    public static String getIdInChatForProfile(int userId) throws IOException {
        Request request;
        if(userId != myId) request = new Request.Builder().url(baseUrl + singleChatUrl + userId).get().build();
        else request = new Request.Builder().url(baseUrl + getSelfChatUrl).get().build();
        Response response = okHttpClient.newCall(request).execute();
        ResponseBody responseBody = response.body();
        String idStr = responseBody.string();

        responseBody.close();
        response.close();
        return idStr;
    }

    public static List<TaskDTO> getTasks() throws IOException {
        Request request = new Request.Builder().url(baseUrl + tasksUrl).get().build();
        Response response = okHttpClient.newCall(request).execute();

        ResponseBody responseBody = response.body();
        List<TaskDTO> tasks = new Gson().fromJson(responseBody.string(), new TypeToken<List<TaskDTO>>(){}.getType());

        responseBody.close();
        response.close();
        return tasks;
    }

    public static TaskDTO createTask(String taskText) throws IOException {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setText(taskText);

        RequestBody requestBody = RequestBody.create(new Gson().toJson(taskDTO), JSON);
        Request request = new Request.Builder().url(baseUrl + tasksUrl + createTaskUrl).post(requestBody).build();

        Response response = okHttpClient.newCall(request).execute();
        if(!response.isSuccessful()) {
            response.close();
            return null;
        }

        ResponseBody responseBody = response.body();

        TaskDTO newTask = new Gson().fromJson(responseBody.string(), TaskDTO.class);

        responseBody.close();
        response.close();

        return newTask;
    }
    public static boolean changeTaskStatus(int taskId) throws IOException {

        RequestBody requestBody = RequestBody.create(String.valueOf(taskId).getBytes(), JSON);
        Request request = new Request.Builder().url(baseUrl + tasksUrl + changeTaskUrl).post(requestBody).build();

        Response response = okHttpClient.newCall(request).execute();
        boolean isSuccess = response.isSuccessful();
        response.close();

        return isSuccess;
    }

    public static boolean deleteTask(int taskId) throws IOException {
        Request request = new Request.Builder().url(baseUrl + tasksUrl + deleteTaskUrl + taskId).delete().build();
        Response response = okHttpClient.newCall(request).execute();

        boolean isSuccess = response.isSuccessful();
        response.close();

        return isSuccess;
    }

}
