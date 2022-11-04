package com.example.customapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView


open class MainActivity : AppCompatActivity(), HomeFragment.OnDataPass {
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var drawerLayout: DrawerLayout
    var list = mutableListOf<Plant>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //
        
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.frameLayout, HomeFragment())
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()

        //this.deleteDatabase("PlantLibrary.db")

        // SET DRAWERLAYOUT
        drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //LISTENER DRAWER
        navView.setNavigationItemSelectedListener {

            it.isChecked = true
            Log.i("INFO", list.toString())
            when (it.itemId) {
                R.id.firstDrawerItem -> replaceFragment(HomeFragment(),it.title.toString())
                R.id.secondDrawerItem -> replaceFragment(ToDoFragment.newInstance(ArrayList(list)),it.title.toString())
                R.id.thirdDrawerItem -> replaceFragment(AboutFragment(),it.title.toString())
            }
            true
        }


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun replaceFragment(fragment: Fragment, title: String){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
        drawerLayout.closeDrawers()
        setTitle(title)
    }

    override fun onDataPass(plantList: MutableList<Plant>) {
        list = plantList
    }

}