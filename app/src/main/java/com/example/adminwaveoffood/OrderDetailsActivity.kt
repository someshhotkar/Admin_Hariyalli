package com.example.adminwaveoffood

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adminwaveoffood.adapter.OrderDetailsAdapter
import com.example.adminwaveoffood.databinding.ActivityOrderDetailsBinding
import com.example.adminwaveoffood.model.OrderDetails

class OrderDetailsActivity : AppCompatActivity() {
    private val binding: ActivityOrderDetailsBinding by lazy {
        ActivityOrderDetailsBinding.inflate(layoutInflater)
    }

    private var userName: String? = null
    private var address: String? = null
    private var phoneNumber: String? = null
    private var totalPrice: String? = null
    private var foodNames: ArrayList<String> = arrayListOf()
    private var foodImages: ArrayList<String> = arrayListOf()
    private var foodQuantity: ArrayList<Int> = arrayListOf()
    private var foodPrices: ArrayList<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            finish()
        }

        getDataFromIntent()
        setAdapter()
    }

    private fun getDataFromIntent() {
        val receivedOrderDetails: OrderDetails? = intent.getSerializableExtra("UserOrderDetails") as OrderDetails?
        receivedOrderDetails?.let { orderDetails ->
            userName = orderDetails.userName
            foodNames = (orderDetails.foodNames ?: arrayListOf()) as ArrayList<String>
            foodImages = (orderDetails.foodImages ?: arrayListOf()) as ArrayList<String>
            foodQuantity = (orderDetails.foodQuantities ?: arrayListOf()) as ArrayList<Int>
            address = orderDetails.address
            phoneNumber = orderDetails.phoneNumber
            foodPrices = (orderDetails.foodPrices ?: arrayListOf()) as ArrayList<String>
            totalPrice = orderDetails.totalPrice
            setUserDetail()
        }
    }

    private fun setUserDetail() {
        binding.name?.text = userName ?: "Default Name"
        binding.address?.text = address ?: "Default Address"
        binding.totalPays?.text = totalPrice ?: "Default Price"
    }

    private fun setAdapter() {
        binding.orderDetailRecyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = OrderDetailsAdapter(
            context = this,
            foodNames = foodNames,
            foodImages = foodImages,
            foodQuantities = foodQuantity,
            foodPrices = foodPrices
        )
        binding.orderDetailRecyclerView.adapter = adapter
    }
}
