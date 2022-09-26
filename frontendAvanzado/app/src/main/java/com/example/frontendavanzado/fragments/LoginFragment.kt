package com.example.frontendavanzado.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.findNavController
import com.example.frontendavanzado.R
import com.example.frontendavanzado.datasource.localStorage.DataStorage
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginFragment : Fragment(R.layout.fragment_login) {
    private lateinit var inputEmail: TextInputLayout
    private lateinit var inputPassword: TextInputLayout
    private lateinit var buttonLogin: MaterialButton
    private lateinit var dataStore: DataStorage

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        inputEmail = view.findViewById(R.id.textField_loginFragment_email)
        inputPassword = view.findViewById(R.id.textField_loginFragment_password)
        buttonLogin = view.findViewById(R.id.button_loginFragment_login)

        dataStore = DataStorage(requireContext())

        setListeners()
    }

    private fun setListeners() {
        buttonLogin.setOnClickListener{
            val email = inputEmail.editText!!.text.toString()
            val password = inputPassword.editText!!.text.toString()
            val defaultEmail = getString(R.string.defaultEmail)

            if (email == password && email == defaultEmail){
                CoroutineScope(Dispatchers.IO).launch{
                    dataStore.saveKeyValue("email", email)
                }
                requireView().findNavController().navigate(R.id.action_loginFragment_to_charactersFragment)
            }
            else
                Toast.makeText(requireContext(), getString(R.string.errorLogin), Toast.LENGTH_LONG).show()
        }
    }
}