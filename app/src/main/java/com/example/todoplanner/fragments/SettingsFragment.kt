package com.example.todoplanner.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.todoplanner.R
import com.example.todoplanner.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater)
        sharedPreferences = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.boy.setOnClickListener {
            binding.layout.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.blue))
        }
        binding.girl.setOnClickListener {
            binding.layout.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.purple))
        }
        binding.continueButton.setOnClickListener{
            val sex = getText(binding.girl, binding.boy)
            val save = getText(binding.account, binding.phone)
            if(binding.editTextName.text.isNotEmpty() && sex.isNotEmpty() && save.isNotEmpty()){
                val spe = sharedPreferences.edit()
                spe.putString("name", binding.editTextName.text.toString())
                spe.putString("sex", sex)
                spe.putString("save", save)
                if(save == "Телефоне"){
                    findNavController().navigate(R.id.action_settingsFragment_to_listFragment)
                    spe.putBoolean("isCorrectSettings?", true)
                    spe.apply()
                }
                else{
                    findNavController().navigate(R.id.action_settingsFragment_to_regFragment)
                    spe.apply()
                }
            }
        }
    }

    private fun getText(first: RadioButton, second: RadioButton) : String{
        return if(first.isChecked){
            first.text.toString()
        }
        else if(second.isChecked){
            second.text.toString()
        }
        else{
            ""
        }
    }
}