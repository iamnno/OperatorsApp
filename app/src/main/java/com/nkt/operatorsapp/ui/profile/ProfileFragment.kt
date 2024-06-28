package com.nkt.operatorsapp.ui.profile

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.nkt.operatorsapp.MainActivity
import com.nkt.operatorsapp.R
import com.nkt.operatorsapp.data.UserType
import com.nkt.operatorsapp.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val TAG = "ProfileFragment"

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding

    private val viewModel by viewModels<ProfileViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (requireActivity() as MainActivity).setTopAppBarTitle(getString(R.string.profile))
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (requireActivity() as MainActivity).hideProfile()
    }

    override fun onDetach() {
        super.onDetach()

        (requireActivity() as MainActivity).showProfile()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        binding = FragmentProfileBinding.bind(view)

        lifecycleScope.launch {
            viewModel.profileState.collect {
                when (it) {
                    is ProfileState.Loaded -> {
                        val username = it.user.username
                        val userType = it.user.type.name

                        binding.usernameText.text = username
                        binding.userTypeText.text = when (userType) {
                            UserType.ADMINISTRATOR.name -> getString(R.string.administrator)
                            UserType.OPERATOR_1.name -> getString(R.string.operator_1)
                            else -> getString(R.string.operator_2)
                        }
                    }

                    ProfileState.Loading -> {}
                }
            }
        }

        arguments?.let {

        }
        binding.backButton.setOnClickListener {
            val navHostFragment =
                (requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment)
            val navController = navHostFragment.navController

            navController.popBackStack()
        }
        binding.returnButton.setOnClickListener {
            viewModel.signOut()

            val navHostFragment =
                (requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment)
            val navController = navHostFragment.navController

            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.authFragment, true)
                .build()

            navController.clearBackStack(R.id.authFragment)
            navController.navigate(R.id.authFragment, null, navOptions)
        }

        return binding.root
    }
}