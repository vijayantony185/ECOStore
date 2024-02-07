package com.eco.store.adapter

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.eco.store.R
import com.eco.store.data.Product
import com.eco.store.databinding.AdapterProductBinding

data class ProductAdapter(
    val lifecycleOwner: LifecycleOwner,
    val context: Context,
    var productList: ArrayList<Product>,
    val listener: ProductListener
) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {
    inner class ViewHolder(private val binding: AdapterProductBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            setSpinner(product)
            updateUI(product)
            binding.btnDelete.setOnClickListener {
                listener.remove(product)
            }
            binding.btnPlus.setOnClickListener {
                product.quantity++
                updateUI(product)
            }
            binding.btnMinus.setOnClickListener {
                if (product.quantity > 1) {
                    product.quantity--
                    updateUI(product)
                }
            }
            textChange(binding.edtProductPrice, product)
            textChange(binding.edtProductQuantity, product)
        }

        private fun updateUI(product: Product) {
            val totalValue = product.price * product.quantity
            product.totalValue = totalValue
            binding.edtProductPrice.setText(product.price.toString())
            binding.edtProductQuantity.setText(product.quantity.toString())
            binding.edtProductPriceTotal.setText((product.price * product.quantity).toString())
        }

        private fun setSpinner(product: Product) {
            val adapter = ArrayAdapter.createFromResource(
                context,
                R.array.products, // replace with your array resource
                android.R.layout.simple_spinner_item
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerProductName.adapter = adapter
            binding.spinnerProductName.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    val selectedItem = p0?.getItemAtPosition(p2).toString()
                    product.name = selectedItem
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

            }

            val array = context.resources.getStringArray(R.array.products)
            val index = array.indexOf(product.name)
            binding.spinnerProductName.setSelection(index)
        }

        private fun textChange(editText: EditText, product: Product) {
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun afterTextChanged(p0: Editable?) {
                    if (!p0.isNullOrBlank()) {
                        try {
                            if (editText == binding.edtProductPrice) {
                                product.price = p0.toString().toFloat()
                            } else {
                                product.quantity = p0.toString().toInt()
                            }
                            val totalValue = product.price * product.quantity
                            product.totalValue = totalValue
                            binding.edtProductPriceTotal.setText(totalValue.toString())
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            AdapterProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(productList[position])
    }
}

interface ProductListener {
    fun remove(product: Product)
}
