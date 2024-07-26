package com.example.doctor_appointment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.f1soft.esewapaymentsdk.EsewaConfiguration
import com.f1soft.esewapaymentsdk.EsewaPayment
import com.f1soft.esewapaymentsdk.ui.screens.EsewaPaymentActivity
import org.json.JSONException
import org.json.JSONObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EsewaPayment : AppCompatActivity() {

    private lateinit var requestQueue: RequestQueue
    private var doctorID: String? = null
    private var selectedDate: String? = null
    private var selectedTimeSlot: String? = null
    private var reason: String? = null
    private var b_id: String? = null
    private var status: String? = null
    private var fee: String? = null

    private lateinit var date: TextView
    private lateinit var time: TextView
    private lateinit var reasons: TextView
    private lateinit var cash: TextView
    private lateinit var online: TextView
    private lateinit var doctorImage: ImageView
    private lateinit var doctorName: TextView
    private lateinit var doctorSpecialty: TextView
    private lateinit var nmcNo: TextView
    private lateinit var stat: TextView
    private lateinit var paymentDetail: TextView
    private lateinit var paymentBtn: Button
    private lateinit var esewa: Button

    private lateinit var eSewaPayment: EsewaPayment

    private val eSewaConfiguration = EsewaConfiguration(
            clientId = "JB0BBQ4aD0UqIThFJwAKBgAXEUkEGQUBBAwdOgABHD4DChwUAB0R",
            secretKey = "BhwIWQQADhIYSxILExMcAgFXFhcOBwAKBgAXEQ==",
            environment = EsewaConfiguration.ENVIRONMENT_TEST
    )

    private lateinit var registerActivity: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment) // Replace with your actual layout

        requestQueue = Volley.newRequestQueue(this)

        // Retrieve data from the Intent
        doctorID = intent.getStringExtra("doctorID")
        selectedDate = intent.getStringExtra("selectedDate")
        selectedTimeSlot = intent.getStringExtra("selectedTimeSlot")
        reason = intent.getStringExtra("reason")
        b_id = intent.getStringExtra("b_id")
        status = intent.getStringExtra("status")
        fee = intent.getStringExtra("fee")

        Log.d("status", status.toString())

        doctorImage = findViewById(R.id.doctor_image)
        doctorName = findViewById(R.id.doctor_name)
        doctorSpecialty = findViewById(R.id.doctor_specialty)
        nmcNo = findViewById(R.id.doctor_nmc)
        date = findViewById(R.id.appointment_date)
        time = findViewById(R.id.appointment_time)
        reasons = findViewById(R.id.appointment_reason)
        stat = findViewById(R.id.status)
        paymentDetail = findViewById(R.id.paymentdetail)
        esewa = findViewById(R.id.esewabtn)

        date.text = selectedDate
        time.text = "|| $selectedTimeSlot"
        reasons.text = reason

        // Parsing selectedDate to Date object
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        var appointmentDate: Date? = null
        try {
            appointmentDate = dateFormat.parse(selectedDate!!)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        // Handle visibility and text based on status and date
        when {
            status == "1" && appointmentDate != null && appointmentDate.before(getCurrentDate()) -> {
                stat.text = "accepted"
                esewa.visibility = View.GONE
            }
            status == "1" && appointmentDate != null && appointmentDate.after(getCurrentDate()) -> {
                stat.text = "accepted"
                esewa.visibility = View.VISIBLE
            }
            status == "2" && appointmentDate != null && appointmentDate.after(getCurrentDate()) -> {
                stat.text = "accepted"
                paymentDetail.text = "payment complete"
                esewa.visibility = View.GONE
            }
            status == "2" && appointmentDate != null && appointmentDate.before(getCurrentDate()) -> {
                stat.text = "accepted"
                paymentDetail.text = "payment complete"
                esewa.visibility = View.GONE
            }
            status == "0" -> {
                stat.text = "Not accepted"
                esewa.visibility = View.GONE
            }
        }

        // Initialize Activity Result Launcher
        registerActivity = registerForActivityResult(
                ActivityResultContracts.StartActivityForResult(),
                ::onResultCallback
        )

        // Initialize EsewaPayment with fee and b_id
        esewa.setOnClickListener {
            Log.d("eSewaPayment", "eSewa button clicked")

            // Ensure b_id and fee are not null before using them
            if (b_id != null && fee != null) {
                eSewaPayment = EsewaPayment(
                        fee!!, // Amount
                        "Product Description", // Replace with actual product description
                        b_id!! // Product ID

                )
                Log.d("fee", fee!!);

                registerActivity.launch(
                        Intent(this, EsewaPaymentActivity::class.java).apply {
                            putExtra(EsewaConfiguration.ESEWA_CONFIGURATION, eSewaConfiguration)
                            putExtra(EsewaPayment.ESEWA_PAYMENT, eSewaPayment)
                        }
                )
            } else {
                Toast.makeText(this, "Payment details are not available", Toast.LENGTH_SHORT).show()
            }
        }

        // Fetch doctor info
        doctorID?.let { getDoctorInfo(it) }
    }

    private fun getCurrentDate(): Date {
        return Date()
    }

    private fun getDoctorInfo(doctorId: String) {
        val url = Endpoints.select_doctor_info

        val stringRequest = object : StringRequest(Request.Method.POST, url,
                Response.Listener<String> { response ->
                    try {
                        val `object` = JSONObject(response)
                        val result = `object`.getString("result")
                        if (result == "success") {
                            val name = `object`.getString("name")
                            val specialist = `object`.getString("specialist")
                            val nmc = `object`.getString("nmc_no")
                            val imageUrl = `object`.getString("image")

                            Glide.with(this).load(imageUrl).circleCrop().into(doctorImage)
                            doctorName.text = name
                            doctorSpecialty.text = specialist
                            nmcNo.text = nmc
                        } else {
                            Log.e("Error", "Doctor ID is null or empty")
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    Log.e("Volley Error", error.toString())
                }) {
            override fun getParams(): MutableMap<String, String> {
                val data = HashMap<String, String>()
                data["d_id"] = doctorId
                return data
            }
        }

        requestQueue.add(stringRequest)
    }

    private fun onResultCallback(result: ActivityResult) {
        Log.d("TAGz", "result ${result.resultCode}")
        result.let {
            when (it.resultCode) {
                AppCompatActivity.RESULT_OK -> {
                    it.data?.getStringExtra(EsewaPayment.EXTRA_RESULT_MESSAGE)?.let { s ->
                        Log.i("Proof of Payment", s)
                    }

                    // Insert fee information
                    if (b_id != null && fee != null) {
                        insertFee(b_id!!, fee!!)
                    }

                    Toast.makeText(this, "SUCCESSFUL PAYMENT", Toast.LENGTH_SHORT).show()
                }
                AppCompatActivity.RESULT_CANCELED -> {
                    Toast.makeText(this, "Canceled By User", Toast.LENGTH_SHORT).show()
                }
                EsewaPayment.RESULT_EXTRAS_INVALID -> {
                    it.data?.getStringExtra(EsewaPayment.EXTRA_RESULT_MESSAGE)?.let { s ->
                        Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
                    }
                }
                else -> {}
            }
        }
    }

    private fun insertFee(b_id: String, fee: String) {
        val url = Endpoints.get_insert_paymenr // Replace with actual URL

        val stringRequest = object : StringRequest(Request.Method.POST, url,
                Response.Listener<String> { response ->
                    try {
                        val `object` = JSONObject(response)
                        val result = `object`.getString("result")
                        if (result != "success") {
                            Log.e("Error", "Failed to update fee")
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    Log.e("Volley Error", error.toString())
                }) {
            override fun getParams(): MutableMap<String, String> {
                val data = HashMap<String, String>()
                data["b_id"] = b_id
                data["fee"] = fee
                return data
            }
        }

        requestQueue.add(stringRequest)
    }
}
