package com.example.debdebpoultry.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.debdebpoultry.R
import com.example.debdebpoultry.adapters.ProductCategoryAdapter
import com.example.debdebpoultry.config.ApiUrlRoutes
import com.example.debdebpoultry.config.SharedPref
import com.example.debdebpoultry.models.ProductCategoryModel
import com.example.debdebpoultry.pages.MainActivity
import org.json.JSONArray
import org.json.JSONObject
import java.util.HashMap

class SignUp : AppCompatActivity() {

    private lateinit var btn_login:TextView
    private lateinit var btn_register:TextView
    private lateinit var email:EditText
    private lateinit var password:EditText
    private lateinit var fullname:EditText
    private lateinit var address:EditText
    private lateinit var loading:ProgressBar
    private lateinit var spf:SharedPref
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        spf = SharedPref(this)
        btn_login = findViewById(R.id.btn_login)
        btn_register = findViewById(R.id.btn_register)
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        fullname = findViewById(R.id.fullname)
        address = findViewById(R.id.address)
        loading = findViewById(R.id.progressBar)
        loading.isVisible = false

        btn_login.setOnClickListener{
            val intent = Intent(this, SignIn::class.java)
            startActivity(intent)
            finish()
        }

        btn_register.setOnClickListener {
            if (valid()){
                register()
            }
            else
            {
                Toast.makeText(this,"Fill all fields!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun valid() : Boolean{
        return email.text.isNotEmpty() && password.text.isNotEmpty() && fullname.text.isNotEmpty()
    }

    private fun register(){

        loading.isVisible = true
        val uemail = email.text.toString()
        val upass = password.text.toString()
        val ufname = fullname.text.toString()
        val uaddress= address.text.toString()
        val url = ApiUrlRoutes().register
        val stringRequest= object : StringRequest(
            Method.POST,url,
            Response.Listener{
                loading.isVisible = false
                val jo = JSONObject(it)
                val msg = jo.getString("message")
                if(msg.trim() == "success"){
                    parseJson(it)
                }else{
                    Toast.makeText(this,msg, Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener {
                loading.isVisible = false
                Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
            }){
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Accept"] = "application/json"
                return headers
            }


            override fun getParams(): MutableMap<String, String> {
                val params= HashMap<String,String>()
                params["email"]= uemail
                params["password"]= upass
                params["name"]= ufname
                params["address"]= uaddress

                return params
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(stringRequest)
    }

    private fun parseJson(jsonResponse: String){
        try {
            if(jsonResponse.isNotEmpty()){
                val jobj = JSONObject(jsonResponse)

                val juser = jobj.getJSONObject("user")
                val uID = juser.getInt("id")
                val uName = juser.getString("name")
                val uEmail = juser.getString("email")
                val uAddress = juser.getString("address")
                val uToken = jobj.getString("token")

                if (uID.toString().isNotEmpty() && uName.isNotEmpty() && uEmail.isNotEmpty() && uAddress.isNotEmpty() && uToken.isNotEmpty()){
                    spf.store(uID, uName, uAddress, uEmail, uToken)
                    Toast.makeText(this, "Welcome $uName", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }

        }catch (e: Exception){
            e.printStackTrace()
        }
    }
}