package com.example.customapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class Adapter(
    private val data: MutableList<Plant>,
    private val listener: (Plant) -> Unit
) : RecyclerView.Adapter<Adapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Adapter.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater
            .inflate(R.layout.layout_row, parent, false) as View
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: Adapter.ViewHolder, position: Int) {
        val item = data[position]
        holder.const.visibility = if (item.visibility) View.VISIBLE else View.GONE

        holder.bind(item)
    }

    fun removeAt(position: Int) {
        data.removeAt(position)
        notifyItemRemoved(position)
    }

    inner class ViewHolder(private val v: View) : RecyclerView.ViewHolder(v) {
        private val name: TextView = v.findViewById(R.id.name)
        private val species: TextView = v.findViewById(R.id.species)
        private val icon: ImageView = v.findViewById(R.id.icon)
        val const: LinearLayout = v.findViewById(R.id.expandedItem)
        val waterDate: TextView = v.findViewById(R.id.waterDate)
        val nextWaterDate: TextView = v.findViewById(R.id.nextWaterDate)
        val infoText: TextView = v.findViewById(R.id.infoText)


        fun bind(item: Plant) {
            name.text = item.name
            val dropdown = v.resources.getStringArray(R.array.options)
            species.text = dropdown[item.species]
            waterDate.text = item.waterDate
            nextWaterDate.text = item.nextWaterDate
            infoText.text = item.infoText

            //EDIT BUTTON
            val editBtn = v?.findViewById<ImageView>(R.id.editBtn)
            editBtn?.setOnClickListener {
                val activity=it.context as MainActivity
                (activity as MainActivity?)?.replaceFragment(EditFragment.newInstance(item, adapterPosition), "Edit Plant")
            }


            fun String.removeWhitespaces() = replace(" ", "")
            val uri = "drawable/"+dropdown[item.species].lowercase().removeWhitespaces()
            val res: Int = v.context.resources.getIdentifier(uri, null, v.context.packageName)
            icon.setImageResource(res)


            v.setOnClickListener {
                listener(item)
                notifyItemChanged(adapterPosition)
            }
        }

    }

}