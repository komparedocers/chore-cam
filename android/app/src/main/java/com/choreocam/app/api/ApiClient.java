package com.choreocam.app.api;

import com.choreocam.app.BuildConfig;
import com.choreocam.app.ChoreoCamApplication;
import com.choreocam.app.utils.ConfigManager;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;
import java.util.concurrent.TimeUnit;

public class ApiClient {

    private static Retrofit retrofit;
    private static ApiService apiService;

    public static ApiService getApiService() {
        Timber.d("Getting API service instance");
        try {
            if (apiService == null) {
                Timber.d("Creating new API service instance");
                apiService = getRetrofitInstance().create(ApiService.class);
            }
            return apiService;
        } catch (Exception e) {
            Timber.e(e, "Error getting API service");
            throw e;
        }
    }

    private static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            Timber.d("Creating new Retrofit instance");
            try {
                ConfigManager config = ChoreoCamApplication.getConfigManager();

                // Create logging interceptor
                HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(
                    message -> Timber.tag("HTTP").d(message)
                );

                // Set log level based on build type
                if (BuildConfig.DEBUG) {
                    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                    Timber.d("HTTP logging level: BODY");
                } else {
                    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
                    Timber.d("HTTP logging level: BASIC");
                }

                // Create auth interceptor
                AuthInterceptor authInterceptor = new AuthInterceptor();

                int timeout = config.getTimeoutSeconds();
                Timber.d("API timeout: %d seconds", timeout);

                // Create OkHttp client
                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(authInterceptor)
                    .addInterceptor(loggingInterceptor)
                    .connectTimeout(timeout, TimeUnit.SECONDS)
                    .readTimeout(timeout, TimeUnit.SECONDS)
                    .writeTimeout(timeout, TimeUnit.SECONDS)
                    .build();

                // Build base URL
                String baseUrl = config.getBackendBaseUrl();
                if (!baseUrl.endsWith("/")) {
                    baseUrl += "/";
                }
                baseUrl += config.getApiVersion() + "/";

                Timber.i("API Base URL: %s", baseUrl);

                // Create Retrofit instance
                retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

                Timber.i("Retrofit instance created successfully");
            } catch (Exception e) {
                Timber.e(e, "Error creating Retrofit instance");
                throw e;
            }
        }
        return retrofit;
    }

    public static void resetClient() {
        Timber.d("Resetting API client");
        retrofit = null;
        apiService = null;
    }
}
