package com.nkt.operatorsapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.nkt.operatorsapp.data.User
import com.nkt.operatorsapp.data.UserType
import com.nkt.operatorsapp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel>()

    val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val navController by lazy {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navHostFragment.navController
    }

    fun setTopAppBarTitle(title: String) {
        binding.topAppBar.title = title
    }

    fun setTopAppBarClickListener(listener: View.OnClickListener) {
        binding.topAppBar.setOnClickListener(listener)
    }

    fun hideProfile() {
        binding.topAppBar.navigationIcon = null
    }

    fun showProfile() {
        binding.topAppBar.setNavigationIcon(R.drawable.ic_home_24)
    }

    private fun isUserSignedIn() {
        lifecycleScope.launch {
            viewModel.authState.collect {
                when (it) {
                    AuthUIState.Loading -> {
                    }

                    AuthUIState.NotSignedIn -> {
                        setupAuthScreen()
                    }

                    is AuthUIState.SignedIn -> {
                        openUserScreen(it.user)
                    }

                    AuthUIState.Empty -> {}
                    is AuthUIState.Failure -> {
                        Toast.makeText(this@MainActivity, it.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    fun openUserScreen(user: User) {
        when (user.type) {
            UserType.OPERATOR_1 -> openFirstOperatorScreen()
            UserType.OPERATOR_2 -> openSecondOperatorScreen()
            UserType.ADMINISTRATOR -> openAdminScreen()
        }
    }

    fun setupAuthScreen() {
        navController.clearBackStack(R.id.authFragment)

        binding.topAppBar.setNavigationOnClickListener {
            navController.navigate(R.id.questionnaireFragment)
        }
    }

    private fun openFirstOperatorScreen() {
        setTopAppBar()

        navController.navigate(R.id.firstOperatorFragment)
    }

    private fun openSecondOperatorScreen() {

        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.authFragment, true)
            .build()

        setTopAppBar()

        navController.navigate(R.id.secondOperatorFragment, null, navOptions)
    }

    private fun openAdminScreen() {
        setTopAppBar()

        navController.navigate(R.id.usersFragment)
    }

    fun setTopAppBar() {
        binding.topAppBar.setNavigationOnClickListener {
            navController.navigate(
                R.id.profileFragment,
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        isUserSignedIn()
    }
}