package com.example.debdebpoultry.components

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.debdebpoultry.R
import com.example.debdebpoultry.adapters.CartAdapter
import com.example.debdebpoultry.config.ApiUrlRoutes
import com.example.debdebpoultry.config.SharedPref
import com.example.debdebpoultry.models.CartModel
import com.example.debdebpoultry.models.CartModel2
import com.example.debdebpoultry.pages.MainActivity
import com.google.android.gms.location.*
import com.google.android.material.textfield.TextInputEditText
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class CheckOut : AppCompatActivity() {
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var carts : ArrayList<CartModel2>
    private lateinit var contain : LinearLayoutCompat
    private lateinit var containDetails : LinearLayoutCompat
    private lateinit var tvSubTotal : TextView
    private lateinit var editAddress : TextView
    private lateinit var editPhone : TextView
    private lateinit var tvDeliveryFee : TextView
    private lateinit var tvTotalPayment : TextView
    private lateinit var tvTotal : TextView
    private lateinit var rdCod : RadioButton
    private lateinit var rdGcash : RadioButton
    private lateinit var btnPlaceOrder : Button
    private lateinit var tvAddress : TextInputEditText
    private lateinit var tvPhone : TextInputEditText
    private lateinit var inputReference : EditText
    private lateinit var spf : SharedPref
    private lateinit var loading : ProgressBar
    private lateinit var GCash : TextView
    private var paymentOpt = ""
    private var totFee = 0.00
    private var lat: Double = 0.0
    private var long : Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_out)
        spf = SharedPref(this)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = ""

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        findViewById<TextView>(R.id.getLocation).setOnClickListener {
            fetchLocation()
        }
        contain = findViewById(R.id.contain)
        GCash = findViewById(R.id.gcash_num)
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
        editAddress = findViewById(R.id.editAddress)
        editPhone = findViewById(R.id.editPhone)
        tvPhone = findViewById(R.id.phone)
        inputReference = findViewById(R.id.inputReference)
        inputReference.isVisible = false
        val uncheck: Drawable? = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_radio_button_unchecked_24, null)
        val check: Drawable? = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_check_circle_24, null)
        tvDeliveryFee.text = "Php 100"
        rdCod.buttonDrawable = uncheck
        rdGcash.buttonDrawable = uncheck
        rdCod.setOnClickListener {
            paymentOpt = "COD"
            rdCod.buttonDrawable = check
            rdGcash.buttonDrawable = uncheck
            inputReference.isVisible = false
        }
        rdGcash.setOnClickListener {
            paymentOpt = "GCASH"
            rdCod.buttonDrawable = uncheck
            rdGcash.buttonDrawable = check
            inputReference.isVisible = true
        }
        btnPlaceOrder.setOnClickListener {
            if(tvAddress.text.toString().isEmpty() || tvPhone.text.toString().isEmpty() || paymentOpt.isBlank()){
                Toast.makeText(this, "Fill all fields!", Toast.LENGTH_SHORT).show()
            }
            if (paymentOpt == "GCASH" && inputReference.text.isEmpty()){
                Toast.makeText(this, "Input valid reference!", Toast.LENGTH_SHORT).show()
            }
            if (tvPhone.text.toString().length < 11){
                Toast.makeText(this, "Invalid phone number!", Toast.LENGTH_SHORT).show()
            }
            if (paymentOpt == "GCASH" && inputReference.text.isNotEmpty() && inputReference.text.length < 13){
                Toast.makeText(this, "minimum reference length not meet!", Toast.LENGTH_SHORT).show()
            }
            if (tvAddress.text.toString().isNotEmpty() && tvPhone.text.toString().isNotEmpty() && tvPhone.text.toString().length >= 11 && paymentOpt.isNotBlank() && paymentOpt == "COD"){
                saveOrder()
//                Toast.makeText(this, "success!", Toast.LENGTH_SHORT).show()
            }
            if (tvAddress.text.toString().isNotEmpty() && tvPhone.text.toString().isNotEmpty() && paymentOpt.isNotBlank() && paymentOpt == "GCASH" && inputReference.text.isNotEmpty() && inputReference.text.length >= 13 && tvPhone.text.toString().length >= 11 ){
                saveOrder()
//                Toast.makeText(this, "success!", Toast.LENGTH_SHORT).show()
            }
        }
        tvPhone.setText("+63 " + spf.phone)
        fetchGcash()
        addView()
    }
    private fun fetchGcash() {
        loading.isVisible = true
        val url = ApiUrlRoutes().getAccount
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
            var number = ""
            val ja = JSONArray(jsonResponse)
            var index = 0
            while (index < ja.length() ){
                val jo = ja.getJSONObject(index)
                val num = jo.getString("num")
//                val passcode = jo.getString("passcode")
                index++
                number += "\n$num"
                GCash.text = "Send to \n$number"
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    private fun fetchLocation() {
        val task = fusedLocationProviderClient.lastLocation

        if (ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION), 101)
        }
        if(isLocationEnabled()){
            task.addOnSuccessListener {
                if (it != null){
                    lat = it.latitude
                    long = it.longitude
                    getAddressInfo(lat,long)
//                    Toast.makeText(this,"$lat $long",Toast.LENGTH_SHORT).show()
                }else{
                    NewLocationData()
                }
            }
        }
        else{
            Toast.makeText(this,"Please turn on your device location",Toast.LENGTH_SHORT).show()
        }
    }

    fun NewLocationData(){
        var locationRequest =  LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 1
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,locationCallback, Looper.myLooper()
        )
    }


    private val locationCallback = object : LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult) {
            val lastLocation: Location? = locationResult.lastLocation
            if (lastLocation != null) {
                lat = lastLocation.latitude
                long = lastLocation.longitude
                Log.d("Debug:","your last last location: "+ lastLocation.longitude.toString())
                getAddressInfo(lat,long)
            }
        }
    }

    fun isLocationEnabled():Boolean{
        //this function will return to us the state of the location service
        //if the gps or the network provider is enabled then it will return true otherwise it will return false
        var locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun getAddressInfo(latitude:Double, longitude:Double){
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses: List<Address> = geocoder.getFromLocation(latitude, longitude, 1)

        val address: String = addresses[0].getAddressLine(0)
//        val city: String = addresses[0].locality
//        val state: String = addresses[0].adminArea
//        val country: String = addresses[0].countryName
//        val postalCode: String = addresses[0].postalCode
//        val knownName: String = addresses[0].featureName

        tvAddress.setText(address)
        Toast.makeText(applicationContext, address, Toast.LENGTH_SHORT).show()

    }


    private fun saveOrder(){
        val user_id = spf.userID.toString()
        val user_add = tvAddress.text.toString()
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
//                Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
                val jobj = JSONObject(it)
                val code = jobj.getString("trans_code")
                val name = spf.name.toString().replaceFirstChar{ it.uppercase() }

                val alertDialog = AlertDialog.Builder(this)
                    //set icon
                    .setIcon(R.drawable.ic_baseline_receipt_24)
                    //set title
                    .setTitle("Official Receipt")
                    //set message
                    .setMessage("$name \nAddress: $user_add \nPhone: ${tvPhone.text.toString()}" +
                            " \nTransaction ID: $code \n \nTotal Payment: $totFee")
                    //set positive button
                    .setPositiveButton("Ok", DialogInterface.OnClickListener { dialog, i ->
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }).setCancelable(false)
                    //set negative button
//                    .setNegativeButton("No", DialogInterface.OnClickListener { dialogInterface, i ->
//                        //set what should happen when negative button is clicked
//                        Toast.makeText(applicationContext, "Nothing Happened", Toast.LENGTH_LONG).show()
//                    })
                    .show()

                alertDialog
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
                params["phone"] = tvPhone.text.toString()
                params["total_payment"] = total_payment
                params["payment_opt"] = payment_opt
                params["products"] = jsonProducts.toString()
                params["status"] = status
                params["lat"] = lat.toString()
                params["long"] = long.toString()
                params["purpose"] = "store"
                if (paymentOpt == "GCASH"){
                    params["proof_of_payment"] = inputReference.text.toString()
                }
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
        totFee =  subTot + 100.00
        val tp = "Php $totFee"
        val tt = "Total: Php $totFee"
        tvSubTotal.text = sb
        tvTotalPayment.text = tp
        tvTotal.text = tt
        loading.isVisible = false

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}