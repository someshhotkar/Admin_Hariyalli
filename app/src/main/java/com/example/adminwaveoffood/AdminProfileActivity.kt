package com.example.adminwaveoffood

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.adminwaveoffood.databinding.ActivityAdminProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class AdminProfileActivity : AppCompatActivity() {

    private val binding: ActivityAdminProfileBinding by lazy {
        ActivityAdminProfileBinding.inflate(layoutInflater)
    }

    private var isEnable = false

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var adminReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Initialize Firebase instances
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        adminReference = database.reference.child("user")

        // Back button listener
        binding.backButton.setOnClickListener {
            finish()
        }

        // Save button listener
        binding.SaveInfoButton.setOnClickListener {
            updateUserData()
        }

        // Disable the input fields and save button initially
        binding.name.isEnabled = false
        binding.address.isEnabled = false
        binding.email.isEnabled = false
        binding.phone.isEnabled = false
        binding.password.isEnabled = false
        binding.SaveInfoButton.isEnabled = false

        // Set up click listener for the edit button
        binding.editButton.setOnClickListener {
            isEnable = !isEnable
            // Toggle edit mode
            binding.name.isEnabled = isEnable
            binding.address.isEnabled = isEnable
            binding.email.isEnabled = isEnable
            binding.phone.isEnabled = isEnable
            binding.password.isEnabled = isEnable

            // Change the button text to reflect the current mode
            binding.editButton.text = if (isEnable) "Save" else "Edit"
            binding.SaveInfoButton.isEnabled = isEnable
        }

        // Call function to retrieve user data from Firebase
        retrieveUserData()
    }

    private fun updateUserData() {
        val updateName: String = binding.name.text.toString()
        val updateEmail: String = binding.email.text.toString()
        val updatePassword: String = binding.password.text.toString()
        val updatePhone: String = binding.phone.text.toString()
        val updateAddress: String = binding.address.text.toString()

        val currentUserUid = auth.currentUser?.uid
        if (currentUserUid != null) {
            val userData = mapOf(
                "name" to updateName,
                "email" to updateEmail,
                "password" to updatePassword,
                "phone" to updatePhone,
                "address" to updateAddress
            )

            // Update user data in Firebase Realtime Database
            adminReference.child(currentUserUid).updateChildren(userData).addOnSuccessListener {
                Toast.makeText(this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show()

                // Update email and password in Firebase Authentication
                auth.currentUser?.updateEmail(updateEmail)?.addOnCompleteListener { emailTask ->
                    if (emailTask.isSuccessful) {
                        auth.currentUser?.updatePassword(updatePassword)?.addOnCompleteListener { passwordTask ->
                            if (passwordTask.isSuccessful) {
                                Toast.makeText(this, "Email and Password Updated Successfully", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this, "Password Update Failed", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(this, "Email Update Failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Profile Update Failed", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
        }
    }

    // Function to retrieve user data from Firebase
    private fun retrieveUserData() {
        val currentUserUid: String? = auth.currentUser?.uid
        if (currentUserUid != null) {
            val userReference: DatabaseReference = adminReference.child(currentUserUid)
            userReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // Fetch data from the snapshot
                        val ownerName = snapshot.child("name").getValue(String::class.java)
                        val address = snapshot.child("address").getValue(String::class.java)
                        val email = snapshot.child("email").getValue(String::class.java)
                        val phone = snapshot.child("phone").getValue(String::class.java)
                        val password = snapshot.child("password").getValue(String::class.java)

                        Log.d("TAG", "onDataChange: $ownerName, $email, $phone")

                        // Set data to text views
                        setDataToTextView(ownerName, email, password, address, phone)
                    } else {
                        Log.d("TAG", "No data found for UID: $currentUserUid")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("TAG", "onCancelled: ${error.message}")
                }
            })
        }
    }

    // Function to set retrieved data to TextViews
    private fun setDataToTextView(
        ownerName: String?,
        email: String?,
        password: String?,
        address: String?,
        phone: String?
    ) {
        binding.name.setText(ownerName)
        binding.email.setText(email)
        binding.password.setText(password)
        binding.phone.setText(phone)
        binding.address.setText(address)
    }
}
