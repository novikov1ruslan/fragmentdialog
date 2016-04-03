package com.ivygames.morskoiboi;

public class GoogleApiFactory {

    private static GoogleApiClientWrapper sApiClient;

    public static void inject(GoogleApiClientWrapper apiClient) {
        sApiClient = apiClient;
    }

    public static GoogleApiClientWrapper getApiClient() {
        return sApiClient;
    }
}
