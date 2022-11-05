package com.example.customapp

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.*

class EditFragment : Fragment() {
    private var formatDate = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
    lateinit var plant: Plant
    var position: Int = -1
    private var chosenDropdown: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val plantName = view.findViewById<EditText>(R.id.nameEdit)
        val plantPurchase = view.findViewById<EditText>(R.id.purchaseDateEdit)
        val plantWater = view.findViewById<EditText>(R.id.waterDateEdit)
        plantName.setText(plant.name)
        plantPurchase.setText(plant.purchaseDate)
        plantWater.setText(plant.waterDate)

        val dropdown = resources.getStringArray(R.array.options)
        val dropdownAuto = view.findViewById<AutoCompleteTextView>(R.id.dropdownAuto)
        dropdownAuto.setText(dropdown[plant.species])
        chosenDropdown = plant.species

        //SET DROPDOWN
        val arrayAdapter = ArrayAdapter(view.context, R.layout.dropdown_item, dropdown)
        dropdownAuto.setAdapter(arrayAdapter)
        dropdownAuto.setOnItemClickListener { _, _, position, _ ->
            chosenDropdown = position
        }

        //DATEPICKER FOR DATE INPUT FIELDS
        val purchaseDateLayout = view.findViewById<TextInputLayout>(R.id.purchaseDateLayout)
        val waterDateLayout = view.findViewById<TextInputLayout>(R.id.waterDateLayout)
        plantPurchase.setOnClickListener {
            setDate(plantPurchase)
        }
        plantWater.setOnClickListener {
            setDate(plantWater)
        }

        //SAVE BUTTON
        val nameLayout = view.findViewById<TextInputLayout>(R.id.nameLayout)
        val dropdownLayout = view.findViewById<TextInputLayout>(R.id.dropdownLayout)
        val saveBtn = view.findViewById<Button>(R.id.saveBtn)
        saveBtn.setOnClickListener {
            nameLayout.error = null
            dropdownLayout.error = null
            purchaseDateLayout.error = null
            if (plantName.text.toString() == "") {
                nameLayout.error = "Give your plant a name!"
            } else if (chosenDropdown == -1) {
                dropdownLayout.error = "Choose a species!"
            } else if (plantPurchase.text.toString() == "") {
                purchaseDateLayout.error = "Select a valid date!"
            } else if (plantWater.text.toString() == "") {
                waterDateLayout.error = "Select a valid date!"
            } else {
                val db = DatabaseHelper(view.context, null)
                val result = db.updatePlant(
                    position,
                    plantName.text.toString(),
                    chosenDropdown,
                    plantPurchase.text.toString(),
                    plantWater.text.toString()
                )
                if (result == -1) {
                    Toast.makeText(view.context, "Failed to update plant!", Toast.LENGTH_LONG)
                        .show()
                } else {
                    Toast.makeText(view.context, "Plant was updated!", Toast.LENGTH_LONG).show()
                    (activity as MainActivity?)?.replaceFragment(HomeFragment(), "Home")
                }
            }
        }
    }

    private fun setDate(edit: EditText) {
        val c = Calendar.getInstance()
        val dpd = DatePickerDialog(
            requireView().context, { _, year, month, day ->
                val selectDate = Calendar.getInstance()
                selectDate.set(Calendar.YEAR, year)
                selectDate.set(Calendar.MONTH, month)
                selectDate.set(Calendar.DAY_OF_MONTH, day)
                val date = formatDate.format(selectDate.time)
                edit.setText(date.toString())
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)
        )
        dpd.show()
    }


    companion object {
        @JvmStatic
        fun newInstance(editItem: Plant, adapterPosition: Int) =
            EditFragment().apply {
                plant = editItem
                position = adapterPosition
            }
    }
}