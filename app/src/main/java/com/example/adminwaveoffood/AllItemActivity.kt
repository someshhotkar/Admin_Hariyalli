package com.example.adminwaveoffood

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adminwaveoffood.adapter.MenuItemAdapter
import com.example.adminwaveoffood.databinding.ActivityAllItemBinding
import com.example.adminwaveoffood.model.AllMenu
import com.google.firebase.database.*

class AllItemActivity : AppCompatActivity() {

    private lateinit var databaseReference: DatabaseReference
    private lateinit var database: FirebaseDatabase
    private var menuItems: ArrayList<AllMenu> = ArrayList()

    private val binding: ActivityAllItemBinding by lazy {
        ActivityAllItemBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().reference

        // Retrieve menu items from the database
        retrieveMenuItems()

        // Back button to close activity
        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun retrieveMenuItems() {
        // Get Firebase database instance
        database = FirebaseDatabase.getInstance()

        // Reference to the 'menu' node
        val foodRef: DatabaseReference = database.reference.child("menu")

        // Fetch data from Firebase database
        foodRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Clear existing data before populating
                menuItems.clear()

                // Loop through each food item in the snapshot
                for (foodSnapshot in snapshot.children) {
                    val menuItem: AllMenu? = foodSnapshot.getValue(AllMenu::class.java)
                    menuItem?.let {
                        it.key = foodSnapshot.key // Set the Firebase key
                        menuItems.add(it)
                    }
                }

                // Set the adapter once data is retrieved
                setAdapter()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
                error.toException().printStackTrace()
            }
        })
    }

    private fun setAdapter() {
        // Corrected adapter initialization and RecyclerView setup
        val adapter = MenuItemAdapter(this@AllItemActivity, menuItems, databaseReference) { position ->
            deleteMenuItem(position)
        }

        binding.MenuRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.MenuRecyclerView.adapter = adapter
    }
    private fun deleteMenuItem(position: Int) {
        // Get the menu item to delete
        val menuItemToDelete: AllMenu = menuItems[position]
        val menuItemKey: String? = menuItemToDelete.key // Ensure 'key' is captured

        if (menuItemKey != null) {
            // Reference to the specific menu item in Firebase
            val foodMenuReference: DatabaseReference = databaseReference.child("menu").child(menuItemKey)

            // Remove the item from Firebase
            foodMenuReference.removeValue().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Remove the item from the list and notify the adapter
                    menuItems.removeAt(position)
                    binding.MenuRecyclerView.adapter?.notifyItemRemoved(position)
                    Toast.makeText(this, "Item Deleted Successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Item Not Deleted", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "Item Key is Null", Toast.LENGTH_SHORT).show()
        }
    }
}