package com.example.ruterumahsakit

import android.app.ProgressDialog
import android.content.Intent
import android.media.tv.TvContract.Programs
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.ruterumahsakit.databinding.ActivityLoginBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var registerDialog: ProgressDialog
    private lateinit var mUser: FirebaseUser


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        registerDialog = ProgressDialog(this)

        mAuth = FirebaseAuth.getInstance()

        binding.tvToRegister.setOnClickListener { startActivity(Intent(this, RegisterActivity::class.java)) }

        binding.btnLogin.setOnClickListener {
            registerDialog.setMessage("Login ke akun anda")
            registerDialog.setTitle("Mohon tunggu")
            registerDialog.show()

            val email = binding.tiEmail.text.toString()
            val password = binding.tiPassword.text.toString()

            if(email.isEmpty()) {
                Snackbar.make(binding.root, "Email Harus Diisi!", Snackbar.LENGTH_SHORT).show()
                binding.tiEmail.error = "Email tidak boleh kosong!"
                return@setOnClickListener
            }

            if(password.isEmpty()) {
                Snackbar.make(binding.root, "Password Harus Diisi!", Snackbar.LENGTH_SHORT).show()
                binding.tiPassword.error = "Password tidak boleh kosong!"
                return@setOnClickListener
            }

            if(password.length < 8) {
                Snackbar.make(binding.root, "Password Minimal 8 Karakter!", Snackbar.LENGTH_SHORT).show()
                binding.tiPassword.error = "Isi Password anda Minimal 8 Karakter!"
                return@setOnClickListener
            }

            mAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                Toast.makeText(this, "Berhasil login dengan email" + email, Toast.LENGTH_SHORT).show()
                registerDialog.dismiss()

                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }.addOnFailureListener{
                Toast.makeText(this, "Username / Password Salah!", Toast.LENGTH_SHORT).show()
                registerDialog.dismiss()
            }

        }
    }

    override fun onStart() {
        super.onStart()

        val currentUser = mAuth.currentUser
        if(currentUser != null) {
            Toast.makeText(this, "User Tidak Kosong!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "user Kosong!", Toast.LENGTH_SHORT).show()
        }


    }
}