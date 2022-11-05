package com.example.customapp

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


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

            val array = getNextWaterDateAndInfo(db, plSpecies, plWateringDate)
            list.add(Plant(plName, plSpecies, plPurchaseDate, plWateringDate,
                array[0].toString(), array[1].toString()
            ))

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
                //Log.i("INFO", db.getNextWateringDate(plSpecies))

                val array = getNextWaterDateAndInfo(db, plSpecies, plWateringDate)
                list.add(Plant(plName, plSpecies, plPurchaseDate, plWateringDate,
                    array[0].toString(), array[1].toString()
                ))
            }
        }
        db?.close()
    }

    private fun getNextWaterDateAndInfo(db: DatabaseHelper, plSpecies: Int, plWateringDate: String): Array<String?> {
        // get nextWaterDate
        val deltaWater = db.getNextWateringDate(resources.getStringArray(R.array.options)[plSpecies])
        var formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        var date: LocalDate = LocalDate.parse(plWateringDate, formatter)
        val nextWaterDate = deltaWater?.let { date.plusDays(it.toLong()) }?.format(formatter).toString()

        // get infoText
        val infoText = db.getInfo(resources.getStringArray(R.array.options)[plSpecies])
        if(infoText != null){
            return arrayOf(nextWaterDate, infoText)
        }
        return arrayOf(nextWaterDate, "")
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