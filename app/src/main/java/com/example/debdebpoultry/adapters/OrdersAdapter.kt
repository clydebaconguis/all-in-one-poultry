package com.example.debdebpoultry.adapters

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat.getColor
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.debdebpoultry.R
import com.example.debdebpoultry.config.ApiUrlRoutes
import com.example.debdebpoultry.config.SharedPref
import com.example.debdebpoultry.models.OrdersModel
import com.example.debdebpoultry.pages.OrdersFragment
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.HashMap

class OrdersAdapter(private val context: Context, private val itemList:ArrayList<OrdersModel>): RecyclerView.Adapter<OrdersAdapter.MyViewHolder>() {
    private var list = ArrayList<OrderDetails>()
    private lateinit var spf : SharedPref
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val code : TextView = itemView.findViewById(R.id.tvTransac)
        val status:  TextView = itemView.findViewById(R.id.tvStatus)
        val viewOrder:  Button = itemView.findViewById(R.id.btnViewOrder)
        val cancel:  TextView = itemView.findViewById(R.id.btnCancelOrder)
        val deliveryDate:  TextView = itemView.findViewById(R.id.deliveryDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.orders_item_row, parent, false)
        return MyViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showOrderDialogue(current: OrdersModel) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val container = LayoutInflater.from(context).inflate(R.layout.custom_dialogue_recycler_orders,null,false)
        val contain = container.findViewById<LinearLayoutCompat>(R.id.containerOrderItem)
        val totPrice = container.findViewById<TextView>(R.id.tvTotalPrice)
        val btnClose = container.findViewById<Button>(R.id.btnClose)
        val btnReceived = container.findViewById<Button>(R.id.btnOrderReceived)
        btnClose.setOnClickListener {
            dialog.dismiss()
        }
        val parsedDate = LocalDateTime.parse(current.date, DateTimeFormatter.ISO_DATE_TIME)
        val formattedDate = parsedDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
        val totalPrice =  "Total ${current.total}  via (${current.paymentOpt}) $formattedDate"
        totPrice.text = totalPrice
        dialog.setContentView(container)

        list.forEach {
            val row = LayoutInflater.from(context).inflate(R.layout.custom_dialogue_orders,null,false)
            val name = row.findViewById<TextView>(R.id.tvProduct)
            val image = row.findViewById<ImageView>(R.id.cardImg)
            val qty = row.findViewById<TextView>(R.id.tvTrayQuantity)
            val unit = row.findViewById<TextView>(R.id.tvUnit)

            name.text = it.title.replaceFirstChar { it.uppercase() }
            val imgHost = ApiUrlRoutes().hostImg + it.image
            Picasso.get().load(imgHost).into(image)
            val tempQuan = "x" + it.qty
            qty.text =  tempQuan
            unit.text = it.size.replaceFirstChar { it.uppercase() }

            contain.addView(row)
        }
        dialog.show()

    }
    fun getOrderByTransaction(current: OrdersModel){
        list.clear()
        val url = ApiUrlRoutes(current.id).getOrderDetails
        val stringRequest= @RequiresApi(Build.VERSION_CODES.O)
        object : StringRequest(
            Method.GET,url,
            Response.Listener{
                try {
                    val ja = JSONArray(it)
                    var index = 0
                    while (index < ja.length() ){
                        val jo = ja.getJSONObject(index)

                        val orderId = jo.getInt("id")
                        val image = jo.getString("image")
                        val name = jo.getString("name")
                        val qty = jo.getInt("qty")
                        val size = jo.getString("size")

                        list.add(OrderDetails(orderId,image,name,qty,size))
                        index++
                    }

                    showOrderDialogue(current)

                }catch (e: Exception){
                    e.printStackTrace()
                }
            },
            Response.ErrorListener {
                Toast.makeText(context, it.toString(), Toast.LENGTH_SHORT).show()
            }){}

        val queue = Volley.newRequestQueue(context)
        queue.add(stringRequest)
    }
    private fun cancelOrder(id: Int, position: Int) : Boolean{
        var deleted = false
        val url = "https://larapoultry.herokuapp.com/api/orderstat/${id}/cancel"
        val stringRequest= object : StringRequest(
            Method.POST,url,
            Response.Listener{
                if (it.isNotBlank()){
                    deleted = true
                    itemList[position].status ="cancel"
                    notifyItemChanged(position)
                    notifyItemRemoved(position)
                    Toast.makeText(context, "Canceled",Toast.LENGTH_SHORT).show()
                } else{
                    Toast.makeText(context, "Refresh list pls",Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener {
                Toast.makeText(context, it.toString(), Toast.LENGTH_SHORT).show()
            }){
            override fun getParams(): MutableMap<String, String> {
                val params= HashMap<String,String>()
                params["status"] = "cancel"
                return params
            }
        }

        val queue = Volley.newRequestQueue(context)
        queue.add(stringRequest)

        return deleted
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = itemList[position]
        val code = "OrderId " + currentItem.transaction
        if (currentItem.status == "delivery"){
            holder.viewOrder.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context,R.color.black ))
            holder.viewOrder.isEnabled = true
            holder.deliveryDate.setTextColor(Color.BLACK)
            holder.code.setTextColor(Color.BLACK)
            holder.cancel.isVisible = false
            holder.code.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_baseline_check_circle_24,0,0,0)
            holder.deliveryDate.text = "Estimated date to deliver is "+ currentItem.dateToDeliver
        }
        if(currentItem.status == "delivered"){
            holder.viewOrder.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context,R.color.black ))
            holder.viewOrder.isEnabled = true
            holder.deliveryDate.setTextColor(Color.GRAY)
            holder.code.setTextColor(Color.GRAY)
            holder.cancel.isVisible = false
            holder.code.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_baseline_check_circle_24,0,0,0)
            holder.deliveryDate.text = "Delivered on "+currentItem.dateDelivered
        }
        if(currentItem.status == "failed"){
            holder.viewOrder.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context,R.color.black ))
            holder.viewOrder.isEnabled = true
            holder.deliveryDate.setTextColor(Color.BLACK)
            holder.code.setTextColor(Color.BLACK)
            holder.cancel.isVisible = false
            holder.code.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_baseline_check_circle_24,0,0,0)
            holder.deliveryDate.text = "Order Rescheduled successfully on " + currentItem.dateToDeliver
        }
        if (currentItem.status == "cancel"){
            holder.deliveryDate.text = "Order Canceled"
            holder.viewOrder.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context,R.color.light_gray ))
            holder.viewOrder.isEnabled = false
            holder.cancel.isVisible = false
            holder.deliveryDate.setTextColor(Color.GRAY)
            holder.code.setTextColor(Color.GRAY)
            holder.code.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_baseline_cancel_24,0,0,0)
        }
        if (currentItem.status == "for approval" || currentItem.status == "preparing for delivery" ){
            holder.deliveryDate.text = "Pending"
            holder.cancel.isVisible = true
            holder.viewOrder.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context,R.color.black ))
            holder.viewOrder.isEnabled = true
            holder.deliveryDate.setTextColor(Color.BLACK)
            holder.code.setTextColor(Color.BLACK)
            holder.code.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_baseline_check_circle_24,0,0,0)
            val parsedDate = LocalDateTime.parse(currentItem.date, DateTimeFormatter.ISO_DATE_TIME)
            val formattedDate = parsedDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
            holder.deliveryDate.text = "Pending \nAdded on $formattedDate"
        }

        holder.code.text = code
        holder.status.text = "Status " + currentItem.status
        holder.viewOrder.setOnClickListener {
            getOrderByTransaction(currentItem)
        }
        holder.cancel.paintFlags = holder.cancel.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        holder.cancel.setOnClickListener {
            if (currentItem.status == "for approval"){
                cancelOrder(currentItem.id, position)
            }
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

}
data class OrderDetails(val id: Int, val image: String, val title: String,
                        val qty: Int, val size: String)