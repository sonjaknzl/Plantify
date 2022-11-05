package com.example.customapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList

class ToDoFragment : Fragment() {

    lateinit var list: ArrayList<Plant>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_to_do, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val currentDate = sdf.format(Date())

        list.sortBy { it.waterDate }

        val listToday = mutableListOf<Plant>()
        val listLater = mutableListOf<Plant>()
        for (item in list){
            if(item.nextWaterDate <= currentDate){
                listToday.add(item)
            } else{
                listLater.add(item)
            }
        }

        //RECYCLERVIEW TODAY
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler)
        val adapter = Adapter(listToday) {}
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        //RECYCLERVIEW LATER
        val recyclerViewLater = view.findViewById<RecyclerView>(R.id.recyclerLater)
        val adapterLater = Adapter(listLater) {}
        recyclerViewLater.adapter = adapterLater
        recyclerViewLater.layoutManager = LinearLayoutManager(context)
    }
    companion object {
        @JvmStatic
        fun newInstance(arrayList: ArrayList<Plant>) =
            ToDoFragment().apply {
                list = arrayList
            }
    }
}