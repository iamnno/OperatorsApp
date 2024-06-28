package com.nkt.operatorsapp.ui.auth

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.snackbar.Snackbar
import com.nkt.operatorsapp.AuthUIState
import com.nkt.operatorsapp.MainActivity
import com.nkt.operatorsapp.R
import com.nkt.operatorsapp.data.UserType
import com.nkt.operatorsapp.databinding.FragmentAuthBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val TAG = "AuthFragment"

@AndroidEntryPoint
class AuthFragment : Fragment() {

    private lateinit var binding: FragmentAuthBinding

    private val viewModel by viewModels<AuthViewModel>()

    override fun onAttach(context: Context) {
        super.onAttach(context)

//        requireActivity().onBackPressedDispatcher.addCallback {
//            requireActivity().finishAffinity()
//        }
    }

    override fun onResume() {
        super.onResume()

        (requireActivity() as MainActivity).setTopAppBarTitle(getString(R.string.auth_title))
        (requireActivity() as MainActivity).setupAuthScreen()

    }

    private fun setupBinding() {
        binding.signInButton.setOnClickListener {
            val username = binding.loginEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            viewModel.signIn(username, password)
        }
    }

    private fun observeAuthState() {
        lifecycleScope.launch {
            viewModel.authState.collect {
                when (it) {
                    AuthUIState.Loading -> {
                        showMessage("Loading...")
                    }

                    AuthUIState.NotSignedIn -> {}
                    is AuthUIState.SignedIn -> {
                        showMessage("You are logged in!")

                        (requireActivity() as MainActivity).openUserScreen(it.user)
                    }

                    AuthUIState.Empty -> {}
                    is AuthUIState.Failure -> {
                        showMessage(it.message)
                    }
                }
            }
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT)
            .show()
    }

    @SuppressLint("UnsafeRepeatOnLifecycleDetector")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_auth, container, false)
        binding = FragmentAuthBinding.bind(view)

        setupBinding()
        observeAuthState()

        return binding.root
    }

}