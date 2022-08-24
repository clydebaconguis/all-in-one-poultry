package com.example.debdebpoultry.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.debdebpoultry.R
import com.example.debdebpoultry.components.CheckOut
import com.example.debdebpoultry.config.ApiUrlRoutes
import com.example.debdebpoultry.models.CartModel
import com.example.debdebpoultry.models.CartModel2
import com.squareup.picasso.Picasso

class CartAdapter(private var totalAmount:TextView,private val btnCheckOut:Button, private val context: Context, private val cartList:ArrayList<CartModel>): RecyclerView.Adapter<CartAdapter.MyViewHolder>() {
    val list = ArrayList<CartModel2>()

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val productName : TextView = itemView.findViewById(R.id.tvProduct)
        val quanti:  TextView = itemView.findViewById(R.id.tvTrayQuantity)
        val total:  TextView = itemView.findViewById(R.id.tvTotal)
        val unit:  TextView = itemView.findViewById(R.id.tvSize)
        val img : ImageView = itemView.findViewById(R.id.cardImg)
        val checkbox : CheckBox = itemView.findViewById(R.id.checkbox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.cart_item_row, parent, false)
        return MyViewHolder(view)
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