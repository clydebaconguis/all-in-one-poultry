package com.example.debdebpoultry.models


data class OrdersModel(val id:Int, var transaction:String,
                       var status:String, val total: Double,
                       val paymentOpt: String, val dateDelivered:String, val dateToDeliver:String, val date:String) {
}

