package com.nkt.operatorsapp.ui.users

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.nkt.operatorsapp.MainActivity
import com.nkt.operatorsapp.R
import com.nkt.operatorsapp.data.User
import com.nkt.operatorsapp.data.UserType
import com.nkt.operatorsapp.databinding.FragmentUsersBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

private const val TAG = "UsersFragment"

@AndroidEntryPoint
class UsersFragment : Fragment() {

    private lateinit var binding: FragmentUsersBinding
    private lateinit var usersAdapter: UsersAdapter
    private lateinit var queriesAdapter: QueriesAdapter

    private val viewModel by viewModels<UsersViewModel>()

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (requireActivity() as MainActivity).binding.topAppBar.setTitle(R.string.administrator)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("UnsafeRepeatOnLifecycleDetector", "NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_users, container, false)
        binding = FragmentUsersBinding.bind(view)

        setupUsersList()
        setupQueriesList()
        setupSpinner()

        binding.createUserButton.setOnClickListener {
            val username = binding.usernameEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val type =
                requireContext().convertUserType(type = binding.userTypeSpinner.selectedItem.toString())

            viewModel.createUser(username, type, password)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        UsersUiState.Loading -> {}
                        is UsersUiState.Loaded -> {
                            queriesAdapter.queries = state.queries
                            queriesAdapter.notifyDataSetChanged()
                            usersAdapter.users = state.users
                            usersAdapter.notifyDataSetChanged()
                        }
                    }

                }
            }
        }

        return binding.root
    }

    private fun setupSpinner() {
        val types: Array<String> = requireContext().run {
            arrayOf(
                getString(R.string.operator_1),
                getString(R.string.operator_2),
                getString(R.string.administrator)
            )
        }
        val adapter =
            ArrayAdapter<String>(requireContext(), R.layout.dropdown_user_type_item, types)
        binding.userTypeSpinner.adapter = adapter
    }

    private fun Context.convertUserType(type: String): UserType = when (type) {
        getString(R.string.operator_1) -> UserType.OPERATOR_1
        getString(R.string.operator_2) -> UserType.OPERATOR_2
        else -> UserType.ADMINISTRATOR
    }

    private fun setupQueriesList() {
        queriesAdapter = QueriesAdapter { query ->
            viewModel.deleteQuery(query)
        }
        binding.queriesList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.queriesList.adapter = queriesAdapter
    }

    private fun setupUsersList() {
        usersAdapter = UsersAdapter { user ->
            viewModel.deleteUser(user)
        }

        binding.usersList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.usersList.adapter = usersAdapter
    }
}