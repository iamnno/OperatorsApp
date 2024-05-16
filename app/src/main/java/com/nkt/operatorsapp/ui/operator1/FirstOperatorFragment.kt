package com.nkt.operatorsapp.ui.operator1

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.nkt.operatorsapp.MainActivity
import com.nkt.operatorsapp.R
import com.nkt.operatorsapp.data.RemoteParamsRepository.Companion.PARAM_1
import com.nkt.operatorsapp.data.RemoteParamsRepository.Companion.PARAM_2
import com.nkt.operatorsapp.data.RemoteParamsRepository.Companion.PARAM_3
import com.nkt.operatorsapp.databinding.FragmentFirstOperatorBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


@AndroidEntryPoint
class FirstOperatorFragment : Fragment() {

    private lateinit var binding: FragmentFirstOperatorBinding

    private val viewModel by viewModels<FirstOperatorViewModel>()

    override fun onStart() {
        super.onStart()

        (requireActivity() as MainActivity).binding.topAppBar.setTitle(R.string.operator_1)
    }

    @SuppressLint("UnsafeRepeatOnLifecycleDetector")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_first_operator, container, false)
        binding = FragmentFirstOperatorBinding.bind(view)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        FirstOperatorViewModel.UiState.Loading -> {}
                        is FirstOperatorViewModel.UiState.Loaded -> {
                            val params = state.params

                            with(binding) {
                                param1Text.text = params[PARAM_1]
                                param2Text.text = params[PARAM_2]
                                param3Text.text = params[PARAM_3]
                            }
                        }
                    }
                }
            }
        }

        return binding.root
    }

}