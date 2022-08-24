package com.example.debdebpoultry.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
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
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.horizontal_product_category_item_row, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = itemList[position]

        holder.productName.text = currentItem.name.replaceFirstChar { it.uppercase() }
        holder.status.text=currentItem.status
        val imgHost = ApiUrlRoutes().hostImg + currentItem.img
        Picasso.get().load(imgHost).into(holder.img)

        holder.itemView.setOnClickListener {
            val id = currentItem.id
            val name =  currentItem.name
            val img = imgHost
            val intent = Intent(context, ProductItem::class.java)
            intent.putExtra("prodId", id)
            intent.putExtra("prodName", name)
            intent.putExtra("img", img)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}