package com.example.debdebpoultry.pages

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.debdebpoultry.R
import com.example.debdebpoultry.config.SharedPref
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.lang.Compiler.enable


class MainActivity : AppCompatActivity() {
    private lateinit var bottomNav:BottomNavigationView
    private lateinit var barTitle:TextView
    private lateinit var spf : SharedPref
    private val tHome = "Poultry Ordering"
    private val tOrder = "Purchases"
    private val tNotif = "Notification"
    private val tCart = "Cart"
    private val tProfile = "Profile"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        spf = SharedPref(this)


        barTitle = findViewById(R.id.barTitle)
        bottomNav= findViewById(R.id.bottomNav)

        val frag = intent.getStringExtra("fragCart")
        if (frag != null) {
            loadFragment(CartFragment())
            barTitle.text = tCart
            bottomNav.selectedItemId = R.id.cart
        }else{
            loadFragment(HomeFragment())
            barTitle.setText(tHome).toString()
            bottomNav.selectedItemId = R.id.home
        }
        //setting toolbar
        setSupportActionBar(findViewById(R.id.toolbarMain))
        //setting bottom Nav
//        bottomNav.selectedItemId = R.id.home
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
//                R.id.notification -> {
//                    loadFragment(NotificationFragment())
//                    barTitle.text = tNotif
//                }
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
