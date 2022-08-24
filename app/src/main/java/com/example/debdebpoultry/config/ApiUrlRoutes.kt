package com.example.debdebpoultry.config

import android.content.Context
import android.content.Context.WIFI_SERVICE
import android.net.wifi.WifiManager
import android.text.format.Formatter.formatIpAddress
import java.util.*

class ApiUrlRoutes(val id:Int=0) {

    private val hostMain = "https://larapoultry.herokuapp.com"
    private var host = "$hostMain/api/"

    var hostImg = "https://drive.google.com/uc?export=view&id="

    //Products
    val getProductCategories = host + "product_categories"
    val getPricing = host +  "pricings/$id"

    //Carts
    val addCart = host + "carts"
    val getCart = host + "carts/$id"

    //login
    val login = host + "login"
    val register = host + "register"

    //Orders
    val saveOrder = host + "transactions"
    val getTransac = host + "transactions/$id"

    val logout = host + "logout"
}