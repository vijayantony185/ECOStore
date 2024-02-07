package com.eco.store.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eco.store.data.Product
import com.eco.store.databinding.AdapterExistingProductBinding

class ExistingProductAdapter(val context: Context, val list : ArrayList<Product>, val listener : ExistingProductListener) : RecyclerView.Adapter<ExistingProductAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: AdapterExistingProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product, position: Int) {
            binding.tvProductName.setText(product.name)
            binding.tvProductPrice.setText("Price : ₹ ${product.price}")
            binding.tvQuantity.setText("Quantity : ${product.quantity}")
            binding.tvTotalPrice.setText("Total : ₹ ${product.totalValue}")
            binding.ivEdit.setOnClickListener {
                listener.edit(product, position)
            }
            binding.ivDelete.setOnClickListener {
                listener.delete(product)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AdapterExistingProductBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position], position)
    }
}

interface ExistingProductListener{
    fun edit(product: Product, position: Int)
    fun delete(product: Product)
}