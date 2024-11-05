package com.example.adminwaveoffood.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.adminwaveoffood.databinding.ItemItemBinding
import com.example.adminwaveoffood.model.AllMenu
import com.google.firebase.database.DatabaseReference

class MenuItemAdapter(

    private val context: Context,
    private val menuList: ArrayList<AllMenu>,
    databaseReference: DatabaseReference,
    private val onDeleteClickListner:(position :Int) ->Unit
) : RecyclerView.Adapter<MenuItemAdapter.AddItemViewHolder>() {

    private val itemQuantities = IntArray(menuList.size) { 1 }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddItemViewHolder {
        val binding = ItemItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AddItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddItemViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = menuList.size

    inner class AddItemViewHolder(private val binding: ItemItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                // Correct usage of variables
                val quantity = itemQuantities[position]
                val menuItem = menuList[position]
                val uriString: String? = menuItem.foodImage
                val uri: Uri? = uriString?.let { Uri.parse(it) }

                foodnameTextView.text = menuItem.foodName
                priceTextView.text = menuItem.foodPrice

                // Using Glide to load the image
                uri?.let {
                    Glide.with(context).load(uri).into(orderdFoodImage)
                }

                quantityTextView.text = quantity.toString()

                // Set button listeners
                minusButton.setOnClickListener { decreaseQuantity(position) }
                plusButton.setOnClickListener { increaseQuantity(position) }
                deleteButton.setOnClickListener {
                    onDeleteClickListner(position) }
            }
        }
    }

    private fun increaseQuantity(position: Int) {
        if (itemQuantities[position] < 10) {
            itemQuantities[position]++
            notifyItemChanged(position)
        }
    }

    private fun decreaseQuantity(position: Int) {
        if (itemQuantities[position] > 1) {
            itemQuantities[position]--
            notifyItemChanged(position)
        }
    }

    private fun deleteItem(position: Int) {
        menuList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, menuList.size)
    }
}
