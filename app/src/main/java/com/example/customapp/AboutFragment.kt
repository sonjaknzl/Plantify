package com.example.customapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView


class AboutFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val preferences =
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(view.context)
        val prefValue = preferences.getString("username", "")
        val username = view.findViewById<EditText>(R.id.usernameEdit)
        username.setText(prefValue.toString())

        //DELETE BUTTON
        val addBtn = view.findViewById<Button>(R.id.resetBtn)
        addBtn.setOnClickListener {
            val db = DatabaseHelper(view.context, null)
            db.deleteTable()
            Toast.makeText(view.context, "Plants were deleted!", Toast.LENGTH_LONG).show()
        }

        // SAVE BTN
        val saveBtn = view.findViewById<Button>(R.id.saveBtn)
        saveBtn.setOnClickListener {
            val appSharedPrefs =
                androidx.preference.PreferenceManager.getDefaultSharedPreferences(it.context.applicationContext)
            val prefsEditor = appSharedPrefs.edit()
            prefsEditor.putString("username", username.text.toString())
            prefsEditor.apply()

            val navigationView = activity?.findViewById<View>(R.id.nav_view) as NavigationView
            val headerView = navigationView.getHeaderView(0)
            val navUsername = headerView.findViewById<View>(R.id.username) as TextView
            navUsername.text = username.text.toString()
            Toast.makeText(view.context, "Username was saved!", Toast.LENGTH_LONG).show()

        }
    }
}