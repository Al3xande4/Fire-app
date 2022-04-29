package com.example.fireapplicatioin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.example.fireapplicatioin.databinding.ActivityEnterBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

fun getPrefsName(): String{
    return "MyPrefsName"
}

class EnterActivity : AppCompatActivity() {
    lateinit var mDataBase: DatabaseReference
    lateinit var binding: ActivityEnterBinding

    val userKey = "User"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter)

        mDataBase = FirebaseDatabase.getInstance().getReference(userKey)
        binding = DataBindingUtil.setContentView<ActivityEnterBinding>(this, R.layout.activity_enter)

        binding.signButton.setOnClickListener {
            onClickSave(binding.signButton)
            val sharedPreferences = getSharedPreferences(getPrefsName(), 0)
            val editor = sharedPreferences.edit()
            editor.putBoolean("hasLoggedIn", true)
            editor.commit()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
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