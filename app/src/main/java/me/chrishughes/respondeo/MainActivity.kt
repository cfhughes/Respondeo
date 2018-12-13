package me.chrishughes.respondeo

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import me.chrishughes.respondeo.api.AuthInfo
import me.chrishughes.respondeo.ui.login.LoginFragment
import okhttp3.HttpUrl
import javax.inject.Inject

const val CLIENT_ID = "41pftitfbicm58edvg0fr90mkr"
const val API_SCOPE = "rsvp"
const val REDIRECT_URI = "me.chrishughes.respondeo:/oauth2redirect"
const val REDIRECT_URI_ROOT = "me.chrishughes.respondeo"
const val ERROR_CODE = "error"
const val CLIENT_SECRET = "br5krihft6nvo5jq2cp64ptmd8"
const val GRANT_TYPE_AUTHORIZATION_CODE = "authorization_code"
const val RC_AUTH = 15

class MainActivity : AppCompatActivity(), HasSupportFragmentInjector, LoginFragment.LoginProvider {

    @Inject
    lateinit var authInfo: AuthInfo

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    private var prefs: SharedPreferences? = null

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        prefs = this.getPreferences(Context.MODE_PRIVATE)

        val token = getFromPrefs()
        if (token != null && !token.isEmpty()){
            authInfo.accessToken = token
            navigateToCalendar()
        }

        val data = intent.data
        if (data != null && !TextUtils.isEmpty(data.scheme)) {
            if (REDIRECT_URI_ROOT == data.scheme) {
                val uri = Uri.parse(data.toString().replace('#','?'))
                val newToken = uri.getQueryParameter("access_token")
                val error = uri.getQueryParameter(ERROR_CODE)
                if (newToken != null && !TextUtils.isEmpty(newToken)) {
                    Log.v("Access", newToken)
                    authInfo.accessToken = newToken
                    saveToPrefs(newToken)
                    navigateToCalendar()
                    /*val retrofit = Retrofit.Builder()
                        .addConverterFactory(GsonConverterFactory.create())
                        .baseUrl("https://secure.meetup.com/")
                        .build()
                    val oAuthServer = retrofit.create(OAuthService::class.java)
                    val getRequestTokenFormCall = oAuthServer.requestTokenForm(
                        code,
                        CLIENT_ID,
                        CLIENT_SECRET,
                        REDIRECT_URI,
                        GRANT_TYPE_AUTHORIZATION_CODE
                    )
                    getRequestTokenFormCall.enqueue(object : Callback<OAuthToken> {
                        override fun onFailure(call: Call<OAuthToken>, t: Throwable) {
                            Log.e("Login","The call getRequestTokenFormCall failed", t)
                        }

                        override fun onResponse(call: Call<OAuthToken>, response: Response<OAuthToken>) {
                            authInfo.accessToken = response.body()!!.accessToken!!
                            saveToPrefs(response.body()!!.accessToken!!)
                            navigateToCalendar()
                        }
                    })*/
                }
                if (!TextUtils.isEmpty(error)) {
                    //a problem occurs, the user reject our granting request or something like that
                    Toast.makeText(this, "Auth Error", Toast.LENGTH_LONG).show()
                    Log.e("Login", "onCreate: handle result of authorization with error :$error")
                }
            }
        }

    }

    private fun navigateToCalendar() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.container) as NavHostFragment
        val controller = navHostFragment.navController
        val options: NavOptions? = NavOptions.Builder().setPopUpTo(R.id.main, true).build()
        controller.navigate(R.id.calendarFragment, null, options)
    }

    override fun doLogin() {
        val authorizeUrl = HttpUrl.parse("https://secure.meetup.com/oauth2/authorize")
            ?.newBuilder()
            ?.addQueryParameter("client_id", CLIENT_ID)
            ?.addQueryParameter("scope", API_SCOPE)
            ?.addQueryParameter("redirect_uri", REDIRECT_URI)
            ?.addQueryParameter("response_type", "token")
            ?.build()
        /*val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(authorizeUrl!!.url().toString())
        startActivity(i)*/

        val builder = CustomTabsIntent.Builder()
        val intent = builder.build()
        intent.launchUrl(this, Uri.parse(authorizeUrl!!.url().toString()))//pass the url you need to open
    }

    private val TOKEN_KEY = "meetup_token"

    fun saveToPrefs(token: String){
        val editor = prefs!!.edit()
        editor.putString(TOKEN_KEY,token)
        editor.apply()
    }

    fun getFromPrefs() : String?{
        return prefs?.getString(TOKEN_KEY,null)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.options, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item!!.itemId) {
            R.id.logout -> {
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logout() {
        authInfo.accessToken = ""
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.container) as NavHostFragment
        val controller = navHostFragment.navController
        val options: NavOptions? = NavOptions.Builder().setPopUpTo(R.id.main, true).build()
        controller.navigate(R.id.loginFragment, null, options)
    }

    override fun supportFragmentInjector() = dispatchingAndroidInjector
}

