package cl.simpleclick.yotta;

import android.util.Log;

import cl.simpleclick.yotta.models.ApiResponse;

public class SniSdk {
    private static SniSdk instance;
    private ApiClient apiClient;

    public static SniSdk getInstance() {
        if (instance == null) instance = new SniSdk();
        return instance;
    }

    public void init(String apiUrl) {
        apiClient = new ApiClient(apiUrl);
    }

    public void onSniCaptured(String domain) {
        apiClient.sendDomain(domain, new Callback<ApiResponse>() {
            @Override
            public void onSuccess(ApiResponse response) {
                Log.d("SNI-SDK", "Respuesta: " + response.toString());
            }

            @Override
            public void onError(Exception e) {
                Log.e("SNI-SDK", "Error: " + e.getMessage());
            }
        });
    }
}