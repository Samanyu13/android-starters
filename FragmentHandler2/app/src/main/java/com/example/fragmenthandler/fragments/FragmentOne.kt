package com.example.fragmenthandler.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.fragmenthandler.R
import com.example.fragmenthandler.viemodel.SharedViewModel
import kotlinx.coroutines.launch

class FragmentOne : Fragment(R.layout.fragment_view) {

    val viewModel: SharedViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as? AppCompatActivity)?.supportActionBar?.title = "Fragment One"

        val editText = view.findViewById<EditText>(R.id.editText)
        val button = view.findViewById<Button>(R.id.btnNext)

        button.text = getString(R.string.goto_2nd_fragment)
        button.setOnClickListener {
            viewModel.setSharedText(editText.text.toString())
            findNavController().navigate(R.id.action_one_to_two)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.sharedText.collect {
                    editText.setText(it)
                }
            }
        }
    }
}