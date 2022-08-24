package com.example.debdebpoultry.components

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.view.marginTop
import androidx.core.widget.ImageViewCompat
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.debdebpoultry.R
import com.example.debdebpoultry.config.ApiUrlRoutes
import com.example.debdebpoultry.config.SharedPref
import com.example.debdebpoultry.models.CartModel2
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONObject
import java.util.HashMap

class CheckOut : AppCompatActivity() {
    private lateinit var carts : ArrayList<CartModel2>
    private lateinit var contain : LinearLayoutCompat
    private lateinit var containDetails : LinearLayoutCompat
    private lateinit var tvSubTotal : TextView
    private lateinit var tvDeliveryFee : TextView
    private lateinit var tvTotalPayment : TextView
    private lateinit var tvTotal : TextView
    private lateinit var rdCod : RadioButton
    private lateinit var rdGcash : RadioButton
    private lateinit var btnPlaceOrder : Button
    private lateinit var tvAddress : TextView
    private lateinit var spf : SharedPref
    private lateinit var loading : ProgressBar
    private var paymentOpt : String = ""
    private var totFee = 0.00

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_out)
        spf = SharedPref(this)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = ""
        contain = findViewById(R.id.contain)
        containDetails = findViewById(R.id.containDetails)
        tvSubTotal = findViewById(R.id.tvSubTotal)
        tvDeliveryFee = findViewById(R.id.tvDeliveryFee)
        tvTotalPayment = findViewById(R.id.tvTotalPayment)
        tvTotal =  findViewById(R.id.tvTotal)
        tvAddress = findViewById(R.id.address)
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder)
        rdCod = findViewById(R.id.rdCod)
        rdGcash = findViewById(R.id.rdGcash)
        loading = findViewById(R.id.progressBar)
        val uncheck: Drawable? = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_radio_button_unchecked_24, null)
        val check: Drawable? = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_check_circle_24, null)
        rdCod.buttonDrawable = uncheck
        rdGcash.buttonDrawable = uncheck

        rdCod.setOnClickListener {
            paymentOpt = "COD"
            rdCod.buttonDrawable = check
            rdGcash.buttonDrawable = uncheck
        }
        rdGcash.setOnClickListener {
            paymentOpt = "GCASH"
            rdCod.buttonDrawable = uncheck
            rdGcash.buttonDrawable = check
        }
        btnPlaceOrder.setOnClickListener {
            if (paymentOpt.isNotEmpty()){
                saveOrder()
            }else{
                Toast.makeText(this, "Select payment option pls!", Toast.LENGTH_SHORT).show()
            }
        }

        tvAddress.text = spf.userAddress


        addView()
    }

    fun saveOrder(){
        val user_id = spf.userID.toString()
        val user_add = spf.userAddress.toString()
        val total_payment = totFee.toString()
        val payment_opt = paymentOpt
        val status = "for approval"

        loading.isVisible = true
        val jsonProducts = JSONArray()

        carts.forEach {
            val jsonProduct = JSONObject()

            jsonProduct.put("product_category_id",it.prod_id)
            jsonProduct.put("size",it.unit)
            jsonProduct.put("qty",it.tray)

            jsonProducts.put(jsonProduct)
        }

        val stringRequest= object : StringRequest(
            Method.POST, ApiUrlRoutes().saveOrder,

            Response.Listener{
                loading.isVisible = false
                Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
            },
            Response.ErrorListener {
                loading.isVisible = false
                Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Accept"] = "application/json"
                headers["Content-Type"] = "application/x-www-form-urlencoded"
                return headers
            }

            override fun getParams(): MutableMap<String, String> {
                val params= HashMap<String,String>()
                params["user_id"] = user_id
                params["user_add"] = user_add
                params["total_payment"] = total_payment
                params["payment_opt"] = payment_opt
                params["products"] = jsonProducts.toString()
                params["status"] = status
                return params
            }
        }

        val queue = Volley.newRequestQueue(this)
        queue.add(stringRequest)
    }


    private fun addView(){
        carts = intent?.getParcelableArrayListExtra("carts")!!
        var subTot = 0.00

        carts.forEach {
            val row = layoutInflater.inflate(R.layout.checkout_item_row,null,false)
            val name = row.findViewById<TextView>(R.id.tvProduct)
            val img = row.findViewById<ImageView>(R.id.cardImg)
            val total = row.findViewById<TextView>(R.id.tvTotal)
            val unit = row.findViewById<TextView>(R.id.tvUnit)
            val quantity = row.findViewById<TextView>(R.id.tvTrayQuantity)

            name.text = it.prod_name.toString().replaceFirstChar { it.uppercase() }
            val imgHost = ApiUrlRoutes().hostImg + it.img
            Picasso.get().load(imgHost).into(img)
            total.text = it.total.toString()
            subTot += it.total
            val tempQuan = "x" + it.tray.toString()
            quantity.text =  tempQuan
            unit.text = it.unit.toString().replaceFirstChar { it.uppercase() }

            contain.addView(row)
        }
        val sb = "Php $subTot"
        val df = "Php 100"
        totFee = 100.00 + subTot
        val tp = "Php $totFee"
        val tt = "Total: Php $totFee"
        tvSubTotal.text = sb
        tvDeliveryFee.text = df
        tvTotalPayment.text = tp
        tvTotal.text = tt
        loading.isVisible = false

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}