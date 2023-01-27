package com.example.todoplanner.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.todoplanner.R
import com.example.todoplanner.databinding.FragmentAuthBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AuthFragment : Fragment() {

    private lateinit var binding: FragmentAuthBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var sharedPreferences : SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAuthBinding.inflate(inflater)
        auth = Firebase.auth
        sharedPreferences = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sex = sharedPreferences.getString("sex", "nothing")
        if(sex == "Мальчик"){
            binding.layout.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.blue))
        }
        else if (sex == "Девочка"){
            binding.layout.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.purple))
        }
        binding.authButton.setOnClickListener{
            val login = binding.login.text.toString()
            val password = binding.password.text.toString()
            if(login.isNotEmpty() && password.isNotEmpty()){
                auth.signInWithEmailAndPassword(login, password).addOnCompleteListener {
                    if(it.isSuccessful){
                        try{
                            findNavController().navigate(R.id.action_authFragment_to_listFragment)
                        }
                        catch (_: IllegalStateException){}
                    }
                    else{
                        when(it.exception.toString()){
                            "com.google.firebase.auth.FirebaseAuthInvalidUserException: There is no user record corresponding to this identifier. The user may have been deleted." ->
                                Toast.makeText(activity,"Аккаунт не найден", Toast.LENGTH_SHORT).show()

                            "com.google.firebase.auth.FirebaseAuthInvalidCredentialsException: The email address is badly formatted." ->
                                Toast.makeText(activity,"Неверный логин или пароль", Toast.LENGTH_SHORT).show()

                            "com.google.firebase.auth.FirebaseAuthInvalidCredentialsException: The password is invalid or the user does not have a password." ->
                                Toast.makeText(activity,"Неверный логин или пароль", Toast.LENGTH_SHORT).show()

                            else -> Toast.makeText(activity,"Проверьте подключение к интернету и повторите попытку позже", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            else{
                Toast.makeText(activity,"Вы оставили какое-то поле пустым", Toast.LENGTH_SHORT).show()
            }
        }
    }
}