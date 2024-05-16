package com.nkt.operatorsapp.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import com.nkt.operatorsapp.MainActivity
import com.nkt.operatorsapp.R
import com.nkt.operatorsapp.data.UserType
import com.nkt.operatorsapp.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "ProfileFragment"

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding

    private val viewModel by viewModels<ProfileViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (requireActivity() as MainActivity).binding.topAppBar.title = getString(R.string.profile)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        binding = FragmentProfileBinding.bind(view)

        arguments?.let {
            val username = it.getString("username")
            val userType = it.getString("type")

            binding.usernameText.text = username
            binding.userTypeText.text = when (userType) {
                UserType.ADMINISTRATOR.name -> getString(R.string.administrator)
                UserType.OPERATOR_1.name -> getString(R.string.operator_1)
                else -> getString(R.string.operator_2)
            }
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

            navController.navigate(R.id.action_profileFragment_to_authFragment)
        }

        return binding.root
    }
}