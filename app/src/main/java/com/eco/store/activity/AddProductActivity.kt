package com.eco.store.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.eco.store.R
import com.eco.store.adapter.ProductAdapter
import com.eco.store.adapter.ProductListener
import com.eco.store.data.Product
import com.eco.store.database.DBHelper
import com.eco.store.databinding.ActivityAddNewProductBinding

class AddProductActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddNewProductBinding
    var _productList = MutableLiveData<ArrayList<Product>>()
    private var productList: LiveData<ArrayList<Product>> = _productList
    var list: ArrayList<Product> = ArrayList()
    private val lifecycleowner: LifecycleOwner = this
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_new_product)
        initToolbar()
        binding.btnAdd.setOnClickListener {
            list.add(
                Product(
                    name = resources.getResourceName(R.string.mobile),
                    price = 1f,
                    quantity = 1,
                    totalValue = 1f
                )
            )
            _productList.value = list
        }

        binding.rcvProduct.layoutManager = LinearLayoutManager(this)

        productList.observe(this) {
            binding.apply {
                btnAdd.isEnabled = it.size <= 4
                btnSave.isVisible = it.size >= 1
                btnAdd.background = if (it.size <= 4) ContextCompat.getDrawable(this@AddProductActivity,R.drawable.bg_button) else ContextCompat.getDrawable(this@AddProductActivity,R.drawable.bg_button_disabled)
                rcvProduct.adapter =
                    ProductAdapter(
                        lifecycleowner,
                        this@AddProductActivity,
                        it,
                        object : ProductListener {
                            override fun remove(product: Product) {
                                list.remove(product)
                                _productList.value = list
                            }
                        })
                btnSave.setOnClickListener {
                    val dbHelper = DBHelper(this@AddProductActivity)
                    productList.value?.let { it1 -> dbHelper.insertProducts(it1) }
                    finish()
                }
            }
        }
    }

    private fun initToolbar() {
        binding.toolbar.apply {
            title = resources.getString(R.string.toolbarTitle)
            setNavigationIcon(R.drawable.arrow_back_24)
            setTitleTextColor(ContextCompat.getColor(this@AddProductActivity, R.color.white))
            setSupportActionBar(this)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

}