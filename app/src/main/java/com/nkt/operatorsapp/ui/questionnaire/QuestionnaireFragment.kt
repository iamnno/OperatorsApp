package com.nkt.operatorsapp.ui.questionnaire

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import com.nkt.operatorsapp.R
import com.nkt.operatorsapp.databinding.FragmentQuestionnaireBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class QuestionnaireFragment : Fragment() {

    private lateinit var binding: FragmentQuestionnaireBinding

    private val viewModel by viewModels<QuestionnaireViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.sentState.collect {
                    Snackbar.make(
                        requireContext(),
                        binding.root,
                        getString(if (it) R.string.key_word_send_error else R.string.key_wod_was_successfuly_sent),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_questionnaire, container, false)
        binding = FragmentQuestionnaireBinding.bind(view)

        with(binding) {
            sendButton.setOnClickListener {
                sendKeyWord(word = this.keyWordEditText.text.toString())
                binding.keyWordEditText.text.clear()
            }
        }

        return binding.root
    }

    private fun sendKeyWord(word: String) {
        viewModel.send(keyWord = word.trim())
    }

}