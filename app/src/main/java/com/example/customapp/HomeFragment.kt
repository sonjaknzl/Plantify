package com.example.customapp

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SimpleAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class HomeFragment : Fragment() {


    interface OnDataPass {
        fun onDataPass(data: MutableList<Plant>)
    }

    lateinit var dataPasser: OnDataPass

    override fun onAttach(context: Context) {
        super.onAttach(context)
        dataPasser = context as OnDataPass
    }

    lateinit var recyclerView: RecyclerView
    lateinit var adapter: Adapter
    var list = mutableListOf<Plant>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun showInfo(item: Plant) {
        item.visibility = !item.visibility
    }

    override fun onStop() {
        super.onStop()
        list.forEach {
            it.visibility = false
        }
        recyclerView.adapter?.notifyDataSetChanged()
        //Log.i("INFO", list.toString())
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        //RECYCLERVIEW
        dataInArray()
        passData(list)
        recyclerView = view.findViewById<RecyclerView>(R.id.recycler)
        adapter = Adapter(list) { showInfo(it) }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        //ADD BUTTON
        val addBtn = view.findViewById<FloatingActionButton>(R.id.addBtn)
        addBtn.setOnClickListener {
            (activity as MainActivity?)?.replaceFragment(AddFragment(), "Add Plant")
        }

        //SWIPE TO DELETE
        val swipeHandler = object : SwipeToDeleteCallback(view.context) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                adapter.removeAt(position)
                val db = DatabaseHelper(view.context, null)
                db.deletePlant(position)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    fun passData(data: MutableList<Plant>){
        dataPasser.onDataPass(data)
    }

    //FETCH DATA FROM DB IN LIST
    @SuppressLint("Range")
    fun dataInArray() {
        val db = view?.let { DatabaseHelper(it.context, null) }
        val cursor = db?.getName()

        // moving cursor to first position
        if (cursor != null && cursor.count > 0) {

            cursor.moveToFirst()
            val plName: String = cursor.getString(cursor.getColumnIndex(DatabaseHelper.NAME_COl))
            val plSpecies: Int = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.SPECIES_COL))
            val plPurchaseDate: String = cursor.getString(cursor.getColumnIndex(DatabaseHelper.PURCHASEDATE_COL))
            val plWateringDate: String = cursor.getString(cursor.getColumnIndex(DatabaseHelper.WATERINGDATE_COL))
            list.add(Plant(plName, plSpecies, plPurchaseDate, plWateringDate))

            // moving cursor to next position
            while (cursor.moveToNext()) {
                val plName: String =
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.NAME_COl))
                val plSpecies: Int =
                    cursor.getInt(cursor.getColumnIndex(DatabaseHelper.SPECIES_COL))
                val plPurchaseDate: String =
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.PURCHASEDATE_COL))
                val plWateringDate: String =
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.WATERINGDATE_COL))
                list.add(Plant(plName, plSpecies, plPurchaseDate, plWateringDate))
            }
        }
        db?.close()
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList("dataArray",ArrayList(list))
                }
            }
    }
}