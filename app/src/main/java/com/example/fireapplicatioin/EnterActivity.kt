package com.example.fireapplicatioin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.fireapplicatioin.databinding.ActivityEnterBinding
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


fun getPrefsName(): String{
    return "MyPrefsName"
}

class EnterActivity : AppCompatActivity() {
    private lateinit var mDataBase: DatabaseReference
    lateinit var binding: ActivityEnterBinding

    private val userKey = "User"

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter)

        mDataBase = FirebaseDatabase.getInstance().getReference(userKey)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_enter)

        auth = FirebaseAuth.getInstance()

        binding.btnSignUp.setOnClickListener {
            onClickSave()
        }

    }

    private fun onClickSave(){
        val id = mDataBase.key
        val name = binding.edtUsername.text.toString()
        val password = binding.edtPassword.text.toString()
        val email = binding.edtEmail.text.toString()


        if(email.isEmpty()){
            binding.edtEmail.error = "Email is required"
            binding.edtEmail.requestFocus()
            return
        }

        if(name.isEmpty()){
            binding.edtUsername.error = "Name is required"
            binding.edtUsername.requestFocus()
            return
        }

        if(password.isEmpty()){
            binding.edtPassword.error = "Password is required"
            binding.edtPassword.requestFocus()
            return
        }

        if(password.length < 6){
            binding.edtPassword.error = "Password is too short"
            binding.edtPassword.requestFocus()
            return
        }
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this){task ->
                if (task.isSuccessful){
                    val user = id?.let { User(it, name, password, email) }
                    Firebase.database.getReference("Users")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                        .setValue(user).addOnCompleteListener(this){
                            auth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener(this){task ->
                                    if(task.isSuccessful){
                                        val sharedPreferences = getSharedPreferences(getPrefsName(), 0)
                                        val editor = sharedPreferences.edit()
                                        editor.putBoolean("hasLoggedIn", true)
                                        editor.apply()
                                        val intent = Intent(this, MainActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                }
                        }


                }
            }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this){task ->
                if(task.isSuccessful){
                    val sharedPreferences = getSharedPreferences(getPrefsName(), 0)
                    val editor = sharedPreferences.edit()
                    editor.putBoolean("hasLoggedIn", true)
                    editor.apply()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
    }
}


class User(var id: String = "", var name: String = "", var password: String= "", var email: String = "")
