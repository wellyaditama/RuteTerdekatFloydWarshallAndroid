package com.example.ruterumahsakit

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ruterumahsakit.databinding.ActivityRegisterBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private lateinit var registerDialog: ProgressDialog
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mUser: FirebaseUser
    private lateinit var db: FirebaseFirestore
    private lateinit var imageUri: Uri
    private lateinit var downloadUrl: String
    private lateinit var storageReference: StorageReference
    private lateinit var tipeAkun: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        imageUri = Uri.EMPTY
        tipeAkun = "kosong"

        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        registerDialog = ProgressDialog(this)

        mAuth = Firebase.auth
        
        db = FirebaseFirestore.getInstance()

        binding.tvToLogin.setOnClickListener { startActivity(Intent(this, LoginActivity::class.java)) }

        binding.btnChooseProfilePicture.setOnClickListener { chooseImage() }

        binding.btnRegister.setOnClickListener {
            val email : String = binding.tiEmail.text.toString();
            val nama : String = binding.tiNama.text.toString();
            val password : String = binding.tiPassword.text.toString();
            val confirmPassword : String = binding.tiConfirmPassword.text.toString();

            var isFormValid = true;


            if(imageUri == Uri.EMPTY) {
                isFormValid = false
                Toast.makeText(this, "Mohon Pilih Foto Profil Anda!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(nama.isEmpty()) {
                isFormValid = false
                binding.tiNama.error = "Nama Harus Diisi!"
                return@setOnClickListener
            }

            if(email.isEmpty()) {
                isFormValid = false
                binding.tiEmail.error = "Email Harus Diisi!"
                return@setOnClickListener
            }

            // TODO: Create Email Pattern

            if(tipeAkun == "kosong") {
                isFormValid = false
                Toast.makeText(this, "Pilih Tipe Akun!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(password.isEmpty()) {
                isFormValid = false
                binding.tiPassword.error = "Password Harus Diisi!"
                return@setOnClickListener
            }

            if(password.length < 8) {
                isFormValid = false
                binding.tiPassword.error = "Password minimal 8 karakter!"
                return@setOnClickListener
            }

            if(confirmPassword.isEmpty()) {
                isFormValid = false
                binding.tiConfirmPassword.error = "Konfirmasi Password Harus Diisi!"
                return@setOnClickListener
            }

            if(confirmPassword.length < 8) {
                isFormValid = false
                binding.tiConfirmPassword.error = "Konfirmasi Password minimal 8 karakter!"
                return@setOnClickListener
            }

            if(confirmPassword != password) {
                isFormValid = false
                binding.tiConfirmPassword.error = "Konfirmasi Password tidak sama dengan Password!"
                return@setOnClickListener
            }

            if(isFormValid) {
                registerDialog.setMessage("Membuat Akun Anda...")
                registerDialog.setTitle("Mohon Bersabar...")
                registerDialog.setCanceledOnTouchOutside(false)
                registerDialog.show()

                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                val current = LocalDateTime.now().format(formatter)

                storageReference = FirebaseStorage.getInstance().getReference("profilePicture/"+email+"/"+current)

                storageReference.putFile(imageUri).addOnSuccessListener {
                    if(it.metadata != null) {
                        if(it.metadata!!.reference != null) {
                            it.metadata!!.reference!!.downloadUrl.addOnCompleteListener {
                                Log.d("CLOUD FIRESTORE", "onComplete: " + it.result.toString())

                                val downloadUrlImage: String = it.result.toString()
                                downloadUrl = downloadUrlImage

                                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                                    if(it.isSuccessful) {
                                        createuserRegisterFirestore(email, nama, tipeAkun, downloadUrl)
                                        Log.d("FIRESTORE", "onComplete: successfully upload image")
                                    } else {
                                        registerDialog.dismiss()
                                        Snackbar.make(binding.clRegister, "Akun anda Gagal Dibuat!", Snackbar.LENGTH_SHORT).show()
                                    }
                                }.addOnFailureListener{
                                    registerDialog.dismiss()
                                    Log.e("FIREBASEAUTH", "onFailure: Gagal mendaftarkan akun dengan email " + email);
                                    Snackbar.make(binding.clRegister, "Akun anda Gagal Dibuat!", Snackbar.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }



            }

        }






    }

    private fun createuserRegisterFirestore(email: String, nama: String, tipeAkun: String, downloadUrl: String) {

        val user = HashMap<String, String>()

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val current = LocalDateTime.now().format(formatter)

        user.put("email", email)
        user.put("nama", nama)
        user.put("userType", tipeAkun)
        user.put("createdAt", current)
        user.put("downloadUrl", downloadUrl)

        db.collection("users").document().set(user).addOnSuccessListener {
            registerDialog.dismiss()
            Log.d("FIRESTORE", "DocumentSnapshot added with document "+ email)
            Snackbar.make(binding.clRegister, "Akun anda berhasil dibuat", Snackbar.LENGTH_SHORT).show()

            FirebaseAuth.getInstance().currentUser!!.sendEmailVerification().addOnSuccessListener {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }.addOnFailureListener{
            registerDialog.dismiss()
            Log.e("FIRESTORE", "onFailure: gagal upload data register ke cloud firestore")
        }

    }

    fun chooseImage() {
        val intent = Intent()
        intent.setType("image/*")
        intent.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 100 && data != null && data.data != null) {
            imageUri = data.data!!
            Picasso.get().load(imageUri).into(binding.ivRegisterProfilePicture)
        }
    }

    override fun onStart() {
        super.onStart()
        

    }

    fun onRadioTypeClicked(v: View) {
        val selectedId: Int = binding.rgJenisAkun.checkedRadioButtonId

        val rbTipeAkun : RadioButton = findViewById(selectedId)

        if(selectedId == -1) {
            Toast.makeText(this, "Nothing Selected", Toast.LENGTH_SHORT).show()
        } else {
            tipeAkun = rbTipeAkun.text.toString()
            Toast.makeText(this, tipeAkun, Toast.LENGTH_SHORT).show()
        }
    }



}