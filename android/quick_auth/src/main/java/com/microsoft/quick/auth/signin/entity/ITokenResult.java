package com.microsoft.quick.auth.signin.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.microsoft.identity.client.IAccount;

import java.util.Date;
import java.util.UUID;

public interface ITokenResult {
    /**
     * @return The access token requested.
     */
    @NonNull
    String getAccessToken();

    /**
     * Gets the fully-formed Authorization header value. Includes the Authentication scheme.
     *
     * @return The Authorization header value.
     */
    @NonNull
    String getAuthorizationHeader();

    /**
     * Gets the authentication scheme (Bearer, PoP, etc)....
     *
     * @return The authentication scheme name.
     */
    @NonNull
    String getAuthenticationScheme();

    /**
     * @return The expiration time of the access token returned in the Token property. This value is
     * calculated based on current UTC time measured locally and the value expiresIn returned
     * from the service. Please note that if the authentication scheme is 'pop', this value
     * reflects the expiry of the 'inner' token returned by AAD and does not indicate the
     * expiry
     * of the signed pop JWT ('outer' token).
     */
    @NonNull
    Date getExpiresOn();

    /**
     * @return A unique tenant identifier that was used in token acquisition. Could be null if
     * tenant information is not returned by the service.
     */
    @Nullable
    String getTenantId();

    /**
     * @return Gets the Account.
     * Returns: The Account to get.
     */
    @NonNull
    IAccount getAccount();

    /**
     * @return The scopes returned from the service.
     */
    @NonNull
    String[] getScope();

    /**
     * Gets the correlation id used during the acquire token request. Could be null if an error
     * occurs when parsing from String or if not set.
     *
     * @return a UUID representing a correlation id
     */
    @Nullable
    UUID getCorrelationId(); // this should never actually be null for SDK
}
