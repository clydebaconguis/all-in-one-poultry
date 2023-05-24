package com.example.debdebpoultry.models

import java.io.Serializable


data class CartModel(var stock: Int,var id:Int,var prod_id:Int, var prod_name:String,
                     var img:String, var size:String,var total:Double,
                     var tray:Int, var isChecked:Boolean = false)

