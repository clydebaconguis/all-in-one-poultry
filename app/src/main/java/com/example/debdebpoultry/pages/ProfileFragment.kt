package com.example.debdebpoultry.pages

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.debdebpoultry.R
import com.example.debdebpoultry.config.ApiUrlRoutes
import com.example.debdebpoultry.config.SharedPref
import com.example.debdebpoultry.login.SignIn
import com.google.android.material.button.MaterialButton
import java.util.HashMap

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var signout : MaterialButton
    private lateinit var name : TextView
    private lateinit var phone : TextView
    private lateinit var address : TextView
    private lateinit var email : TextView
    private lateinit var spf : SharedPref

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
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        spf = SharedPref(requireContext())
        name =  view.findViewById(R.id.name)
        phone =  view.findViewById(R.id.phone)
        address =  view.findViewById(R.id.address)
        email =  view.findViewById(R.id.email)
        signout =  view.findViewById(R.id.signout)

        signout.setOnClickListener {
            logout()
        }

        name.text = spf.name.toString().replaceFirstChar{ it.uppercase() }
        phone.text = spf.phone
        address.text = spf.userAddress.toString().replaceFirstChar{ it.uppercase() }
        email.text = spf.email.toString().replaceFirstChar{ it.uppercase() }
    }

    private fun logout() {
        val url = ApiUrlRoutes().logout
        val stringRequest= object : StringRequest(
            Method.POST,url,
            Response.Listener{
                Toast.makeText(requireContext(), "Logged out!", Toast.LENGTH_SHORT).show()
                signOut()
            },
            Response.ErrorListener {
                Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT).show()
            }){
            override fun getParams(): MutableMap<String, String> {
                val params= HashMap<String,String>()
                params["user"] = spf.userID.toString()
                return params
            }
        }

        val queue = Volley.newRequestQueue(requireContext())
        queue.add(stringRequest)
    }

    private fun signOut(){
        spf.signOut()
        val intent = Intent(requireContext(), SignIn::class.java)
        startActivity(intent)
        this.requireActivity().finish()
    }
}