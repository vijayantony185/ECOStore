package com.eco.store.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.eco.store.data.Product

class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "product.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_NAME = "products"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_QUANTITY = "quantity"
        private const val COLUMN_PRICE = "price"
        private const val COLUMN_TOTAL_VALUE = "total_value"
    }
    override fun onCreate(p0: SQLiteDatabase?) {
        val createTableQuery = "CREATE TABLE IF NOT EXISTS $TABLE_NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_NAME TEXT," +
                "$COLUMN_QUANTITY INTEGER," +
                "$COLUMN_PRICE REAL," +
                "$COLUMN_TOTAL_VALUE REAL);"
        p0?.execSQL(createTableQuery)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {

    }

    fun insertProduct(context: Context, product: Product) : Long{
        val dbFile = context.getDatabasePath(DBHelper.DATABASE_NAME)

        val db = writableDatabase

        val values = ContentValues().apply {
            put(COLUMN_NAME, product.name)
            put(COLUMN_QUANTITY, product.quantity)
            put(COLUMN_PRICE, product.price)
            put(COLUMN_TOTAL_VALUE, product.totalValue)
        }

        return db.insert(TABLE_NAME, null, values)
    }

    fun readAllProducts() : List<Product> {
        val productList = mutableListOf<Product>()
        val db = readableDatabase

        val projection = arrayOf(COLUMN_ID, COLUMN_NAME, COLUMN_QUANTITY, COLUMN_PRICE, COLUMN_TOTAL_VALUE)
        val cursor = db.query(TABLE_NAME, projection, null, null, null, null, null)

        while (cursor.moveToNext()) {
            val id = cursor.getLong(cursor.getColumnIndex(COLUMN_ID))
            val name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME))
            val quantity = cursor.getInt(cursor.getColumnIndex(COLUMN_QUANTITY))
            val price = cursor.getFloat(cursor.getColumnIndex(COLUMN_PRICE))
            val totalValue = cursor.getFloat(cursor.getColumnIndex(COLUMN_TOTAL_VALUE))

            val product = Product(id, name, quantity, price, totalValue)
            productList.add(product)
        }

        cursor.close()
        return productList
    }

    fun insertProducts(productList: ArrayList<Product>): List<Long> {
        if (productList.isEmpty()) {
            // Handle the case where the list is null or empty
            return emptyList()
        }
        val db = writableDatabase
        val insertedIds = mutableListOf<Long>()

        for (product in productList) {
            val values = ContentValues().apply {
                put(COLUMN_NAME, product.name)
                put(COLUMN_QUANTITY, product.quantity)
                put(COLUMN_PRICE, product.price)
                put(COLUMN_TOTAL_VALUE, product.totalValue)
            }

            val insertedId = db.insert(TABLE_NAME, null, values)
            insertedIds.add(insertedId)
        }

        return insertedIds
    }

    fun updateProduct(product: Product) : Int {
        val db = writableDatabase

        val values = ContentValues().apply {
            put(COLUMN_NAME, product.name)
            put(COLUMN_QUANTITY, product.quantity)
            put(COLUMN_PRICE, product.price)
            put(COLUMN_TOTAL_VALUE, product.totalValue)
        }

        return db.update(TABLE_NAME, values, "$COLUMN_ID=?", arrayOf(product.id.toString()))
    }

    fun deleteProduct(id : Long):Int {
        val db = writableDatabase
        return db.delete(TABLE_NAME, "$COLUMN_ID=?", arrayOf(id.toString()))
    }

    fun deleteAllProducts():Int {
        val db = writableDatabase
        return db.delete(TABLE_NAME, null, null)
    }
}