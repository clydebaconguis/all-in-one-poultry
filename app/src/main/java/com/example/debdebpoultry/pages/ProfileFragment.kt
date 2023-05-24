package com.example.debdebpoultry.pages

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.debdebpoultry.R
import com.example.debdebpoultry.config.SharedPref
import com.example.debdebpoultry.login.SignIn
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONObject
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
    private lateinit var editPassword : TextInputEditText
    private lateinit var editOldPassword : TextInputEditText
    private lateinit var btnEditPassword : TextView
    private lateinit var btnSave : TextView
    private lateinit var spf : SharedPref
    private var enabledEditPass  = false

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
        editPassword = view.findViewById(R.id.editPassword)
        editOldPassword = view.findViewById(R.id.editOldPass)
        btnEditPassword = view.findViewById(R.id.btnEditPassword)
        btnSave = view.findViewById(R.id.btnSave)
        btnEditPassword.setOnClickListener {
            btnSave.isVisible = true
            btnEditPassword.isVisible = false
            editPassword.isVisible = true
            editOldPassword.isVisible = true
        }
        btnSave.setOnClickListener {
            if (editPassword.text.toString().length < 8){
                Toast.makeText(requireContext(), "Password must not be lesser than 8 char", Toast.LENGTH_SHORT).show()
            }
            if (editPassword.text.toString().isEmpty() || editOldPassword.text.toString().isEmpty()){
                Toast.makeText(requireContext(), "fill all fields", Toast.LENGTH_SHORT).show()
            }
            if(editPassword.text.toString().isNotEmpty() && editOldPassword.text.toString().isNotEmpty() && editPassword.text.toString().length >= 8){
                changedPass()
            }
        }

        signout.setOnClickListener {
            logout()
        }

        name.text = spf.name.toString().replaceFirstChar{ it.uppercase() }
        phone.text = spf.phone
        address.text = spf.userAddress.toString().replaceFirstChar{ it.uppercase() }
        email.text = spf.email.toString().replaceFirstChar{ it.uppercase() }
    }
    private fun changedPass(){
        val url = "https://larapoultry.herokuapp.com/api/chpass/${spf.userID}"
        val stringRequest= object : StringRequest(
            Method.POST,url,
            Response.Listener{
                val jo = JSONObject(it)
                if (jo.has("message")){
                    val msg = jo.getString("message")
                    if (msg == "success"){
                        editPassword.isVisible = false
                        editOldPassword.isVisible = false
                        btnSave.isVisible = false
                        btnEditPassword.isVisible = true
                        editPassword.setText("")
                        Toast.makeText(requireContext(), "Password Updated Successfully", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(requireContext(), "Password Failed to update", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            Response.ErrorListener {
                Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT).show()
            }){
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Accept"] = "application/json"
                headers["Content-Type"] = "application/x-www-form-urlencoded"
                return headers
            }
            override fun getParams(): MutableMap<String, String> {
                val params= HashMap<String,String>()
                params["password"] = editPassword.text.toString()
                params["old_password"] = editOldPassword.text.toString()
                return params
            }
        }
        val queue = Volley.newRequestQueue(requireContext())
        queue.add(stringRequest)
    }

    private fun logout() {
        try {
            val url = "https://larapoultry.herokuapp.com/api/logout/${spf.userID}"
            val stringRequest= object : StringRequest(
                Method.GET,url,
                Response.Listener{
                    val jo = JSONObject(it)
                    if (jo.has("message")){
                        val msg = jo.getString("message")
                        if (msg == "success"){
                            Toast.makeText(requireContext(), "Logged out", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                Response.ErrorListener {
                    Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT).show()
                }){
            }
            val queue = Volley.newRequestQueue(requireContext())
            queue.add(stringRequest)
            signOut()
        }catch (e:Exception){
            Toast.makeText(requireContext(), e.toString(), Toast.LENGTH_SHORT).show()
        }

    }

    private fun signOut(){
        try {
            spf.signOut()
            val intent = Intent(this.requireContext(), SignIn::class.java)
            startActivity(intent)
            activity?.finish()
        }catch (e:Exception){
            Toast.makeText(requireContext(), e.toString(), Toast.LENGTH_SHORT).show()
        }
    }
}