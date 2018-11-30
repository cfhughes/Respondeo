package me.chrishughes.respondeo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import me.chrishughes.respondeo.api.AuthInfo
import me.chrishughes.respondeo.ui.login.LoginFragment
import okhttp3.HttpUrl
import javax.inject.Inject


class MainActivity : AppCompatActivity(), HasSupportFragmentInjector, LoginFragment.LoginProvider {

    private val CLIENT_ID = "83oedf13673j51ce2if14b92j7"
    private val API_SCOPE = "rsvp"
    private val REDIRECT_URI = "me.chrishughes.respondeo:/oauth2redirect"
    private val REDIRECT_URI_ROOT = "me.chrishughes.respondeo"
    private val ERROR_CODE = "error"
    private val GRANT_TYPE_AUTHORIZATION_CODE = "authorization_code"
    private val RC_AUTH = 15

    @Inject
    lateinit var authInfo: AuthInfo

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val data = intent.data
        if (data != null && !TextUtils.isEmpty(data.scheme)) {
            if (REDIRECT_URI_ROOT == data.scheme) {
                val uri = Uri.parse(data.toString().replace('#','?'))
                val access_token = uri.getQueryParameter("access_token")
                val error = data.getQueryParameter(ERROR_CODE)
                if (access_token != null && !TextUtils.isEmpty(access_token)) {
                    Log.v("Access",access_token)
                    authInfo.accessToken = access_token
                    val navHostFragment = supportFragmentManager.findFragmentById(R.id.container) as NavHostFragment
                    val controller = navHostFragment.navController
                    controller.navigate(R.id.calendarFragment)
                }
                if (!TextUtils.isEmpty(error)) {
                    //a problem occurs, the user reject our granting request or something like that
                    Toast.makeText(this, "Auth Error", Toast.LENGTH_LONG).show()
                    Log.e("Login", "onCreate: handle result of authorization with error :$error")
                }
            }
        }

    }

    override fun doLogin() {
        val authorizeUrl = HttpUrl.parse("https://secure.meetup.com/oauth2/authorize")
            ?.newBuilder()
            ?.addQueryParameter("client_id", CLIENT_ID)
            ?.addQueryParameter("scope", API_SCOPE)
            ?.addQueryParameter("redirect_uri", REDIRECT_URI)
            ?.addQueryParameter("response_type", "token")
            ?.build()
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(authorizeUrl!!.url().toString())
        startActivity(i)
    }

    override fun supportFragmentInjector() = dispatchingAndroidInjector
}
