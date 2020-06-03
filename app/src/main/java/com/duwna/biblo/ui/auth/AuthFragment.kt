package com.duwna.biblo.ui.auth

import android.content.Intent
import androidx.fragment.app.viewModels
import com.duwna.biblo.R
import com.duwna.biblo.base.BaseFragment
import com.duwna.biblo.base.IViewModelState
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.android.synthetic.main.fragment_auth.*

class AuthFragment : BaseFragment<AuthViewModel>() {
    override val viewModel: AuthViewModel by viewModels()
    override val layout: Int = R.layout.fragment_auth

    override fun setupViews() {

        btn_google_sign_in.setOnClickListener {
            googleSignIn()
        }
    }


    override fun bindState(state: IViewModelState) {

    }

    private fun googleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(root, gso)

        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RC_SIGN_IN -> {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                viewModel.firebaseAuthWithGoogle(task.result?.idToken)
            }
        }
    }

    companion object {
        private const val RC_SIGN_IN = 100
    }


}