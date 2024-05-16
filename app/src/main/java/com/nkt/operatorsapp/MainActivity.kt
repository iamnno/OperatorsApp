package com.nkt.operatorsapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import com.nkt.operatorsapp.data.UserType
import com.nkt.operatorsapp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel>()

    val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.isUserSignedIn.collect { state ->
                    when (state) {
                        MainUiState.Loading -> {
                        }

                        is MainUiState.Loaded -> {
                            val navHostFragment =
                                supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
                            val navController = navHostFragment.navController
                            val bundle = bundleOf(
                                "username" to state.user.username,
                                "type" to state.user.type.name
                            )

                            when (state.user.type) {
                                UserType.OPERATOR_1 -> {
//                                    navController.navigate(R.id.action_authFragment_to_firstOperatorFragment)
                                    binding.topAppBar.setNavigationOnClickListener {
                                        navController.navigate(
                                            R.id.action_firstOperatorFragment_to_profileFragment,
                                            bundle
                                        )
                                    }
                                }

                                UserType.OPERATOR_2 -> {
//                                    navController.navigate(R.id.action_authFragment_to_secondOperatorFragment)
                                    binding.topAppBar.setNavigationOnClickListener {
                                        navController.navigate(
                                            R.id.action_secondOperatorFragment_to_profileFragment,
                                            bundle
                                        )
                                    }
                                }

                                UserType.ADMINISTRATOR -> {
//                                    navController.navigate(R.id.action_authFragment_to_usersFragment)
                                    binding.topAppBar.setNavigationOnClickListener {
                                        navController.navigate(
                                            R.id.action_usersFragment_to_profileFragment,
                                            bundle
                                        )
                                    }
                                }
                            }

                        }

                        MainUiState.NotSignedIn -> {
                            val navHostFragment =
                                supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
                            val navController = navHostFragment.navController

                            binding.topAppBar.setNavigationOnClickListener {
                                navController.navigate(R.id.action_authFragment_to_questionnaireFragment)
                            }

                        }
                    }
                }
            }
        }

    }


}