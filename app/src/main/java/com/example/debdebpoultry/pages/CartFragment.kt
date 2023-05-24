package com.example.debdebpoultry.pages

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.debdebpoultry.R
import com.example.debdebpoultry.adapters.CartAdapter
import com.example.debdebpoultry.adapters.ProductCategoryAdapter
import com.example.debdebpoultry.config.ApiUrlRoutes
import com.example.debdebpoultry.config.SharedPref
import com.example.debdebpoultry.models.CartModel
import com.example.debdebpoultry.models.ProductCategoryModel
import org.json.JSONArray
import org.json.JSONObject

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class CartFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var totalAmount:TextView
    private lateinit var btnCheckOut:Button
    private lateinit var loading : ProgressBar
    private lateinit var tvEmptyList : TextView
    private lateinit var spf : SharedPref
    private var list = ArrayList<CartModel>()
    private val inital = "Php 0.00"
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CartFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CartFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        spf = SharedPref(requireContext())
        tvEmptyList = view.findViewById(R.id.emptyLabelCart)
        loading =  view.findViewById(R.id.progressBar)
        totalAmount = view.findViewById(R.id.totalAmount)
        totalAmount.text = inital
        btnCheckOut = view.findViewById(R.id.btnCheckOut)
        recyclerView = view.findViewById(R.id.recyclerCart)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)
        tvEmptyList.isVisible = false
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayoutCart)
        swipeRefreshLayout.setOnRefreshListener {
            list.clear()
            fetchData()
            swipeRefreshLayout.isRefreshing = false
        }
        fetchData()
    }
    private fun fetchData() {
        loading.isVisible = true
        val USER_ID = spf.userID
        val url = ApiUrlRoutes( USER_ID).getCart
        val stringRequest= object : StringRequest(
            Method.GET,url,
            Response.Listener{
                loading.isVisible = false
                if (it.toString().isNotEmpty()){
                    parseJson(it)
                }else{
                    tvEmptyList.isVisible = true
                }
            },
            Response.ErrorListener {
                loading.isVisible = false
                Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT).show()
            }){}

        val queue = Volley.newRequestQueue(requireContext())
        queue.add(stringRequest)
    }
    private fun parseJson(jsonResponse: String){
        try {
            val ja = JSONArray(jsonResponse)
            var index = 0
            while (index < ja.length() ){
                val jo = ja.getJSONObject(index)
                val id = jo.getInt("id")
                val prod_id = jo.getInt("product_category_id")
                val prod_name = jo.getString("name")
                val prod_img = jo.getString("image")
                val size = jo.getString("size")
                val total = jo.getDouble("total")
                val tray = jo.getInt("tray")
                val stock = jo.getInt("stock")
                list.add(CartModel(stock, id, prod_id, prod_name, prod_img ,size, total, tray))
                index++
            }
            if (list.isEmpty()){
                tvEmptyList.isVisible = true
            }

            recyclerView.adapter =  CartAdapter(totalAmount,btnCheckOut,requireContext(),list)

        }catch (e: Exception){
            e.printStackTrace()
        }
    }

}