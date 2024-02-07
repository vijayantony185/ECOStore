package com.eco.store.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.eco.store.R
import com.eco.store.adapter.ExistingProductAdapter
import com.eco.store.adapter.ExistingProductListener
import com.eco.store.data.Product
import com.eco.store.database.DBHelper
import com.eco.store.databinding.ActivityHomeBinding
import com.eco.store.databinding.DialogUpdateProductBinding

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private val dbHelper = DBHelper(this)
    private lateinit var adapter: ExistingProductAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)

        binding.floatingAddButton.setOnClickListener {
            startActivity(Intent(this, AddProductActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        initUI()
    }

    private fun initUI() {
        val dbHelper = DBHelper(this)
        val list: List<Product> = dbHelper.readAllProducts()
        val arraylist = ArrayList(list)
        binding.ivEmptyBox.isVisible = arraylist.size == 0
        binding.tvNoProducts.isVisible = arraylist.size == 0
        binding.tvProductList.isVisible = arraylist.size >= 1
        binding.btnClearAll.isVisible = arraylist.size >= 1
        binding.btnClearAll.setOnClickListener {
            deleteAllProductsConfirmationAlertDialog()
        }
        binding.rcvProductList.layoutManager = LinearLayoutManager(this)
        adapter = ExistingProductAdapter(this, arraylist, object : ExistingProductListener {
            override fun edit(product: Product, position: Int) {
                editProductDialog(product, position)
            }

            override fun delete(product: Product) {
                deleteConfirmationAlertDialog(product)
            }
        })
        binding.rcvProductList.adapter = adapter
    }

    private fun deleteAllProductsConfirmationAlertDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle(resources.getString(R.string.delete))
        alertDialogBuilder.setMessage(resources.getString(R.string.deleteAllProductsMessage))
        alertDialogBuilder.setPositiveButton(resources.getString(R.string.yes)) { dialog, _ ->
            dbHelper.deleteAllProducts()
            initUI()
            dialog.dismiss()
        }
        alertDialogBuilder.setNegativeButton(resources.getString(R.string.no)) { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun deleteConfirmationAlertDialog(product: Product) {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle(resources.getString(R.string.delete))
        alertDialogBuilder.setMessage(resources.getString(R.string.deleteProductMessage))
        alertDialogBuilder.setPositiveButton(resources.getString(R.string.yes)) { dialog, _ ->
            dbHelper.deleteProduct(product.id)
            initUI()
            dialog.dismiss()
        }
        alertDialogBuilder.setNegativeButton(resources.getString(R.string.no)) { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun editProductDialog(product: Product, position: Int) {
        val binding: DialogUpdateProductBinding = DataBindingUtil.inflate(
            LayoutInflater.from(this),
            R.layout.dialog_update_product,
            null,
            false
        )
        val builder = AlertDialog.Builder(this)
        builder.setView(binding.root)

        updateCustomDialogUI(binding, product)

        binding.btnPlus.setOnClickListener {
            product.quantity++
            updateCustomDialogUI(binding, product)
        }
        binding.btnMinus.setOnClickListener {
            if (product.quantity > 1) {
                product.quantity--
                updateCustomDialogUI(binding, product)
            }
        }


        textChange(binding, binding.edtProductPrice, product)
        textChange(binding, binding.edtProductQuantity, product)

        builder.setPositiveButton(resources.getString(R.string.submit)) { dialog, _ ->
            dbHelper.updateProduct(product)
            adapter.notifyItemChanged(position)
            dialog.dismiss()
        }

        builder.setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun updateCustomDialogUI(binding: DialogUpdateProductBinding, product: Product) {
        binding.edtProductPrice.setText(product.price.toString())
        binding.edtProductQuantity.setText(product.quantity.toString())
        binding.edtProductPriceTotal.setText(product.totalValue.toString())
    }

    private fun textChange(
        binding: DialogUpdateProductBinding,
        editText: EditText,
        product: Product
    ) {
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