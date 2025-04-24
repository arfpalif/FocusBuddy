package com.example.focusbuddy.ui.intro

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.focusbuddy.R
import com.example.focusbuddy.data.model.QuickSetupViewModel
import com.example.focusbuddy.databinding.FragmentIntro3Binding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentIntro3.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentIntro3 : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val viewModel: QuickSetupViewModel by activityViewModels()

    private lateinit var binding: FragmentIntro3Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_intro3, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentIntro3Binding.bind(view)
        binding.loginButton.setOnClickListener {
            val terganggu = when {
                binding.btnIya.isSelectedChoice -> "Iya"
                binding.btnTidak.isSelectedChoice -> "Tidak"
                else -> null
            }
            val alasan = binding.edAlasan.text.toString()

            viewModel.terganggu = terganggu != null
            viewModel.alasanGangguan = alasan

            binding.btnIya.isSelectedChoice = true
            findNavController().navigate(R.id.action_fragmenIntro3_to_fragmentIntro4)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmenIntro3.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentIntro3().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}