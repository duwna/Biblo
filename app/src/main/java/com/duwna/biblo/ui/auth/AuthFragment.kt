package com.duwna.biblo.ui.auth

import android.content.Intent
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.duwna.biblo.R
import com.duwna.biblo.ui.base.BaseFragment
import com.duwna.biblo.ui.base.IViewModelState
import com.duwna.biblo.utils.hideKeyBoard
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_auth.*


class AuthFragment : BaseFragment<AuthViewModel>() {
    override val viewModel: AuthViewModel by viewModels()
    override val layout: Int = R.layout.fragment_auth

    override fun setupViews() {

        btn_google_sign_in.setOnClickListener {
            googleSignIn()
        }

        btn_registration.setOnClickListener {
            findNavController().navigate(R.id.action_auth_to_registration)
        }

        btn_enter.setOnClickListener {
            root.hideKeyBoard(container)
            viewModel.enter(et_email.text.toString(), et_sum.text.toString())
        }

        tv_forgot_password.setOnClickListener {
            if (et_email.text.toString().isBlank()) {
                Snackbar.make(
                    container,
                    getString(R.string.message_enter_email),
                    Snackbar.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            Snackbar.make(
                container,
                "${getString(R.string.message_send_password_link)} ${et_email.text}?",
                Snackbar.LENGTH_LONG
            ).apply {
                setAction(getString(R.string.label_send)) { viewModel.resetPassword(et_email.text.toString()) }
                view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)?.maxLines =
                    5
                show()
            }
        }
    }

    override fun bindState(state: IViewModelState) {
        state as AuthState
        showViews(state.isLoading)

        state.ready?.let { findNavController().navigate(R.id.action_auth_to_groups) }
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

    private fun showViews(isLoading: Boolean) {
        container.isVisible = !isLoading
        wave_view.isVisible = isLoading
    }

}