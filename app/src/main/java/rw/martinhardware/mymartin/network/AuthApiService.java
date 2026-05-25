package rw.martinhardware.mymartin.network;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import rw.martinhardware.mymartin.entities.User;

public class AuthApiService {

    private final Context context;
    private final VolleySingleton volleySingleton;

    public AuthApiService(Context context) {
        this.context = context;
        this.volleySingleton = VolleySingleton.getInstance(context);
    }

    public interface AuthCallback {
        void onSuccess(JSONObject response);
        void onError(String errorMessage);
    }

    public interface AuthCallbackString {
        void onSuccess(String response);
        void onError(String errorMessage);
    }

    // Request WhatsApp OTP
    public void requestWhatsAppOtp(String phoneNumber, final AuthCallback callback) {
        String url = ApiConfig.AUTH_REQUEST_WHATSAPP_OTP;

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("phone_number", phoneNumber);
        } catch (JSONException e) {
            Log.e("AuthApiService", "Failed to create request body", e);
            callback.onError("Failed to create request: " + e.getMessage());
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                requestBody,
                response -> callback.onSuccess(response),
                error -> handleVolleyError(error, callback)
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        volleySingleton.addToRequestQueue(request);
    }

    // Verify WhatsApp OTP
    public void verifyWhatsAppOtp(String phoneNumber, String otp, final AuthCallback callback) {
        String url = ApiConfig.AUTH_VERIFY_WHATSAPP_OTP;

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("phone_number", phoneNumber);
            requestBody.put("otp", otp);
        } catch (JSONException e) {
            callback.onError("Failed to create request: " + e.getMessage());
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                requestBody,
                response -> callback.onSuccess(response),
                error -> handleVolleyError(error, callback)
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        volleySingleton.addToRequestQueue(request);
    }

    // Verify Firebase Phone
    public void verifyFirebasePhone(String firebaseToken, final AuthCallback callback) {
        String url = ApiConfig.AUTH_VERIFY_FIREBASE_PHONE;

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("firebase_token", firebaseToken);
        } catch (JSONException e) {
            callback.onError("Failed to create request: " + e.getMessage());
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                requestBody,
                response -> callback.onSuccess(response),
                error -> handleVolleyError(error, callback)
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        volleySingleton.addToRequestQueue(request);
    }

    // Logout
    public void logout(String token, final AuthCallback callback) {
        String url = ApiConfig.AUTH_LOGOUT;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                null,
                response -> callback.onSuccess(response),
                error -> handleVolleyError(error, callback)
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        volleySingleton.addToRequestQueue(request);
    }

    // Email/Password Login (if backend supports it)
    public void loginWithEmail(String email, String password, final AuthCallback callback) {
        String url = ApiConfig.BASE_URL + "/mobile/auth/login";

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("email", email);
            requestBody.put("password", password);
        } catch (JSONException e) {
            callback.onError("Failed to create request: " + e.getMessage());
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                requestBody,
                response -> callback.onSuccess(response),
                error -> handleVolleyError(error, callback)
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        volleySingleton.addToRequestQueue(request);
    }

    private void handleVolleyError(VolleyError error, AuthCallback callback) {
        String errorMessage = "Unknown error occurred";
        
        if (error.networkResponse != null) {
            int statusCode = error.networkResponse.statusCode;
            String responseData = new String(error.networkResponse.data);
            
            try {
                JSONObject errorJson = new JSONObject(responseData);
                if (errorJson.has("message")) {
                    errorMessage = errorJson.getString("message");
                } else if (errorJson.has("error")) {
                    errorMessage = errorJson.getString("error");
                } else {
                    errorMessage = "Error " + statusCode + ": " + responseData;
                }
            } catch (JSONException e) {
                errorMessage = "Error " + statusCode + ": " + responseData;
            }
        } else if (error.getMessage() != null) {
            errorMessage = error.getMessage();
        }
        
        callback.onError(errorMessage);
    }
}
