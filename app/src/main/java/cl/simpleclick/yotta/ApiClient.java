package cl.simpleclick.yotta;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import cl.simpleclick.yotta.models.ApiResponse;
import okhttp3.*;

import java.io.IOException;

public class ApiClient {
    private final String apiUrl;

    public ApiClient(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public void sendDomain(String domain, Callback<ApiResponse> callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(apiUrl + "?domain=" + domain)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                ApiResponse result = new Gson().fromJson(response.body().string(), ApiResponse.class);
                callback.onSuccess(result);
            }

            @Override
            public void onFailure(@NonNull Call call, IOException e) {
                callback.onError(e);
            }
        });
    }
}