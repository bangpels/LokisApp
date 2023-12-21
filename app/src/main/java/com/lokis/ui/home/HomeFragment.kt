@file:Suppress("DEPRECATION")

package com.lokis.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.lokis.R
import com.lokis.model.DataTravel
import com.lokis.ui.detail.DetailActivity
import java.util.Locale

class HomeFragment : Fragment() {
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var adapter: HomeAdapter
    private lateinit var  fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private var PERMISSION_ID = 100
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnSearch: ImageView = view.findViewById(R.id.btnsearch)
        val nameUser: TextView = view.findViewById(R.id.username)
        val btnPosition: LinearLayout = view.findViewById(R.id.btn_location)
        auth = FirebaseAuth.getInstance()

        val user = auth.currentUser
        nameUser.text = "Hello, "+ user?.displayName

        adapter = HomeAdapter()
        adapter.notifyDataSetChanged()

        adapter.setOnItemClickCallback(object : HomeAdapter.OnItemClickCallback{
            override fun onItemClicked(home: DataTravel) {
                val intent = Intent(requireContext(), DetailActivity::class.java).apply {
                    putExtra(DetailActivity.EXTRA_NAME, home.name)
                    putExtra(DetailActivity.EXTRA_DESCRIPTION, home.deskripsi)
                    putExtra(DetailActivity.EXTRA_URL, home.url)
                    putExtra(DetailActivity.EXTRA_RATING, home.rating)
                }
                startActivity(intent)
            }
        })

        btnSearch.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_navigation_search)
        }

        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        val rv_Rekomendasi = view.findViewById<RecyclerView>(R.id.rv_rekomendasi)
        rv_Rekomendasi.setHasFixedSize(true)
        rv_Rekomendasi.layoutManager = LinearLayoutManager(activity)
        rv_Rekomendasi.adapter = adapter
        homeViewModel.setLocationRekomendasi()
        homeViewModel.getLocationRekomendasi().observe(viewLifecycleOwner) {
            if (it != null) {
                adapter.setList(it)
            }
        }



        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        getLastLocation()

        btnPosition.setOnClickListener {
            getLastLocation()
        }
    }

    private fun checkPermission():Boolean{
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ){
            return true
        }

        return false
    }

    private fun requestPermission(){
        ActivityCompat.requestPermissions(
            requireActivity(), arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ), PERMISSION_ID
        )
    }

    private fun isLocationEnabled():Boolean{
        val locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ID){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.d("Debug:", "Permission Done ")
            }
        }
    }

    private fun getLastLocation(){
        val locationtxt: TextView = requireView().findViewById(R.id.location)

        if (checkPermission()){
            if (isLocationEnabled()){
                fusedLocationProviderClient.lastLocation.addOnCompleteListener { task->
                    val location = task.result
                    if (location == null){
                        getNewLocation()

                    } else {
                        locationtxt.text = getCityName(location.latitude, location.longitude)
                        savedStateRegistry
                    }
                }

            } else {
                Toast.makeText(
                    requireContext(),
                    "Please Enable your Location Service",
                    Toast.LENGTH_SHORT
                ).show()
            }

        } else {
            requestPermission()
        }
    }

    private fun getNewLocation(){
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 2
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest, locationCallback, Looper.myLooper()
        )
    }

    private val locationCallback = object : LocationCallback(){
        override fun onLocationResult(p0: LocationResult) {
            val locationtxt: TextView = requireView().findViewById(R.id.location)
            val lastLocation = p0.lastLocation
            locationtxt.text = lastLocation?.let { getCityName(it.latitude, lastLocation.longitude) }
        }
    }

    private fun getCityName(lat: Double, long: Double): String{
        val cityName: String
        val geoCoder = Geocoder(requireContext(), Locale.getDefault())
        val address = geoCoder.getFromLocation(lat, long, 1)

        cityName = address?.get(0)!!.locality
        return cityName

    }

}