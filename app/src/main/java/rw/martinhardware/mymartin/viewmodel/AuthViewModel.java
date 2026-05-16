package rw.martinhardware.mymartin.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import io.objectbox.BoxStore;
import rw.martinhardware.mymartin.MyApp;
import rw.martinhardware.mymartin.entities.User;
import rw.martinhardware.mymartin.repository.AuthRepository;

public class AuthViewModel extends AndroidViewModel {

    private final AuthRepository authRepository;
    private final MutableLiveData<AuthState> authState = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public AuthViewModel(@NonNull Application application) {
        super(application);
        BoxStore boxStore = ((MyApp) application).getBoxStore();
        this.authRepository = new AuthRepository(application, boxStore);
        checkAuthStatus();
    }

    public LiveData<AuthState> getAuthState() {
        return authState;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void checkAuthStatus() {
        if (authRepository.isLoggedIn()) {
            User user = authRepository.getCurrentUser();
            authState.setValue(AuthState.AUTHENTICATED);
        } else {
            authState.setValue(AuthState.NOT_AUTHENTICATED);
        }
    }

    public void requestWhatsAppOtp(String phoneNumber) {
        isLoading.setValue(true);
        errorMessage.setValue(null);

        authRepository.requestWhatsAppOtp(phoneNumber, new AuthRepository.AuthRepositoryCallback() {
            @Override
            public void onSuccess(User user) {
                isLoading.setValue(false);
                authState.setValue(AuthState.OTP_SENT);
            }

            @Override
            public void onError(String errorMessage) {
                isLoading.setValue(false);
                AuthViewModel.this.errorMessage.setValue(errorMessage);
            }
        });
    }

    public void verifyWhatsAppOtp(String phoneNumber, String otp) {
        isLoading.setValue(true);
        errorMessage.setValue(null);

        authRepository.verifyWhatsAppOtp(phoneNumber, otp, new AuthRepository.AuthRepositoryCallback() {
            @Override
            public void onSuccess(User user) {
                isLoading.setValue(false);
                authState.setValue(AuthState.AUTHENTICATED);
            }

            @Override
            public void onError(String errorMessage) {
                isLoading.setValue(false);
                AuthViewModel.this.errorMessage.setValue(errorMessage);
            }
        });
    }

    public void verifyFirebasePhone(String firebaseToken) {
        isLoading.setValue(true);
        errorMessage.setValue(null);

        authRepository.verifyFirebasePhone(firebaseToken, new AuthRepository.AuthRepositoryCallback() {
            @Override
            public void onSuccess(User user) {
                isLoading.setValue(false);
                authState.setValue(AuthState.AUTHENTICATED);
            }

            @Override
            public void onError(String errorMessage) {
                isLoading.setValue(false);
                AuthViewModel.this.errorMessage.setValue(errorMessage);
            }
        });
    }

    public void loginWithEmail(String email, String password) {
        isLoading.setValue(true);
        errorMessage.setValue(null);

        authRepository.loginWithEmail(email, password, new AuthRepository.AuthRepositoryCallback() {
            @Override
            public void onSuccess(User user) {
                isLoading.setValue(false);
                authState.setValue(AuthState.AUTHENTICATED);
            }

            @Override
            public void onError(String errorMessage) {
                isLoading.setValue(false);
                AuthViewModel.this.errorMessage.setValue(errorMessage);
            }
        });
    }

    public void logout() {
        isLoading.setValue(true);
        errorMessage.setValue(null);

        authRepository.logout(new AuthRepository.AuthRepositoryCallback() {
            @Override
            public void onSuccess(User user) {
                isLoading.setValue(false);
                authState.setValue(AuthState.NOT_AUTHENTICATED);
            }

            @Override
            public void onError(String errorMessage) {
                isLoading.setValue(false);
                AuthViewModel.this.errorMessage.setValue(errorMessage);
            }
        });
    }

    public void resetAuthState() {
        authState.setValue(AuthState.NOT_AUTHENTICATED);
        errorMessage.setValue(null);
    }

    public enum AuthState {
        NOT_AUTHENTICATED,
        OTP_SENT,
        AUTHENTICATED
    }
}
