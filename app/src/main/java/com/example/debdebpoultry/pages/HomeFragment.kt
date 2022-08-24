package com.example.debdebpoultry.pages

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.debdebpoultry.R
import com.example.debdebpoultry.adapters.ProductCategoryAdapter
import com.example.debdebpoultry.config.ApiUrlRoutes
import com.example.debdebpoultry.models.ProductCategoryModel
import org.json.JSONArray

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HomeFragment() : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var loading : ProgressBar
    private lateinit var recyclerView: RecyclerView
    private var list = ArrayList<ProductCategoryModel>()

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
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loading =  view.findViewById(R.id.progressBar)
        recyclerView = view.findViewById(R.id.recyclerHome)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)
        fetchData()
    }

    private fun fetchData() {
        loading.isVisible =  true
        val conf = ApiUrlRoutes()
        val url = conf.getProductCategories
        val stringRequest= object : StringRequest(
            Method.GET,url,
            Response.Listener{
                loading.isVisible =  false
                parseJson(it)
            },
            Response.ErrorListener {
                loading.isVisible =  false
                Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT).show()
            }){}

        val queue = Volley.newRequestQueue(requireContext())
        queue.add(stringRequest)
    }

    private fun parseJson(jsonResponse: String){
        var status = ""
        try {
            val ja = JSONArray(jsonResponse)
            var index = 0
            while (index < ja.length() ){
                val jo = ja.getJSONObject(index)
                val id = jo.getInt("id")
                val img = jo.getString("image")
                val name = jo.getString("name")
                val st = jo.getInt("status")
                if (st == 1){
                    status = "Available now"
                }else{
                    status = "Unavailable!"
                }
                list.add(ProductCategoryModel(id,name,status,img))
                index++
            }

            recyclerView.adapter =  ProductCategoryAdapter(requireContext(),list)

        }catch (e: Exception){
            e.printStackTrace()
        }
    }

}
