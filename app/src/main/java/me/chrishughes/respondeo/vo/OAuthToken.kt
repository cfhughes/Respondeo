package me.chrishughes.respondeo.vo

import com.google.gson.annotations.SerializedName

data class OAuthToken(
    @field:SerializedName("access_token")
    var accessToken: String?,
    @field:SerializedName("token_type")
    var tokenType: String?,
    @field:SerializedName("expires_in")
    var expiresIn: Long,
    var expiredAfterMilli: Long,
    @field:SerializedName("refresh_token")
    var refreshToken: String?
)