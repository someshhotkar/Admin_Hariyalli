package com.example.adminwaveoffood

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adminwaveoffood.adapter.PendingOrderAdapter
import com.example.adminwaveoffood.databinding.ActivityPendingOrderBinding
import com.example.adminwaveoffood.model.OrderDetails
import com.google.firebase.database.*

class PendingOrderActivity : AppCompatActivity(), PendingOrderAdapter.OnItemClicked {

    private lateinit var binding: ActivityPendingOrderBinding
    private var listOfName: MutableList<String> = mutableListOf()
    private var listOfTotalPrice: MutableList<String> = mutableListOf()
    private var listOfImageFirstFoodOrder: MutableList<String> = mutableListOf()
    private var listOfOrderItem: ArrayList<OrderDetails> = arrayListOf()
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseOrderDetails: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPendingOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialization of database
        database = FirebaseDatabase.getInstance()

        // Initialization of databaseReference
        databaseOrderDetails = database.reference.child("OrderDetails")

        // Retrieve order details
        getOrdersDetails()

        // Back button click listener
        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun getOrdersDetails() {
        // Retrieve order details from Firebase database
        databaseOrderDetails.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (orderSnapshot in snapshot.children) {
                    val orderDetails: OrderDetails? = orderSnapshot.getValue(OrderDetails::class.java)
                    orderDetails?.let {
                        listOfOrderItem.add(it)
                    }
                }
                addDataToListForRecyclerView()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database errors if any
            }
        })
    }

    private fun addDataToListForRecyclerView() {
        // Add data to respective lists for populating the RecyclerView
        for (orderItem in listOfOrderItem) {
            orderItem.userName?.let { listOfName.add(it) }
            orderItem.totalPrice?.let { listOfTotalPrice.add(it) }
            orderItem.foodImages?.firstOrNull()?.let { listOfImageFirstFoodOrder.add(it) }
        }
        setAdapter()
    }

    private fun setAdapter() {
        binding.pendingOrderRecyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = PendingOrderAdapter(this, listOfName, listOfTotalPrice, listOfImageFirstFoodOrder, this)
        binding.pendingOrderRecyclerView.adapter = adapter
    }

    override fun onItemAcceptClickListener(position: Int) {
        // Handle item acceptance and update database
        val childItemPushKey: String? = listOfOrderItem[position].itemPushKey
        val clickItemOrderReference: DatabaseReference? = childItemPushKey?.let {
            database.reference.child("OrderDetails").child(it)
        }
        clickItemOrderReference?.child("orderAccepted")?.setValue(true)
        updateOrderAcceptStatus(position)
    }

  override fun onItemDispatchClickListener(position: Int) {
        // Handle item dispatch and update database
        val dispatchItemPushKey: String? = listOfOrderItem[position].itemPushKey
        val dispatchItemOrderReference: DatabaseReference? = dispatchItemPushKey?.let {
            database.reference.child("CompletedOrder").child(it)
        }
        dispatchItemOrderReference?.setValue(listOfOrderItem[position])
            ?.addOnSuccessListener {
                dispatchItemPushKey?.let { deleteThisItemFromOrderDetails(it) }
            }
    }

    private fun deleteThisItemFromOrderDetails(dispatchItemPushKey: String) {
        // Delete the item from OrderDetails after dispatch
        val orderDetailsItemsReference: DatabaseReference = database.reference.child("OrderDetails").child(dispatchItemPushKey)
        orderDetailsItemsReference.removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "Order is Dispatched", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Order is not Dispatched", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateOrderAcceptStatus(position: Int) {
        // Update order acceptance in user's BuyHistory and OrderDetails
        val userIdOfClickedItem: String? = listOfOrderItem[position].userUid
        val pushKeyOfClickedItem: String? = listOfOrderItem[position].itemPushKey

        if (userIdOfClickedItem != null && pushKeyOfClickedItem != null) {
            val buyHistoryReference: DatabaseReference =
                database.reference.child("user").child(userIdOfClickedItem).child("BuyHistory").child(pushKeyOfClickedItem)

            buyHistoryReference.child("orderAccepted").setValue(true)
            databaseOrderDetails.child(pushKeyOfClickedItem).child("orderAccepted").setValue(true)
        }
    }

    override fun onItemClickListener(position: Int) {
        val intent = Intent(this, OrderDetailsActivity::class.java)
        val userOrderDetails: OrderDetails = listOfOrderItem[position]
        intent.putExtra("UserOrderDetails", userOrderDetails) // Ensure OrderDetails implements Parcelable
        startActivity(intent)
    }
}
