package com.example.debdebpoultry.pages

import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.debdebpoultry.R
import com.example.debdebpoultry.config.ApiUrlRoutes
import com.example.debdebpoultry.config.SharedPref
import com.example.debdebpoultry.login.SignIn
import com.example.debdebpoultry.models.ProductCategoryModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONArray
import java.util.HashMap

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNav:BottomNavigationView
    private lateinit var barTitle:TextView
    private lateinit var productList: ArrayList<ProductCategoryModel>
    private lateinit var spf : SharedPref
    private val tHome = "Debdeb Poultry"
    private val tOrder = "Purchases"
    private val tNotif = "Notification"
    private val tCart = "Cart"
    private val tProfile = "Profile"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        spf = SharedPref(this)
        loadFragment(HomeFragment())
        barTitle = findViewById(R.id.barTitle)
        barTitle.setText(tHome).toString()
        //setting toolbar
        setSupportActionBar(findViewById(R.id.toolbarMain))
        //setting bottom Nav
        bottomNav= findViewById(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.home
        bottomNav.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home -> {
                    loadFragment(HomeFragment())
                    barTitle.text = tHome
                }
                R.id.purchased -> {
                    loadFragment(OrdersFragment())
                    barTitle.setText(tOrder).toString()
                }
                R.id.notification -> {
                    loadFragment(NotificationFragment())
                    barTitle.text = tNotif
                }
                R.id.cart -> {
                    loadFragment(CartFragment())
                    barTitle.text = tCart
                }
                R.id.profile -> {
                    loadFragment(ProfileFragment())
                    barTitle.text = tProfile
                }
            }
            true
        }
//        Toast.makeText(this,spf.getID().toString(), Toast.LENGTH_LONG).show()
    }
    private fun loadFragment(fragment:Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.commit()
    }

    //setting menu in action bar
//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        menuInflater.inflate(R.menu.option_menu,menu)
//        return super.onCreateOptionsMenu(menu)
//    }

    // actions on click menu items
//    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
//        R.id.reserve -> {
//            Toast.makeText(this,"Reserve", Toast.LENGTH_LONG).show()
//            true
//        }
//
//        R.id.signout -> {
//            logout()
//            true
//        }
//
//        else -> {
//            // If we got here, the user's action was not recognized.
//            // Invoke the superclass to handle it.
//            super.onOptionsItemSelected(item)
//        }
//    }
}
