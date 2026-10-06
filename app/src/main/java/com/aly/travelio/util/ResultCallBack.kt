package com.aly.travelio.util


import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.gson.JsonParseException
import retrofit2.HttpException
import java.io.IOException


enum class NetworkError(){NETWORK,SERVER,SessionExpired}

sealed class ResultCallBack<out T : Any> {
    data class Success<out T : Any>(val data: T) : ResultCallBack<T>()
    data class Error(val type: NetworkError, val message: String?, val statusCode: Int? = null) : ResultCallBack<Nothing>()
}

fun Exception.toFailure(): ResultCallBack.Error {
    return when (this) {
        is HttpException -> {
            when (code()) {
                in 300 until 400 -> ResultCallBack.Error(NetworkError.SERVER, "Not Authorized.")
                in 400 until 500 -> ResultCallBack.Error(NetworkError.SERVER, "Data not valid.")
                in 500 until 600 -> ResultCallBack.Error(
                    NetworkError.SERVER,
                    "Server is busy. Please try again later."
                )
                else -> ResultCallBack.Error(
                    NetworkError.SERVER,
                    "Something went wrong. Please try again later."
                )
            }
        }
        is IOException -> ResultCallBack.Error(NetworkError.NETWORK, "Check your internet connection.")
        is JsonParseException -> ResultCallBack.Error(NetworkError.SERVER, "Data does not match")
        else -> ResultCallBack.Error(NetworkError.SERVER, "Something went wrong. Please try again later.")
    }
}


fun Exception.toFireBaseFailure(): ResultCallBack.Error {
    return when (this) {
        is FirebaseAuthRecentLoginRequiredException -> ResultCallBack.Error(NetworkError.SessionExpired, "Something went wrong. Please try again later.")
        is FirebaseAuthException -> {
            when (this.errorCode) {
                "ERROR_INVALID_CREDENTIAL" -> ResultCallBack.Error(NetworkError.SERVER, "Wrong password.")
                "ERROR_INVALID_CUSTOM_TOKEN" -> ResultCallBack.Error(NetworkError.SERVER, "The custom token format is incorrect or expired.")
                "ERROR_CUSTOM_TOKEN_MISMATCH" -> ResultCallBack.Error(NetworkError.SERVER, "The custom token corresponds to a different audience.")
                "ERROR_INVALID_EMAIL" -> ResultCallBack.Error(NetworkError.SERVER, "The email address is not valid.")
                "ERROR_USER_DISABLED" -> ResultCallBack.Error(NetworkError.SERVER, "The user account has been disabled by an administrator.")
                "ERROR_USER_NOT_FOUND" -> ResultCallBack.Error(NetworkError.SERVER, "There is no user corresponding to this identifier.")
                "ERROR_EMAIL_ALREADY_IN_USE" -> ResultCallBack.Error(NetworkError.SERVER, "The email address is already in use by another account.")
                "ERROR_WEAK_PASSWORD" -> ResultCallBack.Error(NetworkError.SERVER, "The password is too weak.")
                "ERROR_WRONG_PASSWORD" -> ResultCallBack.Error(NetworkError.SERVER, "The password is invalid or the user does not have a password.")
                "ERROR_CREDENTIAL_ALREADY_IN_USE" -> ResultCallBack.Error(NetworkError.SERVER, "The account is already in use with a different credential.")
                "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" -> ResultCallBack.Error(NetworkError.SERVER, "An account already exists with the same email address but with different sign-in credentials.")
                "ERROR_REQUIRES_RECENT_LOGIN" -> ResultCallBack.Error(NetworkError.SERVER, "This operation requires recent authentication. Please sign in again.")
                "ERROR_INVALID_VERIFICATION_CODE" -> ResultCallBack.Error(NetworkError.SERVER, "The SMS verification code is invalid.")
                "ERROR_INVALID_VERIFICATION_ID" -> ResultCallBack.Error(NetworkError.SERVER, "The verification ID is invalid.")
                "ERROR_SESSION_EXPIRED" -> ResultCallBack.Error(NetworkError.SERVER, "The SMS code has expired. Please request a new code.")
                "ERROR_QUOTA_EXCEEDED" -> ResultCallBack.Error(NetworkError.SERVER, "The project's quota for this operation has been exceeded.")
                "ERROR_TOO_MANY_REQUESTS" -> ResultCallBack.Error(NetworkError.SERVER, "Too many requests in a short period. Please try again later.")
                "ERROR_OPERATION_NOT_ALLOWED" -> ResultCallBack.Error(NetworkError.SERVER, "This operation is not allowed. Please enable the sign-in provider in the Firebase Console.")
                "ERROR_PROVIDER_ALREADY_LINKED" -> ResultCallBack.Error(NetworkError.SERVER, "The user is already linked to this provider.")
                "ERROR_INVALID_ID_TOKEN" -> ResultCallBack.Error(NetworkError.SERVER, "The provided ID token is invalid.")
                "ERROR_USER_TOKEN_EXPIRED" -> ResultCallBack.Error(NetworkError.SERVER, "The user's token has expired.")
                "ERROR_INVALID_REFRESH_TOKEN" -> ResultCallBack.Error(NetworkError.SERVER, "The refresh token is invalid.")
                "ERROR_USER_MISMATCH" -> ResultCallBack.Error(NetworkError.SERVER, "The user corresponding to the refresh token does not match the current user.")
                "ERROR_INTERNAL_ERROR" -> ResultCallBack.Error(NetworkError.SERVER, "An internal error has occurred. Please try again later.")
                "ERROR_NETWORK_REQUEST_FAILED" -> ResultCallBack.Error(NetworkError.NETWORK, "A network error occurred. Please check your internet connection.")
                else -> ResultCallBack.Error(NetworkError.SERVER, "Something went wrong. Please try again later.")
            }
        }
        is FirebaseNetworkException -> ResultCallBack.Error(NetworkError.NETWORK, "Check your internet connection.")
        else -> ResultCallBack.Error(NetworkError.SERVER, "Something went wrong. Please try again later.")
    }
}

