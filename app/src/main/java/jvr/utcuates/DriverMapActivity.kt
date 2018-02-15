package jvr.utcuates

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentActivity
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.directions.route.*
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class DriverMapActivity : FragmentActivity(), OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener, RoutingListener {




    //private FirebaseAuth firebaseAuth;
    //private firebaseAuth.AuthstateListener authstateListener;
    private var mMap: GoogleMap? = null
    internal lateinit var mGoogleApiClient: GoogleApiClient
    internal lateinit var mLastLocation: Location
    internal lateinit var mLocationRequest: LocationRequest

    private var mBuscar: Button? = null
    private var mLogout: Button? = null
    private val mapFragment: SupportMapFragment? = null
    private var isLoggingOut: Boolean? = false

    internal val LOCATION_REQUEST_CODE = 1


    private var polylines: MutableList<Polyline>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_map)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        polylines = ArrayList()
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this@DriverMapActivity, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE)
        } else {
            mapFragment.getMapAsync(this)
        }

        mBuscar = findViewById<View>(R.id.Buscar) as Button
        mBuscar!!.setOnClickListener {
            val UTM = LatLng(20.938848, -89.617366)
            getRouteToMarker(UTM)
        }


        mLogout = findViewById<View>(R.id.logout) as Button
        mLogout!!.setOnClickListener(View.OnClickListener {
            isLoggingOut = true

            disconnectDriver()
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this@DriverMapActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
            return@OnClickListener
        })
    }

    private fun getRouteToMarker(utm: LatLng) {
        val routing = Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(false)
                .waypoints(LatLng(mLastLocation.latitude, mLastLocation.longitude), utm)
                .build()
        routing.execute()
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this@DriverMapActivity, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE)
        }
        buildGoogleApiClient()
        mMap!!.isMyLocationEnabled = true
    }

    @Synchronized protected fun buildGoogleApiClient() {
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


            //mMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))  Cámara a Ubnicación
            //mMap!!.animateCamera(CameraUpdateFactory.zoomTo(11f))     Zoom frecuente y molesto


            //val Usuarios =  LatLng(location.latitude, location.longitude)
            //mMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))  Cámara a Ubnicación
            //mMap!!.animateCamera(CameraUpdateFactory.zoomTo(11f))     Zoom frecuente y molesto


            /*val userId2 = FirebaseAuth.getInstance().currentUser!!.uid
            val ref2 = FirebaseDatabase.getInstance().getReference("customerRequest")
            val geoFire2 = GeoFire(ref2)
            geoFire2.setLocation(userId2, GeoLocation(mLastLocation.latitude, mLastLocation.longitude))
            geoFire2.setLocation(userId2, GeoLocation(location.latitude, location.longitude))
            mMap!!.addMarker(MarkerOptions().position(Usuarios).title("Usuario"))
*/

            val userId = FirebaseAuth.getInstance().currentUser!!.uid
            val ref = FirebaseDatabase.getInstance().getReference("driversAvailable")

            val geoFire = GeoFire(ref)
            geoFire.setLocation(userId, GeoLocation(location.latitude, location.longitude))
        }

        val UTM = LatLng(20.938848, -89.617366)
        mMap!!.addMarker(MarkerOptions().position(UTM).title("UTM"))
    }

    override fun onConnected(bundle: Bundle?) {
        mLocationRequest = LocationRequest()
        mLocationRequest.interval = 1000
        mLocationRequest.fastestInterval = 1000
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this@DriverMapActivity, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE)
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this)
    }

    override fun onConnectionSuspended(i: Int) {}

    override fun onConnectionFailed(connectionResult: ConnectionResult) {}

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

    private fun disconnectDriver() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this)
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val ref = FirebaseDatabase.getInstance().getReference("driversAvailable")

        val geoFire = GeoFire(ref)
        geoFire.removeLocation(userId)
    }

    override fun onStop() {
        super.onStop()
        if ((!isLoggingOut!!)!!) {
            disconnectDriver()
        }
    }

    override fun onRoutingFailure(e: RouteException?) {
        if (e != null) {
            Toast.makeText(this, "Error: " + e.message, Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Algo salió mal, intente de nuevo", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRoutingStart() {

    }

    override fun onRoutingSuccess(route: ArrayList<Route>, shortestRouteIndex: Int) {
        if (polylines!!.size > 0) {
            for (poly in polylines!!) {
                poly.remove()
            }
        }

        polylines = ArrayList()
        //add route(s) to the map.
        for (i in route.indices) {

            //In case of more than 5 alternative routes
            val colorIndex = i % COLORS.size

            val polyOptions = PolylineOptions()
            polyOptions.color(resources.getColor(COLORS[colorIndex]))
            polyOptions.width((10 + i * 3).toFloat())
            polyOptions.addAll(route[i].points)
            val polyline = mMap!!.addPolyline(polyOptions)
            polylines!!.add(polyline)

            Toast.makeText(applicationContext, "Ruta " + (i + 1), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRoutingCancelled() {

    }

    private fun erasePolylines() {
        for (line in polylines!!) {
            line.remove()
        }
        polylines!!.clear()
    }

    companion object {
        private val COLORS = intArrayOf(R.color.primary_dark_material_light)
    }

}
