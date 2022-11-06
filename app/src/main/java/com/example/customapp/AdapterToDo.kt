package com.example.customapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class AdapterToDo(
    private val data: MutableList<Plant>
) : RecyclerView.Adapter<AdapterToDo.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterToDo.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater
            .inflate(R.layout.layout_row_todo, parent, false) as View
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: AdapterToDo.ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    inner class ViewHolder(private val v: View) : RecyclerView.ViewHolder(v) {
        private val name: TextView = v.findViewById(R.id.name)
        private val species: TextView = v.findViewById(R.id.species)
        private val icon: ImageView = v.findViewById(R.id.icon)
        private val nextWaterDate: TextView = v.findViewById(R.id.nextWaterDate)


        fun bind(item: Plant) {
            name.text = item.name
            val dropdown = v.resources.getStringArray(R.array.options)
            species.text = dropdown[item.species]
            nextWaterDate.text = item.nextWaterDate

            //WATER BUTTON
            val waterBtn = v.findViewById<Button>(R.id.waterBtn)
            waterBtn?.setOnClickListener {
                val current = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                val formatted = current.format(formatter)


                item.waterDate = formatted
                val db = DatabaseHelper(v.context, null)
                db.updatePlantByName(item.name, item.species, item.purchaseDate, item.waterDate)

                val activity = it.context as MainActivity
                (activity as MainActivity?)?.replaceFragment(HomeFragment(), "Home")

                Toast.makeText(v.context, "Plant was watered!", Toast.LENGTH_LONG).show()

                //position: Int, name: String, species: Int, purchaseDate: String, wateringDate: String
            }


            fun String.removeWhitespaces() = replace(" ", "")
            val uri = "drawable/" + dropdown[item.species].lowercase().removeWhitespaces()
            val res: Int = v.context.resources.getIdentifier(uri, null, v.context.packageName)
            icon.setImageResource(res)

        }

    }

}