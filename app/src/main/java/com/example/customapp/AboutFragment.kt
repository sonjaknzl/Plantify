package com.example.customapp

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton


class AboutFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(view.context)
        val prefValue = preferences.getString("username","")
        val username = view.findViewById<EditText>(R.id.usernameEdit)
        username.setText(prefValue.toString())
        Log.i("INFO", prefValue.toString())

        val saveBtn = view.findViewById<Button>(R.id.saveBtn)
        saveBtn.setOnClickListener {
            val appSharedPrefs =
                androidx.preference.PreferenceManager.getDefaultSharedPreferences(it.context.applicationContext)
            val prefsEditor = appSharedPrefs.edit()
            prefsEditor.putString("username", username.text.toString())
            prefsEditor.apply()


        }
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AboutFragment().apply {
            }
    }
}