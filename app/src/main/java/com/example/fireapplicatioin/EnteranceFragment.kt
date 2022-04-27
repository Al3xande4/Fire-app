package com.example.fireapplicatioin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.fireapplicatioin.databinding.FragmentEnteranceBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class EnteranceFragment : Fragment() {
    lateinit var mDataBase: DatabaseReference
    lateinit var binding: FragmentEnteranceBinding

    val userKey = "User"
    lateinit var drawerLayout: DrawerLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate<FragmentEnteranceBinding>(inflater, R.layout.fragment_enterance, container, false)
        mDataBase = FirebaseDatabase.getInstance().getReference(userKey)

        binding.signButton.setOnClickListener {
            onClickSave(binding.signButton)
            findNavController().navigate(R.id.action_registrationFragment_to_mapFragment)
        }
        return binding.root
    }

    fun onClickSave(view: View){
        var id = mDataBase.key
        var name = binding.nameEditText.text.toString()
        val password = binding.passwordEditText.text.toString()
        var user = id?.let { User(it, name, password) }
        mDataBase.push().setValue(user)
    }
}

class User(var id: String, var name: String, var password: String)