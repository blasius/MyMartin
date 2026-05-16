package rw.martinhardware.mymartin.repository;

import android.content.Context;

import org.json.JSONObject;

import io.objectbox.BoxStore;
import rw.martinhardware.mymartin.dao.UserDao;
import rw.martinhardware.mymartin.entities.User;
import rw.martinhardware.mymartin.network.AuthApiService;

public class AuthRepository {

    private final UserDao userDao;
    private final AuthApiService authApiService;
    private final Context context;

    public AuthRepository(Context context, BoxStore boxStore) {
        this.context = context;
        this.userDao = new UserDao(boxStore);
        this.authApiService = new AuthApiService(context);
    }

    public interface AuthRepositoryCallback {
        void onSuccess(User user);
        void onError(String errorMessage);
    }

    // Check if user is logged in
    public boolean isLoggedIn() {
        User activeUser = userDao.getActiveUser();
        return activeUser != null && activeUser.isTokenValid();
    }

    // Get current logged in user
    public User getCurrentUser() {
        return userDao.getActiveUser();
    }

    // Request WhatsApp OTP
    public void requestWhatsAppOtp(String phoneNumber, final AuthRepositoryCallback callback) {
        authApiService.requestWhatsAppOtp(phoneNumber, new AuthApiService.AuthCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                callback.onSuccess(null);
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }

    // Verify WhatsApp OTP and save user
    public void verifyWhatsAppOtp(String phoneNumber, String otp, final AuthRepositoryCallback callback) {
        authApiService.verifyWhatsAppOtp(phoneNumber, otp, new AuthApiService.AuthCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    User user = parseUserFromResponse(response);
                    user.setWhatsappNumber(phoneNumber);
                    saveUserLocally(user);
                    callback.onSuccess(user);
                } catch (Exception e) {
                    callback.onError("Failed to parse user data: " + e.getMessage());
                }
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }

    // Verify Firebase Phone and save user
    public void verifyFirebasePhone(String firebaseToken, final AuthRepositoryCallback callback) {
        authApiService.verifyFirebasePhone(firebaseToken, new AuthApiService.AuthCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    User user = parseUserFromResponse(response);
                    saveUserLocally(user);
                    callback.onSuccess(user);
                } catch (Exception e) {
                    callback.onError("Failed to parse user data: " + e.getMessage());
                }
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }

    // Login with email and password
    public void loginWithEmail(String email, String password, final AuthRepositoryCallback callback) {
        authApiService.loginWithEmail(email, password, new AuthApiService.AuthCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    User user = parseUserFromResponse(response);
                    user.setEmail(email);
                    saveUserLocally(user);
                    callback.onSuccess(user);
                } catch (Exception e) {
                    callback.onError("Failed to parse user data: " + e.getMessage());
                }
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }

    // Logout
    public void logout(final AuthRepositoryCallback callback) {
        User currentUser = getCurrentUser();
        if (currentUser != null && currentUser.getToken() != null) {
            authApiService.logout(currentUser.getToken(), new AuthApiService.AuthCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    userDao.logout();
                    callback.onSuccess(null);
                }

                @Override
                public void onError(String errorMessage) {
                    // Even if API call fails, logout locally
                    userDao.logout();
                    callback.onSuccess(null);
                }
            });
        } else {
            userDao.logout();
            callback.onSuccess(null);
        }
    }

    // Save user locally
    private void saveUserLocally(User user) {
        user.setCreatedAt(System.currentTimeMillis());
        user.setUpdatedAt(System.currentTimeMillis());
        user.setActive(true);
        
        // Calculate token expiry (assuming token is valid for 30 days)
        user.setTokenExpiry(System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000));
        
        long userId = userDao.saveUser(user);
        userDao.setActiveUser(user.getUuid());
    }

    // Parse user from API response
    private User parseUserFromResponse(JSONObject response) throws Exception {
        User user = new User();
        
        if (response.has("user")) {
            JSONObject userJson = response.getJSONObject("user");
            if (userJson.has("uuid")) user.setUuid(userJson.getString("uuid"));
            if (userJson.has("name")) user.setName(userJson.getString("name"));
            if (userJson.has("email")) user.setEmail(userJson.getString("email"));
            if (userJson.has("phone_number")) user.setPhoneNumber(userJson.getString("phone_number"));
        }
        
        if (response.has("token")) {
            user.setToken(response.getString("token"));
        }
        
        if (response.has("refresh_token")) {
            user.setRefreshToken(response.getString("refresh_token"));
        }
        
        return user;
    }
}
