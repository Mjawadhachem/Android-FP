package com.example.karmacart

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.karmacart.data.database.AppDatabaseSingleton
import com.example.karmacart.data.repository.PostRepository
import com.example.karmacart.viewmodel.PostViewModel
import com.example.karmacart.viewmodel.PostViewModelFactory
import com.google.android.material.button.MaterialButton
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class MapActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_SELECT_MODE = "select_mode"
        const val EXTRA_POST_ID = "post_id"
        private const val LOCATION_REQ = 1001
    }

    private lateinit var map: MapView
    private var myLocationOverlay: MyLocationNewOverlay? = null

    private val isSelectMode: Boolean
        get() = intent?.getBooleanExtra(EXTRA_SELECT_MODE, false) == true

    private val vm: PostViewModel by viewModels {
        val db = AppDatabaseSingleton.getDatabase(this)
        PostViewModelFactory(PostRepository(db.postDao()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // osmdroid configuration
        val config = Configuration.getInstance()
        config.userAgentValue = packageName
        config.osmdroidBasePath = filesDir
        config.osmdroidTileCache = filesDir

        setContentView(R.layout.activity_map)

        map = findViewById(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
        map.controller.setZoom(14.0)
        map.controller.setCenter(GeoPoint(33.8938, 35.5018))

        val btnConfirm = findViewById<MaterialButton>(R.id.btnConfirmLocation)
        val centerMarker = findViewById<ImageView>(R.id.centerMarker)

        if (isSelectMode) {
            // =========================
            // 📍 SELECT MODE (FORCED)
            // =========================
            map.overlays.clear()
            myLocationOverlay = null

            centerMarker.isVisible = true
            btnConfirm.isVisible = true

            btnConfirm.setOnClickListener {
                val center = map.mapCenter as GeoPoint
                setResult(
                    RESULT_OK,
                    Intent().apply {
                        putExtra("latitude", center.latitude)
                        putExtra("longitude", center.longitude)
                    }
                )
                finish()
            }

        } else {
            // =========================
            // 🗺️ VIEW MODE (FORCED)
            // =========================
            centerMarker.isVisible = false
            btnConfirm.isVisible = false

            ensureLocationPermission()

            vm.posts.observe(this) { posts ->
                map.overlays.clear()
                myLocationOverlay?.let { map.overlays.add(it) }

                posts
                    .filter { it.latitude != 0.0 && it.longitude != 0.0 }
                    .forEach { post ->
                        val marker = Marker(map)
                        marker.position = GeoPoint(post.latitude, post.longitude)
                        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

                        marker.title = post.title
                        marker.subDescription =
                            "${post.type}\n${post.description.take(80)}..."

                        marker.icon = if (post.type.equals("REQUEST", true)) {
                            ContextCompat.getDrawable(this, R.drawable.ic_marker_request)
                        } else {
                            ContextCompat.getDrawable(this, R.drawable.ic_marker_offer)
                        }

                        marker.infoWindow = PostInfoWindow(map, post.id, this)

                        marker.setOnMarkerClickListener { m, _ ->
                            m.showInfoWindow()
                            true
                        }

                        map.overlays.add(marker)
                    }

                map.invalidate()
            }
        }

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    finish()
                }
            }
        )
    }

    // =========================
    // 📍 LOCATION
    // =========================
    private fun ensureLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_REQ
            )
        } else {
            enableUserLocation()
        }
    }

    private fun enableUserLocation() {
        myLocationOverlay = MyLocationNewOverlay(
            GpsMyLocationProvider(this),
            map
        )
        myLocationOverlay?.enableMyLocation()
        myLocationOverlay?.enableFollowLocation()
        map.overlays.add(myLocationOverlay)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_REQ &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            enableUserLocation()
        }
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
        myLocationOverlay?.disableMyLocation()
    }
}
