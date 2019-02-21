package me.chrishughes.respondeo.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import me.chrishughes.respondeo.R
import me.chrishughes.respondeo.databinding.LoginFragmentBinding
import me.chrishughes.respondeo.di.Injectable

class LoginFragment : Fragment(), Injectable {

    lateinit var loginProvider : LoginProvider

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<LoginFragmentBinding>(
            inflater,
            R.layout.login_fragment,
            container,
            false
        )

        dataBinding.loginButton.setOnClickListener {
            run {
                loginProvider.doLogin()
            }
        }

        return dataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (activity is LoginProvider){
            loginProvider = activity as LoginProvider
        }
    }

    interface LoginProvider {
        fun doLogin()
    }
}