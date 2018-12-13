package me.chrishughes.respondeo.api

import me.chrishughes.respondeo.vo.OAuthToken
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface OAuthService {

    @FormUrlEncoded
    @POST("oauth2/access")
    fun requestTokenForm(
        @Field("code") code: String,
        @Field("client_id") client_id: String,
        @Field("client_secret") client_secret: String,
        @Field("redirect_uri") redirect_uri: String,
        @Field("grant_type") grant_type: String
    ): Call<OAuthToken>

}