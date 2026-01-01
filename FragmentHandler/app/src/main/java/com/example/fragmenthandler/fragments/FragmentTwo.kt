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
import com.example.fragmenthandler.R
import com.example.fragmenthandler.viemodel.SharedViewModel
import kotlinx.coroutines.launch
import kotlin.getValue

class FragmentTwo : Fragment(R.layout.fragment_view) {

    val viewModel: SharedViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as? AppCompatActivity)?.supportActionBar?.title = "Fragment Two"

        val editText = view.findViewById<EditText>(R.id.editText)
        val button = view.findViewById<Button>(R.id.btnNext)

        button.text = getString(R.string.goto_3rd_fragment)
        button.setOnClickListener {
            viewModel.setSharedText(editText.text.toString())
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, FragmentThree())
                .addToBackStack(null)
                .commit()
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