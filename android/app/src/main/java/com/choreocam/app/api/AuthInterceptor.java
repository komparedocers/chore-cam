package com.choreocam.app.api;

import com.choreocam.app.ChoreoCamApplication;
import com.choreocam.app.models.User;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

public class AuthInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        // Get current user's auth token
        User currentUser = ChoreoCamApplication.getDatabase().userDao().getCurrentUser();

        if (currentUser != null && currentUser.getAuthToken() != null) {
            // Add authorization header
            Request authenticatedRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer " + currentUser.getAuthToken())
                .build();
            return chain.proceed(authenticatedRequest);
        }

        return chain.proceed(originalRequest);
    }
}
