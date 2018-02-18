package jvr.utcuates

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.Toast

import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CustomerMapActivity : AppCompatActivity(), OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private var mMap: GoogleMap? = null
    internal lateinit var mGoogleApiClient: GoogleApiClient
    internal lateinit var mLastLocation: Location
    internal lateinit var mLocationRequest: LocationRequest

    private var mLogout: Button? = null
    private var mRequest: Button? = null

    private val mapFragment: SupportMapFragment? = null
    private var isLoggingOut: Boolean? = false
    private var pickupLocation: LatLng? = null


    internal val LOCATION_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_costumer_map)
        showToolbar("College Drivers", true)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this@CustomerMapActivity, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE)
        } else {
            mapFragment.getMapAsync(this)
        }

        mLogout = findViewById(R.id.logout) as Button
        mRequest = findViewById(R.id.request) as Button
        mLogout!!.setOnClickListener(View.OnClickListener {
            isLoggingOut = true

            disconnectDriver()

            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this@CustomerMapActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
            return@OnClickListener
        })

        mRequest!!.setOnClickListener {
            val userId = FirebaseAuth.getInstance().currentUser!!.uid

            val ref = FirebaseDatabase.getInstance().getReference("customerRequest")
            val geoFire = GeoFire(ref)
            geoFire.setLocation(userId, GeoLocation(mLastLocation.latitude, mLastLocation.longitude))

            pickupLocation = LatLng(mLastLocation.latitude, mLastLocation.longitude)
            mMap!!.addMarker(MarkerOptions().position(pickupLocation!!).title("Alumno Aquí"))

            mRequest!!.text = "Buscando conductor...."
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this@CustomerMapActivity, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE)
        }
        buildGoogleApiClient()
        mMap!!.isMyLocationEnabled = true
    }

    @Synchronized
    protected fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()
        mGoogleApiClient.connect()
    }

    override fun onLocationChanged(location: Location) {
        if (applicationContext != null) {
            mLastLocation = location

            val latLng = LatLng(location.latitude, location.longitude)

            mMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))
            mMap!!.animateCamera(CameraUpdateFactory.zoomTo(11f))
        }
    }

    override fun onConnected(bundle: Bundle?) {
        mLocationRequest = LocationRequest()
        mLocationRequest.interval = 1000
        mLocationRequest.fastestInterval = 1000
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this@CustomerMapActivity, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE)
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this)
    }

    override fun onConnectionSuspended(i: Int) {}

    override fun onConnectionFailed(connectionResult: ConnectionResult) {}

    private fun disconnectDriver() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this)
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val ref = FirebaseDatabase.getInstance().getReference("driversAvailable")

        val geoFire = GeoFire(ref)
        geoFire.removeLocation(userId)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_REQUEST_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    mapFragment!!.getMapAsync(this)
                } else {
                    Toast.makeText(applicationContext, "Por favor, concede los permisos", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onStop() {

        super.onStop()
        if ((!isLoggingOut!!)) {
            disconnectDriver()
        }
    }

    fun showToolbar(title: String, upButton: Boolean) {
        val toolbar = findViewById(R.id.toolbar) as android.support.v7.widget.Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle(title)
        supportActionBar!!.setDisplayHomeAsUpEnabled(upButton)
    }
}
