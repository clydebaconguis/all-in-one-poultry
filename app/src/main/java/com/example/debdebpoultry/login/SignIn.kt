package com.example.debdebpoultry.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.wifi.WifiManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.Formatter
import android.widget.*
import androidx.core.view.isVisible
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.debdebpoultry.R
import com.example.debdebpoultry.config.ApiUrlRoutes
import com.example.debdebpoultry.config.SharedPref
import com.example.debdebpoultry.pages.MainActivity
import org.json.JSONObject
import java.util.HashMap


class SignIn : AppCompatActivity() {

    private lateinit var input_email: EditText
    private lateinit var input_password: EditText
    private lateinit var btn_login:TextView
    private lateinit var btn_register:TextView
    private lateinit var loading: ProgressBar
    private lateinit var spf : SharedPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        spf = SharedPref(this)

        input_email = findViewById(R.id.email)
        input_password = findViewById(R.id.password)
        btn_login = findViewById(R.id.btn_login)
        btn_register = findViewById(R.id.btn_register)
        loading = findViewById(R.id.progressBar)
        loading.isVisible = false

        if(spf.hasToken()){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        btn_login.setOnClickListener{
            if(valid()){
                login()
            }
            else{
                Toast.makeText(this, "Fill all fields!", Toast.LENGTH_SHORT).show()
            }
        }

        btn_register.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun valid(): Boolean {
       return input_email.text.isNotEmpty() && input_password.text.isNotEmpty()
    }

    private fun login(){
        loading.isVisible = true
        val email = input_email.text.toString()
        val password = input_password.text.toString()
        val url = ApiUrlRoutes().login
        val stringRequest= object : StringRequest(
            Method.POST,url,
            Response.Listener{
                loading.isVisible = false
                val jo = JSONObject(it)
                val msg = jo.getString("message")
                if(msg.trim() == "success"){
                    parseJson(it)
                }else{
                    Toast.makeText(this,"Incorrect Email or Password!", Toast.LENGTH_SHORT).show()
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
                params["email"] = email
                params["password"]= password

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