package com.example.debdebpoultry.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.debdebpoultry.R
import com.example.debdebpoultry.components.ProductItem
import com.example.debdebpoultry.config.ApiUrlRoutes
import com.example.debdebpoultry.models.ProductCategoryModel
import com.example.debdebpoultry.pages.HomeFragment
import com.example.debdebpoultry.pages.MainActivity
import com.squareup.picasso.Picasso

class ProductCategoryHorizontalAdapter(private val context: Context, private val itemList:ArrayList<ProductCategoryModel>): RecyclerView.Adapter<ProductCategoryHorizontalAdapter.MyViewHolder>() {
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val productName : TextView = itemView.findViewById(R.id.tvProduct)
        val img : ImageView = itemView.findViewById(R.id.cardImg)
        val status:  TextView = itemView.findViewById(R.id.tvStatus)
        val unavailable: ImageView = itemView.findViewById(R.id.unavailable)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.horizontal_product_category_item_row, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = itemList[position]

        holder.productName.text = currentItem.name.replaceFirstChar { it.uppercase() }
        val stocks = "Stocks " + currentItem.stock.toString()
        holder.status.text = stocks
        val imgHost = ApiUrlRoutes().hostImg + currentItem.img
        Picasso.get().load(imgHost).into(holder.img)

        if (currentItem.stock == 0 || currentItem.status == 0){
            holder.unavailable.isVisible = true
            holder.status.setTextColor(Color.RED)
            holder.status.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_baseline_cancel_24,0,0,0)
        }else{
            holder.status.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_baseline_check_circle_outline_24,0,0,0)
        }


        holder.itemView.setOnClickListener {
            if (currentItem.status > 0 && currentItem.stock > 0){
                val id = currentItem.id
                val name =  currentItem.name
                val img = imgHost
                val intent = Intent(context, ProductItem::class.java)
                intent.putExtra("prodId", id)
                intent.putExtra("prodName", name)
                intent.putExtra("img", img)
                context.startActivity(intent)
            }
            else{
                Toast.makeText(context, "Oops sorry no available!", Toast.LENGTH_LONG).show()
            }

        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}