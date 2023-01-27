package com.example.todoplanner.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.room.Room
import com.example.todoplanner.R
import com.example.todoplanner.adapters.FirebaseDataClass
import com.example.todoplanner.databinding.FragmentAddBinding
import com.example.todoplanner.sql.AppDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import com.example.todoplanner.sql.Task as Task

class AddFragment : Fragment() {

    private lateinit var binding: FragmentAddBinding
    private lateinit var database : DatabaseReference
    private lateinit var arrayAdapter: ArrayAdapter<String>
    private lateinit var auth : FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences
    private var priority : Array<String> = arrayOf("Высокий","Средний","Низкий")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddBinding.inflate(inflater)
        auth = Firebase.auth
        sharedPreferences = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
        return binding.root
    }

    @SuppressLint("SimpleDateFormat")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sex = sharedPreferences.getString("sex", "nothing")
        if(sex == "Мальчик"){
            binding.layout.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.blue))
        }
        else if (sex == "Девочка"){
            binding.layout.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.purple))
        }

        arrayAdapter = ArrayAdapter(requireContext(),R.layout.spinner_item, priority)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.priority.adapter = arrayAdapter

        binding.addButton.setOnClickListener {
            val dateTodayString = SimpleDateFormat("dd.M.yyyy", Locale.getDefault()).format(Date())
            val expirationDateString = "${binding.expirationDate.dayOfMonth}.${binding.expirationDate.month + 1}.${binding.expirationDate.year}"
            val expirationDate = SimpleDateFormat("dd.M.yyyy").parse(expirationDateString)
            val dateToday = SimpleDateFormat("dd.M.yyyy").parse(dateTodayString)
            if(dateToday!! < expirationDate){
                if(binding.header.text.toString().isNotEmpty() && binding.description.text.toString().isNotEmpty()){
                    val save = sharedPreferences.getString("save", "Nothing")
                    if(save == "Телефоне"){
                        val taskDataClass = Task(0,
                            dateOfCreation = dateTodayString,
                            dateOfUpdater = dateTodayString,
                            description = binding.description.text.toString(),
                            expirationDate = expirationDateString,
                            header = binding.header.text.toString(),
                            priority = binding.priority.selectedItem.toString())
                        val db = Room.databaseBuilder(
                            requireContext(),
                            AppDatabase::class.java,
                            "task_table"
                        ).allowMainThreadQueries().build()
                        db.userDao().addUser(taskDataClass)
                        findNavController().navigate(R.id.action_addFragment_to_listFragment)
                    }
                    else if (save == "Аккаунте"){
                        val taskDataClass = FirebaseDataClass(
                            dateOfCreation = dateTodayString,
                            dateOfUpdater = dateTodayString,
                            description = binding.description.text.toString(),
                            expirationDate = expirationDateString,
                            header = binding.header.text.toString(),
                            priority = binding.priority.selectedItem.toString())
                        database = FirebaseDatabase.getInstance().getReference("Tasks/${auth.uid}")
                        database.child(getKey().push().key.toString()).setValue(taskDataClass).addOnCompleteListener {
                            if(it.isSuccessful){
                                findNavController().navigate(R.id.action_addFragment_to_listFragment)
                            }
                        }
                    }
                }
                else{
                    Toast.makeText(activity,"Какое-то поле осталось пустым", Toast.LENGTH_SHORT).show()
                }
            }
            else{
                Toast.makeText(activity,"Дата выбрана неправильно", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getKey(): DatabaseReference {
        return FirebaseDatabase
            .getInstance("https://checkit-63120-default-rtdb.firebaseio.com/")
            .reference
    }
}