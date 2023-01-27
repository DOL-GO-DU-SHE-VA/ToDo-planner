package com.example.todoplanner.fragments

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.app.AlertDialog
import android.content.Context
import com.example.todoplanner.R
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.ktx.auth
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import androidx.navigation.fragment.findNavController
import androidx.room.Room
import com.example.todoplanner.adapters.FirebaseDataClass
import com.example.todoplanner.adapters.FirebaseRecycleAdapter
import com.example.todoplanner.adapters.SQLRecycleAdapter
import com.example.todoplanner.databinding.FragmentListBinding
import com.example.todoplanner.sql.AppDatabase
import com.google.firebase.database.*

class ListFragment : Fragment() {

    private lateinit var auth : FirebaseAuth
    private lateinit var binding : FragmentListBinding
    private lateinit var sharedPreferences : SharedPreferences
    private lateinit var database: DatabaseReference
    private lateinit var fAdapter: FirebaseRecycleAdapter
    private lateinit var sqlAdapter : SQLRecycleAdapter
    private var fArrayList: ArrayList<FirebaseDataClass> = ArrayList()
    private var fArrayKeys = ArrayList<String>()
    override fun onCreateView(
        inflater : LayoutInflater, container : ViewGroup?,
        savedInstanceState : Bundle?
    ) : View {
        binding = FragmentListBinding.inflate(inflater)
        auth = Firebase.auth
        sqlAdapter = SQLRecycleAdapter()
        database = FirebaseDatabase.getInstance().getReference("Tasks/${auth.uid}")
        sharedPreferences = requireContext().getSharedPreferences("settings",
            Context.MODE_PRIVATE)
        return binding.root
    }

    override fun onViewCreated(view : View, savedInstanceState : Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(firstLaunch()){
            AlertDialog.Builder(activity)
                .setCancelable(false)
                .setTitle("Внимание")
                .setMessage("Похоже это ваш первый запуск, настройте приложение")
                .setPositiveButton("Хорошо") { _, _ ->
                    findNavController().navigate(R.id.action_listFragment_to_settingsFragment)
                }.create()
            .show()
        }
        else{
            val sex = sharedPreferences.getString("sex", "nothing")
            if(sex == "Мальчик"){
                binding.layout.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.blue))
            }
            else if (sex == "Девочка"){
                binding.layout.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.purple))
            }
        }
        val save = sharedPreferences.getString("save", "Nothing")
        if(save == "Телефоне"){
            loadFromSQL()
        }
        else if (save == "Аккаунте"){
            loadFromFirebase()
        }
        binding.floatingActionButton.setOnClickListener {
            findNavController().navigate(R.id.action_listFragment_to_addFragment)
        }
    }

    private fun loadFromFirebase(){
        database.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.childrenCount.toString() == "0") {
                    binding.recycleView.visibility = View.INVISIBLE
                    binding.textNull.visibility = View.VISIBLE
                } else {
                    binding.textNull.visibility = View.INVISIBLE
                    binding.recycleView.visibility = View.VISIBLE
                }
                try{
                    if (snapshot.exists()) {
                        fArrayList.clear()
                        for (snap in snapshot.children) {
                            fArrayList.add(snap.getValue(FirebaseDataClass::class.java)!!)
                            fArrayKeys.add(snap.key.toString())
                        }
                    }
                    fAdapter = FirebaseRecycleAdapter(fArrayList)
                    fAdapter.setOnRecycleViewClick(object :
                        FirebaseRecycleAdapter.OnRecycleViewListener {
                        override fun onRecycleViewClick(position: Int) {
                            AlertDialog.Builder(requireContext())
                                .setTitle("Внимание")
                                .setMessage("Что вы хотите сделать?")
                                .setPositiveButton("Удалить"){ _,_ ->
                                    database.child(fArrayKeys[position]).removeValue()
                                }
                                .setNeutralButton("Приоритет"){ _,_ ->
                                    AlertDialog.Builder(requireContext())
                                        .setTitle("Приоритет")
                                        .setNegativeButton("Высокий"){ _,_ ->
                                            val value = FirebaseDataClass(
                                                fArrayList[position].dateOfCreation,
                                                fArrayList[position].dateOfUpdater,
                                                fArrayList[position].description,
                                                fArrayList[position].expirationDate,
                                                fArrayList[position].header,
                                                "Высокий"
                                            )
                                            fArrayList[position].priority
                                            database.child(fArrayKeys[position]).setValue(value)
                                        }
                                        .setPositiveButton("Средний"){ _,_ ->
                                            val value = FirebaseDataClass(
                                                fArrayList[position].dateOfCreation,
                                                fArrayList[position].dateOfUpdater,
                                                fArrayList[position].description,
                                                fArrayList[position].expirationDate,
                                                fArrayList[position].header,
                                                "Средний"
                                            )
                                            fArrayList[position].priority
                                            database.child(fArrayKeys[position]).setValue(value)
                                        }
                                        .setNeutralButton("Низкий"){ _,_ ->
                                            val value = FirebaseDataClass(
                                                fArrayList[position].dateOfCreation,
                                                fArrayList[position].dateOfUpdater,
                                                fArrayList[position].description,
                                                fArrayList[position].expirationDate,
                                                fArrayList[position].header,
                                                "Низкий"
                                            )
                                            fArrayList[position].priority
                                            database.child(fArrayKeys[position]).setValue(value)
                                        }.create().show()
                                }
                                .show()
                        }
                    })
                    binding.recycleView.adapter = fAdapter
                }

                catch (ex: Exception){
                    Log.e("Error", ex.message.toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun loadFromSQL(){
        val db = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java,
            "task_table"
        ).allowMainThreadQueries().build()
        db.userDao().readAllData().observe(viewLifecycleOwner) {
            if(it != null){
                binding.textNull.visibility = View.INVISIBLE
                binding.recycleView.visibility = View.VISIBLE
            }
            else {
                binding.recycleView.visibility = View.INVISIBLE
                binding.textNull.visibility = View.VISIBLE
            }
            sqlAdapter.setData(it)
            sqlAdapter.setOnRecycleViewClick(object : SQLRecycleAdapter.OnRecycleViewListener {
                override fun onRecycleViewClick(position: Int) {
                    AlertDialog.Builder(requireContext())
                        .setTitle("Внимание")
                        .setMessage("Что вы хотите сделать?")
                        .setPositiveButton("Удалить"){ _,_ ->
                            db.userDao().delete(it[position])
                        }
                        .setNeutralButton("Приоритет"){ _,_ ->
                            AlertDialog.Builder(requireContext())
                                .setTitle("Приоритет")
                                .setNegativeButton("Высокий"){ _,_ ->
                                    db.userDao().update( "Высокий", it[position].id)
                                }
                                .setPositiveButton("Средний"){ _,_ ->
                                    db.userDao().update("Средний", it[position].id)
                                }
                                .setNeutralButton("Низкий"){ _,_ ->
                                    db.userDao().update("Низкий", it[position].id)
                                }.create().show()
                        }
                        .show()
                }
            })
        }
        binding.recycleView.adapter = sqlAdapter
    }

    private fun firstLaunch() : Boolean{
        if(auth.currentUser != null){
            return false
        }
        if(sharedPreferences.getBoolean("isCorrectSettings?", false)){
            return false
        }
        return true
    }
}