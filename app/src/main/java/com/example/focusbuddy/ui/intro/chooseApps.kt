package com.example.focusbuddy.ui.intro

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.focusbuddy.R
import com.example.focusbuddy.databinding.FragmentChooseAppsBinding
import com.example.focusbuddy.ui.adapter.AppChoiceAdapter
import com.example.focusbuddy.ui.adapter.AppInfo

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [chooseApps.newInstance] factory method to
 * create an instance of this fragment.
 */
class chooseApps : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentChooseAppsBinding

    private lateinit var adapter: AppChoiceAdapter
    private val selectedApps = mutableListOf<AppInfo>()

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
        return inflater.inflate(R.layout.fragment_choose_apps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentChooseAppsBinding.bind(view)
        val recyclerView:RecyclerView = view.findViewById(R.id.rv_apps)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val apps = getInstalledSocialApps(requireContext())
        adapter = AppChoiceAdapter(apps) { app ->
            if (app.isSelected) selectedApps.add(app)
            else selectedApps.remove(app)
        }
        recyclerView.adapter = adapter

        view.findViewById<Button>(R.id.loginButton).setOnClickListener {
            // Handle selected apps, e.g., send to next screen or save
            selectedApps.forEach {
                Log.d("SelectedApp", it.name)
            }
        }

        binding.loginButton.setOnClickListener {
            // Handle selected apps, e.g., send to next screen or save
            selectedApps.forEach {
                Log.d("SelectedApp", it.name)
            }
            findNavController().navigate(R.id.action_chooseApps_to_resultFragment)

        }
    }

    private fun getInstalledSocialApps(context: Context): List<AppInfo> {
        val pm = context.packageManager
        val apps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        val socialKeywords = listOf("instagram", "facebook", "tiktok", "x", "twitter", "snapchat")

        return apps.filter {
            val lowerName = it.packageName.lowercase()
            socialKeywords.any { keyword -> lowerName.contains(keyword) }
        }.map {
            AppInfo(
                name = pm.getApplicationLabel(it).toString(),
                packageName = it.packageName,
                icon = pm.getApplicationIcon(it)
            )
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment chooseApps.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            chooseApps().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}