package com.example.debdebpoultry.pages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.debdebpoultry.R
import com.example.debdebpoultry.adapters.OrdersAdapter
import com.example.debdebpoultry.config.ApiUrlRoutes
import com.example.debdebpoultry.config.SharedPref
import com.example.debdebpoultry.models.OrdersModel
import org.json.JSONArray

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [OrdersFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OrdersFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var loading : ProgressBar
    private lateinit var emptyLabel : TextView
    private lateinit var recyclerView: RecyclerView
    private var list = ArrayList<OrdersModel>()
    private lateinit var spf : SharedPref
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
        return inflater.inflate(R.layout.fragment_orders, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment OrdersFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OrdersFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        spf = SharedPref(requireContext())
        loading =  view.findViewById(R.id.progressBar)
        recyclerView = view.findViewById(R.id.recyclerOrders)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)
        emptyLabel = view.findViewById(R.id.emptyLabelOrder)
        emptyLabel.isVisible = false
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayoutOrders)
        swipeRefreshLayout.setOnRefreshListener {
            list.clear()
            fetchDataOrders()
            swipeRefreshLayout.isRefreshing = false
        }
        fetchDataOrders()
    }

    private fun fetchDataOrders() {
        loading.isVisible =  true
        val url = ApiUrlRoutes(spf.userID).getTransac
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
        var dateToDeliver = ""
        var dateDelivered = ""
        try {
            val ja = JSONArray(jsonResponse)
            var index = 0
            while (index < ja.length() ){
                val jo = ja.getJSONObject(index)
                val id = jo.getInt("id")
                val code = jo.getString("trans_code")
                val status = jo.getString("status")
                val totalPayment = jo.getDouble("total_payment")
                val paymentOpt = jo.getString("payment_opt")
                val date = jo.getString("created_at")
                if (jo.has("date_to_deliver")){
                    dateToDeliver = jo.getString("date_to_deliver")
                }
                if (jo.has("date_delivered")){
                    dateDelivered = jo.getString("date_delivered")
                }
                list.add(OrdersModel(id, code, status,totalPayment, paymentOpt, dateDelivered,dateToDeliver,date))
                index++
            }
            if(list.isEmpty()){
                emptyLabel.isVisible = true
            }

            recyclerView.adapter = OrdersAdapter(requireContext(),list)

        }catch (e: Exception){
            e.printStackTrace()
        }
    }
}