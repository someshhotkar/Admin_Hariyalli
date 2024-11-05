package com.example.adminwaveoffood.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.adminwaveoffood.databinding.PendingOrderItemBinding

class PendingOrderAdapter(
    private val context: Context,
    private val customerNames: MutableList<String>,
    private val quantities: MutableList<String>,
    private val foodImages: MutableList<String>,
    private val itemClicked: OnItemClicked
) : RecyclerView.Adapter<PendingOrderAdapter.PendingOrderViewHolder>() {

    // Interface to handle item click events
    interface OnItemClicked {
        fun onItemAcceptClickListener(position: Int) // Fix spelling
        fun onItemDispatchClickListener(position: Int) // Fix spelling
        fun onItemClickListener(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PendingOrderViewHolder {
        val binding = PendingOrderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PendingOrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PendingOrderViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = customerNames.size

    inner class PendingOrderViewHolder(private val binding: PendingOrderItemBinding) : RecyclerView.ViewHolder(binding.root) {
        private var isAccepted = false

        fun bind(position: Int) {
            binding.apply {
                customerName.text = customerNames[position]
                pendingOredarQuantity.text = quantities[position]
                val uriString: String = foodImages[position]
                val uri: Uri = Uri.parse(uriString)

                // Use Glide to load the image
                Glide.with(context).load(uri).into(orderdFoodImage)

                orderedAcceptButton.apply {
                    text = if (!isAccepted) "Accept" else "Dispatch"
                    setOnClickListener {
                        if (!isAccepted) {
                            text = "Dispatch"
                            isAccepted = true
                            showToast("Order is Accepted")
                            itemClicked.onItemAcceptClickListener(position)
                        } else {
                            val adapterPosition = this@PendingOrderViewHolder.adapterPosition
                            if (adapterPosition != RecyclerView.NO_POSITION) {
                                customerNames.removeAt(adapterPosition)
                                quantities.removeAt(adapterPosition)
                                foodImages.removeAt(adapterPosition)
                                notifyItemRemoved(adapterPosition)
                                showToast("Order is Dispatched")
                                itemClicked.onItemDispatchClickListener(position)
                            }
                        }
                    }
                }

                // Set item click listener
                itemView.setOnClickListener {
                    itemClicked.onItemClickListener(position)
                }
            }
        }

        private fun showToast(message: String) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}
