package com.nkt.operatorsapp.ui.auth

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.nkt.operatorsapp.MainActivity
import com.nkt.operatorsapp.R
import com.nkt.operatorsapp.data.UserType
import com.nkt.operatorsapp.databinding.FragmentAuthBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

private const val TAG = "AuthFragment"

@AndroidEntryPoint
class AuthFragment : Fragment() {

    private lateinit var binding: FragmentAuthBinding

    private val viewModel by viewModels<AuthViewModel>()


    override fun onAttach(context: Context) {
        super.onAttach(context)

        (requireActivity() as MainActivity).binding.topAppBar.setTitle(getString(R.string.auth_title))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (requireActivity() as MainActivity).binding.topAppBar.setTitle(getString(R.string.auth_title))
    }

    @SuppressLint("UnsafeRepeatOnLifecycleDetector")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_auth, container, false)
        binding = FragmentAuthBinding.bind(view)

        binding.signInButton.setOnClickListener {
            val username = binding.loginEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            viewModel.signIn(username, password)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.isUserSignedIn.collect {
                    when (it) {
                        AuthUiState.Loading -> {

                        }

                        is AuthUiState.Loaded -> {
                            val navHostFragment =
                                (requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment)
                            val navController = navHostFragment.navController

                            when (it.user.type) {
                                UserType.OPERATOR_1 -> {
                                    navController.navigate(R.id.action_authFragment_to_firstOperatorFragment)
                                }

                                UserType.OPERATOR_2 -> {
                                    navController.navigate(R.id.action_authFragment_to_secondOperatorFragment)
                                }

                                UserType.ADMINISTRATOR -> {
                                    navController.navigate(R.id.action_authFragment_to_usersFragment)
                                }
                            }
                        }

                        AuthUiState.NotSignedIn -> {
                            Snackbar.make(
                                requireContext(),
                                binding.root,
                                "Не вдалося авторизуватися! Спробуйте знову!",
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }

        return binding.root
    }

}