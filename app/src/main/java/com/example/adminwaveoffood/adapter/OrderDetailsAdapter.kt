package com.example.adminwaveoffood.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.adminwaveoffood.databinding.OrderdetailItemsBinding

class OrderDetailsAdapter(
    private val context: Context,  // Corrected context declaration
    private val foodNames: ArrayList<String>,
    private val foodImages: ArrayList<String>,
    private val foodQuantities: ArrayList<Int>,  // Corrected variable name
    private val foodPrices: ArrayList<String>
) : RecyclerView.Adapter<OrderDetailsAdapter.OrderDetailsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderDetailsViewHolder {
        val binding: OrderdetailItemsBinding = OrderdetailItemsBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return OrderDetailsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderDetailsViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = foodNames.size

    inner class OrderDetailsViewHolder(private val binding: OrderdetailItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                foodName.text = foodNames[position]
                foodQuantity.text = foodQuantities[position].toString()  // Corrected variable name
                val uriString: String = foodImages[position]
                val uri: Uri = Uri.parse(uriString)  // Fixed Uri parsing
                Glide.with(context).load(uri).into(orderdFoodImage)  // Corrected the Glide syntax
                foodPrice.text = foodPrices[position]
            }
        }
    }
}
