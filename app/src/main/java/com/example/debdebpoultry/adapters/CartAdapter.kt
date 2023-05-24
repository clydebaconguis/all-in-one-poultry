package com.example.debdebpoultry.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.debdebpoultry.R
import com.example.debdebpoultry.components.CheckOut
import com.example.debdebpoultry.config.ApiUrlRoutes
import com.example.debdebpoultry.models.CartModel
import com.example.debdebpoultry.models.CartModel2
import com.example.debdebpoultry.models.ProductPriceModel
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.util.HashMap

class CartAdapter(private var totalAmount:TextView,private val btnCheckOut:Button, private val context: Context, private val cartList:ArrayList<CartModel>): RecyclerView.Adapter<CartAdapter.MyViewHolder>() {
    val list = ArrayList<CartModel2>()

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val productName : TextView = itemView.findViewById(R.id.tvProduct)
        val quanti:  TextView = itemView.findViewById(R.id.tvTrayQuantity)
        val total:  TextView = itemView.findViewById(R.id.tvTotal)
        val unit:  TextView = itemView.findViewById(R.id.tvSize)
        val img : ImageView = itemView.findViewById(R.id.cardImg)
        val minusQty : ImageButton = itemView.findViewById(R.id.btnMinusQty)
        val addQty : ImageButton = itemView.findViewById(R.id.btnAddQty)
        val deleteCart : ImageButton = itemView.findViewById(R.id.btnDeleteCart)
        val checkbox : CheckBox = itemView.findViewById(R.id.checkbox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.cart_item_row, parent, false)
        return MyViewHolder(view)
    }

    private fun deleteCart(currentItem: CartModel, position: Int) {
        val url = ApiUrlRoutes(currentItem.id).getCart
        val stringRequest= object : StringRequest(
            Method.DELETE,url,
            Response.Listener{
                Toast.makeText(context, "Deleted!", Toast.LENGTH_SHORT).show()
                cartList.removeAt(position)
                notifyItemRemoved(position)
            },
            Response.ErrorListener {
                Toast.makeText(context, it.toString(), Toast.LENGTH_SHORT).show()
            }){}

        val queue = Volley.newRequestQueue(context)
        queue.add(stringRequest)
    }

    private fun alterCart(currentItem: CartModel, position: Int) {
        val url = ApiUrlRoutes(currentItem.id).getCart
            val stringRequest= object : StringRequest(
                Method.PUT,url,
                Response.Listener{
                },
                Response.ErrorListener {
                    Toast.makeText(context, it.toString(), Toast.LENGTH_SHORT).show()
                }){
                override fun getParams(): MutableMap<String, String> {
                    val params= HashMap<String,String>()
                    params["tray"] = currentItem.tray.toString()
                    params["total"] = currentItem.total.toString()
                    return params
                }
            }
            val queue = Volley.newRequestQueue(context)
            queue.add(stringRequest)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = cartList[position]
        val total = "Php" + " " + currentItem.total.toString()
        val num = "x" + currentItem.tray.toString()

        holder.productName.text = currentItem.prod_name.replaceFirstChar { it.uppercase() }
        holder.quanti.text = num
        holder.total.text = total
        holder.unit.text = currentItem.size.replaceFirstChar { it.uppercase() }
        val imgHost = ApiUrlRoutes().hostImg + currentItem.img
        Picasso.get().load(imgHost).into(holder.img)
        holder.checkbox.isChecked = currentItem.isChecked

        holder.deleteCart.setOnClickListener {
            deleteCart(currentItem,position)
        }
        holder.minusQty.setOnClickListener {
            val defaultPrice = (currentItem.total / currentItem.tray)
            if (currentItem.tray == 1){
                deleteCart(currentItem,position)
            }else{
                currentItem.tray--
                currentItem.total = defaultPrice * currentItem.tray
                holder.total.text = "Php ${currentItem.total}"
                holder.quanti.text =  "x${currentItem.tray}"
                alterCart(currentItem, position)
            }
        }
        holder.addQty.setOnClickListener {
            val defaultPrice = (currentItem.total / currentItem.tray)
            if (currentItem.tray <= currentItem.stock){
                currentItem.tray++
                currentItem.total = defaultPrice * currentItem.tray
                holder.total.text = "Php ${currentItem.total}"
                holder.quanti.text = "x${currentItem.tray}"
                alterCart(currentItem, position)
            }else{
                Toast.makeText(context, "exceeds stock limit!", Toast.LENGTH_SHORT).show()
            }
        }

        holder.checkbox.setOnCheckedChangeListener { compoundButton, b ->
            var tempTotal = 0.00
            currentItem.isChecked = b
            cartList.forEach {
                if (it.isChecked) {
                    tempTotal += it.total.toString().toDouble()
                }
            }
            val partial =  "Total: Php $tempTotal"
            totalAmount.text = partial
        }

        btnCheckOut.setOnClickListener {
            list.clear()
            cartList.forEach {
                if (it.isChecked) {
                    list.add(CartModel2(it.id, it.prod_id, it.prod_name, it.img, it.total, it.tray,it.size))
                }
            }
            if (list.isNotEmpty()) {
                val intent = Intent(context, CheckOut::class.java)
                intent.putExtra("carts", list)
                context.startActivity(intent)

            } else {
                Toast.makeText(context, "No item selected!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int {
        return cartList.size
    }
}