package com.example.debdebpoultry.components

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.children
import androidx.core.view.forEach
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.debdebpoultry.R
import com.example.debdebpoultry.adapters.ProductCategoryAdapter
import com.example.debdebpoultry.adapters.ProductCategoryHorizontalAdapter
import com.example.debdebpoultry.config.ApiUrlRoutes
import com.example.debdebpoultry.config.SharedPref
import com.example.debdebpoultry.models.ProductCategoryModel
import com.example.debdebpoultry.models.ProductPriceModel
import com.example.debdebpoultry.pages.CartFragment
import com.example.debdebpoultry.pages.MainActivity
import com.google.android.material.textfield.TextInputEditText
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONObject
import java.util.HashMap
import kotlin.math.roundToInt

class ProductItem : AppCompatActivity() {

    private var priceList = ArrayList<ProductPriceModel>()
    //product id
    private var prod_id:Int = 0
    //transaction variable
    private var php = "Php"
    private var zero = "0.00"
    private var count = 0
    private var _price = 0.00
    private var totalPrice = 0.00
    private var size = ""
    private var type = ""
    private var stock = 0

    // view variables
    private lateinit var recyclerHorizontal : RecyclerView
    private lateinit var linearContain : LinearLayoutCompat
    private lateinit var toolbarTitle : TextView
    private lateinit var btn_add : ImageButton
    private lateinit var btn_min : ImageButton
    private lateinit var btn_add_cart : TextView
    private lateinit var cardImg : ImageView
    private lateinit var loading : ProgressBar
    private lateinit var price : TextView
    private lateinit var quanti : EditText
    private lateinit var total : TextView
    private lateinit var spf : SharedPref
    private var productList = ArrayList<ProductCategoryModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_item)
        spf = SharedPref(this)
        // set id
        loading = findViewById(R.id.progressBar)
        recyclerHorizontal = findViewById(R.id.recyclerHorizontal)
        recyclerHorizontal.layoutManager = LinearLayoutManager (this, LinearLayoutManager.HORIZONTAL, false)
        recyclerHorizontal.setHasFixedSize(true)
        price = findViewById(R.id.price)
        linearContain = findViewById(R.id.linearContainer)
        cardImg = findViewById(R.id.cardImg)
        quanti = findViewById(R.id.trayQuantity)
        btn_add = findViewById(R.id.addTray)
        btn_min = findViewById(R.id.minusTray)
        total = findViewById(R.id.totalAmount)
        btn_add_cart = findViewById(R.id.btnAddToCard)
        // set toolbar
        setSupportActionBar(findViewById(R.id.toolbarProduct))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = ""
        toolbarTitle = findViewById(R.id.toolbarTitle)
        // get intent
        getIncomingIntent()
        quanti.setText(count.toString())
        val initP = "$php $zero"
        price.text = initP
        total.text = initP

        // button plus
        btn_add.setOnClickListener {
            counter("plus")
        }
        // button minus
        btn_min.setOnClickListener {
            counter("minus")
        }

        btn_add_cart.setOnClickListener{
            if (totalPrice > 0.00 &&
                !quanti.text.isNullOrEmpty() &&
                quanti.text.toString().toInt() > 0 &&
                size.isNotEmpty()){
                if (quanti.text.toString().toInt() <= stock){
                    postCart()
                }else{
                    Toast.makeText(this, "quantity exceeds stock limit!",Toast.LENGTH_SHORT).show()
                }
            }
            else{
                Toast.makeText(this, "Fill all fields!",Toast.LENGTH_SHORT).show()
            }
        }

        quanti.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
               compute()
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
    }

    private fun addView() {
        var i = 0
        while (i < priceList.size){
            val tvPrices = layoutInflater.inflate(R.layout.tv_new_view_price,null,false)
            val p = tvPrices.findViewById<TextView>(R.id.textViewPrice)
            val h = tvPrices.findViewById<TextView>(R.id.textViewHid)
            val h2 = tvPrices.findViewById<TextView>(R.id.textViewHid2)
            val jo = priceList[i]
            p.text = jo.unit
            val devshare = jo.value + (0.05 * jo.value)
            h.text = devshare.toString()
            h2.text = jo.type

            p.setOnClickListener {
                size =  p.text.toString()
                type = h2.text.toString()
                val temp = h.text.toString()
                _price = temp.toDouble()
                var initP = 0.0
                initP = if (type.trim().lowercase() == "eggs" || type.trim().lowercase() == "egg"){
                    (jo.value*0.05 + jo.value) * 30
                }else{
                    (jo.value*0.05 + jo.value)
                }
                price.text = "Php $initP"
                compute()
                val childCount = linearContain.childCount
                var index = 0
                while (index < childCount) {
                    val element = linearContain.getChildAt(index)
                    if (element is ViewGroup){
                        val highlight = element.findViewById<TextView>(R.id.textViewPrice)
                        if (highlight.text.toString() == size){
                            highlight.setTextColor(Color.WHITE)
                            highlight.setBackgroundResource(R.drawable.custom_card_dark)
                            highlight.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_baseline_check_circle_outline_24,0,0,0)
                        }else{
                            highlight.setTextColor(Color.BLACK)
                            highlight.setBackgroundColor(Color.WHITE)
                            highlight.setBackgroundResource(R.drawable.custom_card)
                            highlight.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,0,0)
                        }

                    }
                    index++
                }
            }
            linearContain.addView(tvPrices)
            val initial = linearContain.getChildAt(0)
            if (initial is ViewGroup){
                val elem = initial.findViewById<TextView>(R.id.textViewPrice)
                val elemPrice = initial.findViewById<TextView>(R.id.textViewHid)
                val elemType = initial.findViewById<TextView>(R.id.textViewHid2)
                elem.gravity = Gravity.CENTER_VERTICAL
                elem.setTextColor(Color.WHITE)
                elem.setBackgroundResource(R.drawable.custom_card_dark)
                elem.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_baseline_check_circle_outline_24,0,0,0)
                size =  elem.text.toString()
                type = elemType.text.toString()
                val temp = elemPrice.text.toString()
                _price = temp.toDouble()
                var initP = 0.0
                initP = if (type.trim().lowercase() == "eggs" || type.trim().lowercase() == "egg"){
                    (jo.value*0.05 + jo.value) * 30
                }else{
                    (jo.value*0.05 + jo.value)
                }
//                val initP = elemPrice.text.toString().toDouble() * 30
                price.text = "Php $initP"
                compute()
                i++
            }

        }
    }

    private fun postCart(){
        loading.isVisible = true
        val totalQuanti = count.toString()
        val totalPrice = totalPrice.toString()

        val url = ApiUrlRoutes().addCart
        val stringRequest= object : StringRequest(
            Method.POST,url,
            Response.Listener{
                loading.isVisible = false
                if (it.isNotEmpty()){
                    val response = JSONObject(it)
                    val tray = response.getInt("tray")
                    val total = response.getInt("total")
                    if (tray > 0 && total > 0){
                       Toast.makeText(this, "Added To Cart!", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            Response.ErrorListener {
                loading.isVisible = false
                Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
            }){
            override fun getParams(): MutableMap<String, String> {
                val params= HashMap<String,String>()
                params["user_id"] = spf.userID.toString()
                params["product_category_id"] = prod_id.toString()
                params["tray"] = totalQuanti
                params["total"] = totalPrice
                params["size"] = size

                return params
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(stringRequest)
    }
    private fun counter(tag: String){
       if (quanti.text.isNotEmpty()){
           count = quanti.text.toString().toInt()
           if (tag == "plus" && count < 5000 ){
               count++
               quanti.setText(count.toString())
               compute()
           }
           else if(tag == "minus" && count > 0){
               count = quanti.text.toString().toInt()
               count--
               quanti.setText(count.toString())
               compute()
           }
           else if (tag == "minus" && count == 0){
               Toast.makeText(this, "invalid",Toast.LENGTH_SHORT).show()
           }
           else if (tag == "plus" && count >= 5000){
               Toast.makeText(this, "Orders Quantity must not exceed 5000!",Toast.LENGTH_SHORT).show()
           }
       }else {
           if (tag == "plus"){
               count = 0
               count++
               quanti.setText(count.toString())
           }
       }
    }

    private fun compute(){
        if (!quanti.text.isNullOrEmpty()){

            val p = _price
            val q = quanti.text.toString()
            if (type.trim().lowercase() == "eggs" || type.trim().lowercase() == "egg"){
                count = q.toInt()
                if(count < 5000){
                    totalPrice = (p * count * 30.00 * 100.0).roundToInt().toDouble() / 100.0
                    val sum = "$php $totalPrice"

                    total.text = sum
                }
            }else{
                count = q.toInt()
                totalPrice = (p * count * 100.0).roundToInt().toDouble() / 100.0
                val sum = "$php $totalPrice"
                total.text = sum
            }
        }
    }

    private fun getIncomingIntent() {
        if (intent.hasExtra("prodId") && intent.hasExtra("prodName")) {
            prod_id = intent.getIntExtra("prodId", 0)
            val name = intent.getStringExtra("prodName")
            stock = intent.getIntExtra("stock", 0)
            val imgUrl = intent.getStringExtra("img")
            Picasso.get().load(imgUrl).into(cardImg)
            // get data
            fetchData()
            val barLabel = name.toString().replaceFirstChar { it.uppercase() }  + " ($stock)"
            toolbarTitle.text = barLabel
        }
    }

    private fun fetchData() {
        loading.isVisible = true
        val conf = ApiUrlRoutes(prod_id)
        val url = conf.getPricing
        val stringRequest= object : StringRequest(
            Method.GET,url,
            Response.Listener{
                loading.isVisible = false
                parseJson(it)
            },
            Response.ErrorListener {
                loading.isVisible = false
                Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
            }){}

        val queue = Volley.newRequestQueue(this)
        queue.add(stringRequest)
    }

    private fun parseJson(jsonResponse: String){

        try {
            val ja = JSONObject(jsonResponse)
            val products = ja.getJSONArray("products")
            parseProduct(products)
            val prices = ja.getJSONArray("prices")
            var index = 0
            while (index < prices.length() ){
                val jo =  prices.getJSONObject(index)
                val unit = jo.getString("unit")
                val type = jo.getString("type")
                val value = jo.getDouble("value")
                priceList.add(ProductPriceModel(unit, type, value))
                index++
            }
            addView()

        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    private fun parseProduct(products: JSONArray) {
        var index = 0
        while (index < products.length() ){
            val jo = products.getJSONObject(index)
            val id = jo.getInt("id")
            val img = jo.getString("image")
            val name = jo.getString("name")
            val stock = jo.getInt("stock")
            val status = jo.getInt("status")
            if (id != prod_id) productList.add(ProductCategoryModel(id,name,status,img,stock))
            index++
        }

        recyclerHorizontal.adapter =  ProductCategoryHorizontalAdapter(this,productList)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    //setting menu in action bar
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.option_menu_cart,menu)
        return super.onCreateOptionsMenu(menu)
    }

    // actions on click menu items
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.cart -> {
            val intent = Intent(this,MainActivity::class.java)
            intent.putExtra("fragCart", "cartFragment")
            startActivity(intent)
            finish()
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }
}