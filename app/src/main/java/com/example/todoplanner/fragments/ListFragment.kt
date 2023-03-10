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
                .setTitle("????????????????")
                .setMessage("???????????? ?????? ?????? ???????????? ????????????, ?????????????????? ????????????????????")
                .setPositiveButton("????????????") { _, _ ->
                    findNavController().navigate(R.id.action_listFragment_to_settingsFragment)
                }.create()
            .show()
        }
        else{
            val sex = sharedPreferences.getString("sex", "nothing")
            if(sex == "??????????????"){
                binding.layout.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.blue))
            }
            else if (sex == "??????????????"){
                binding.layout.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.purple))
            }
        }
        val save = sharedPreferences.getString("save", "Nothing")
        if(save == "????????????????"){
            loadFromSQL()
        }
        else if (save == "????????????????"){
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
                                .setTitle("????????????????")
                                .setMessage("?????? ???? ???????????? ???????????????")
                                .setPositiveButton("??????????????"){ _,_ ->
                                    database.child(fArrayKeys[position]).removeValue()
                                }
                                .setNeutralButton("??????????????????"){ _,_ ->
                                    AlertDialog.Builder(requireContext())
                                        .setTitle("??????????????????")
                                        .setNegativeButton("??????????????"){ _,_ ->
                                            val value = FirebaseDataClass(
                                                fArrayList[position].dateOfCreation,
                                                fArrayList[position].dateOfUpdater,
                                                fArrayList[position].description,
                                                fArrayList[position].expirationDate,
                                                fArrayList[position].header,
                                                "??????????????"
                                            )
                                            fArrayList[position].priority
                                            database.child(fArrayKeys[position]).setValue(value)
                                        }
                                        .setPositiveButton("??????????????"){ _,_ ->
                                            val value = FirebaseDataClass(
                                                fArrayList[position].dateOfCreation,
                                                fArrayList[position].dateOfUpdater,
                                                fArrayList[position].description,
                                                fArrayList[position].expirationDate,
                                                fArrayList[position].header,
                                                "??????????????"
                                            )
                                            fArrayList[position].priority
                                            database.child(fArrayKeys[position]).setValue(value)
                                        }
                                        .setNeutralButton("????????????"){ _,_ ->
                                            val value = FirebaseDataClass(
                                                fArrayList[position].dateOfCreation,
                                                fArrayList[position].dateOfUpdater,
                                                fArrayList[position].description,
                                                fArrayList[position].expirationDate,
                                                fArrayList[position].header,
                                                "????????????"
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
                        .setTitle("????????????????")
                        .setMessage("?????? ???? ???????????? ???????????????")
                        .setPositiveButton("??????????????"){ _,_ ->
                            db.userDao().delete(it[position])
                        }
                        .setNeutralButton("??????????????????"){ _,_ ->
                            AlertDialog.Builder(requireContext())
                                .setTitle("??????????????????")
                                .setNegativeButton("??????????????"){ _,_ ->
                                    db.userDao().update( "??????????????", it[position].id)
                                }
                                .setPositiveButton("??????????????"){ _,_ ->
                                    db.userDao().update("??????????????", it[position].id)
                                }
                                .setNeutralButton("????????????"){ _,_ ->
                                    db.userDao().update("????????????", it[position].id)
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