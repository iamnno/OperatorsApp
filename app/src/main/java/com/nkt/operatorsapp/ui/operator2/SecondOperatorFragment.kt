package com.nkt.operatorsapp.ui.operator2

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
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
import com.nkt.operatorsapp.databinding.FragmentSecondOperatorBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SecondOperatorFragment : Fragment() {

    private lateinit var binding: FragmentSecondOperatorBinding

    private val viewModel by viewModels<SecondOperatorViewModel>()

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (requireActivity() as MainActivity).binding.topAppBar.setTitle(R.string.operator_2)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("UnsafeRepeatOnLifecycleDetector")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_second_operator, container, false)
        binding = FragmentSecondOperatorBinding.bind(view)

        lifecycleScope.launch {
            viewModel.params.collect {
                when (it) {
                    UiState.Loading -> {}
                    is UiState.Loaded -> {
                        binding.param1EditText.setText(it.params[PARAM_1].toString())
                        binding.param2EditText.setText(it.params[PARAM_2].toString())
                        binding.param3EditText.setText(it.params[PARAM_3].toString())
                    }
                }

            }
        }

        binding.saveAndSendButton.setOnClickListener {
            val params = mutableMapOf<String, String>()
            params[PARAM_1] = binding.param1EditText.text.toString()
            params[PARAM_2] = binding.param2EditText.text.toString()
            params[PARAM_3] = binding.param3EditText.text.toString()

            viewModel.update(params)
        }

        return binding.root
    }

}