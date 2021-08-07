package com.duwna.biblo.ui.auth

import android.app.Activity
import android.app.AlertDialog
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.duwna.biblo.R
import com.duwna.biblo.ui.base.BaseFragment
import com.duwna.biblo.ui.base.IViewModelState
import com.duwna.biblo.utils.hideKeyBoard
import com.duwna.biblo.utils.showSnackBar
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.android.synthetic.main.fragment_auth.*


class AuthFragment : BaseFragment<AuthViewModel>() {
    override val viewModel: AuthViewModel by viewModels()
    override val layout: Int = R.layout.fragment_auth

    private val googleSignInResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                viewModel.firebaseAuthWithGoogle(task.result?.idToken)
            }
        }

    override fun setupViews() {

        btn_google_sign_in.setOnClickListener {
            googleSignIn()
        }

        btn_registration.setOnClickListener {
            findNavController().navigate(R.id.action_auth_to_registration)
        }

        btn_enter.setOnClickListener {
            it.hideKeyBoard()
            viewModel.enter(et_email.text.toString(), et_sum.text.toString())
        }

        tv_forgot_password.setOnClickListener {
            resetPassword()
        }
    }

    override fun bindState(state: IViewModelState) {
        state as AuthState

        state.ready?.let { findNavController().navigate(R.id.action_auth_to_groups) }
    }

    private fun googleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(root, gso)

        val signInIntent = googleSignInClient.signInIntent
        googleSignInResult.launch(signInIntent)
    }

    private fun resetPassword() {
        val email = et_email.text.toString()
        if (email.isBlank()) {
            container.showSnackBar(getString(R.string.message_enter_email))
            return
        }
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.label_password_reset))
            .setMessage("${getString(R.string.message_send_password_link_question)} $email?")
            .setPositiveButton(R.string.label_send) { _, _ -> viewModel.resetPassword(email) }
            .setNegativeButton(R.string.btn_cancel) { _, _ -> }
            .show()
    }
}