package com.microsoft.quick.auth.signin;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.microsoft.quick.auth.signin.callback.OnCompleteListener;
import com.microsoft.quick.auth.signin.entity.AccountInfo;
import com.microsoft.quick.auth.signin.entity.ITokenResult;
import com.microsoft.quick.auth.signin.entity.MSQASignInScope;

public interface MSQASignInClient {
    /**
     * Allows a user to sign in to your application with one of their accounts. This method may
     * only be called once: once a user is signed in, they must first be signed out before
     * another user may sign in. If you wish to prompt the existing user for credentials use
     * signInAgain(Activity, String[], Prompt, AuthenticationCallback) or acquireToken
     * (AcquireTokenParameters).
     * Note: The authority used to make the sign in request will be either the MSAL default:
     * https://login .microsoftonline.com/common or the default authority specified by you in
     * your configuration
     *
     * @param activity         Activity that is used as the parent activity for launching sign in
     *                         page.
     * @param completeListener A callback to be invoked when sign in success and will return sign
     *                         in account info
     *                         {@link AccountInfo}.
     */
    Disposable signIn(@NonNull final Activity activity,
                      @NonNull final OnCompleteListener<AccountInfo> completeListener);

    /**
     * Signs out the current the Account and Credentials (tokens).
     *
     * @param callback A callback to be invoked when sign out finishes and will return sign out
     *                 result.
     */
    Disposable signOut(@NonNull final OnCompleteListener<Boolean> callback);

    /**
     * Gets the current account. This method must be called whenever the application is resumed
     * or prior to running a scheduled background operation.
     *
     * @param activity         Activity that is used as the parent activity for get sign account
     * @param completeListener A callback to be invoked when complete and will return sign in
     *                         account info
     *                         {@link AccountInfo} if success
     */
    Disposable getCurrentSignInAccount(@NonNull final Activity activity,
                                       @NonNull final OnCompleteListener<AccountInfo> completeListener);

    /**
     * Perform acquire token silent call. If there is a valid access token in the cache, the sdk
     * will return the access token; If no valid access token exists, the sdk will try to find a
     * refresh token and use the refresh token to get a new access token. If refresh token does
     * not exist or it fails the refresh, exception will be sent back via callback.
     *
     * @param accountInfo      The accountInfo object was obtained from the callback in sign-in
     *                         process.
     * @param scopes           The non-null array of scopes to be requested for the access token,
     *                         the supported
     *                         scopes can be found in{@link MSQASignInScope}.
     * @param completeListener A callback to be invoked when token get finished.
     */
    Disposable acquireTokenSilent(@NonNull final AccountInfo accountInfo,
                                  @NonNull final String[] scopes,
                                  @NonNull final OnCompleteListener<ITokenResult> completeListener);

    /**
     * Acquire token interactively, will pop-up webUI. Interactive flow will skip the cache lookup.
     *
     * @param activity         Activity that is used as the parent activity for get token.
     * @param scopes           The non-null array of scopes to be requested for the access token,
     *                         the supported scopes can be found in{@link MSQASignInScope}.
     * @param loginHint        Optional. If provided, will be used as the query parameter sent
     *                         for authenticating the user, which will have the UPN pre-populated.
     * @param completeListener A callback to be invoked when token get finished.
     */
    Disposable acquireToken(@NonNull final Activity activity, @NonNull final String[] scopes,
                            @Nullable final String loginHint,
                            @NonNull final OnCompleteListener<ITokenResult> completeListener);

    /**
     * Cancel all the requests in this sign-in client.
     */
    void release();
}
