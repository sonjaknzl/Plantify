package com.example.customapp

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.*


class AddFragment : Fragment() {
    private var formatDate = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
    private var chosenDropdown: Int = -1
    private lateinit var purchaseDateEdit: EditText
    private lateinit var waterDateEdit: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dropdown = resources.getStringArray(R.array.options)

        val dropdownAuto = view.findViewById<AutoCompleteTextView>(R.id.dropdownAuto)
        val arrayAdapter = ArrayAdapter(view.context, R.layout.dropdown_item,dropdown)

        dropdownAuto.setAdapter(arrayAdapter)
        dropdownAuto.setOnItemClickListener { _, _, position, _ ->
            chosenDropdown = position
        }

        //IMPLEMENT DATEPICKER FOR DATE INPUT FIELDS
        purchaseDateEdit = view.findViewById(R.id.purchaseDateEdit)
        waterDateEdit = view.findViewById(R.id.waterDateEdit)
        val purchaseDateLayout = view.findViewById<TextInputLayout>(R.id.purchaseDateLayout)
        val waterDateLayout = view.findViewById<TextInputLayout>(R.id.waterDateLayout)
        purchaseDateEdit.setOnClickListener{
            setDate(purchaseDateEdit)
        }
        waterDateEdit.setOnClickListener{
            setDate(waterDateEdit)
        }

        // IMPLEMENT SAVE-CLICK
        val nameLayout = view.findViewById<TextInputLayout>(R.id.nameLayout)
        val nameEdit = view.findViewById<EditText>(R.id.nameEdit)
        val dropdownLayout = view.findViewById<TextInputLayout>(R.id.dropdownLayout)
        val saveBtn = view.findViewById<Button>(R.id.saveBtn)
        saveBtn.setOnClickListener{
            nameLayout.error = null
            dropdownLayout.error = null
            purchaseDateLayout.error = null
            if(nameEdit.text.toString() == "") {
                nameLayout.error = "Give your plant a name!"
            } else if(chosenDropdown == -1) {
                dropdownLayout.error = "Choose a species!"
                Toast.makeText(view.context, "Failed to add plant!", Toast.LENGTH_LONG).show()
                Log.i("INFO", purchaseDateEdit.text.toString())
            }else if(purchaseDateEdit.text.toString() == ""){
                purchaseDateLayout.error = "Select a valid date!"
            }else if(waterDateEdit.text.toString() == ""){
                waterDateLayout.error = "Select a valid date!"
            }else {
                val db = DatabaseHelper(view.context, null)
                val result = db.addPlant(nameEdit.text.toString(),chosenDropdown,purchaseDateEdit.text.toString(), waterDateEdit.text.toString())
                if(result == -1){
                    Toast.makeText(view.context, "Failed to add plant!", Toast.LENGTH_LONG).show()
                }else {
                    Toast.makeText(view.context, "Plant was added!", Toast.LENGTH_LONG).show()
                    (activity as MainActivity?)?.replaceFragment(HomeFragment(), "Home")
                }
            }
        }
    }

    private fun setDate(edit: EditText) {
        val c = Calendar.getInstance()
        val dpd = DatePickerDialog(
            requireView().context, DatePickerDialog.OnDateSetListener{ _, year, month, day ->
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

}