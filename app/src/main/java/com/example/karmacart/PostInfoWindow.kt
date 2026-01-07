package com.example.karmacart

import android.content.Intent
import android.view.View
import android.widget.TextView
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.InfoWindow

class PostInfoWindow(
    mapView: MapView,
    private val postId: Int,
    private val context: MapActivity
) : InfoWindow(R.layout.map_info_window, mapView) {

    override fun onOpen(item: Any?) {
        val marker = item as Marker

        val title = mView.findViewById<TextView>(R.id.tvTitle)
        val desc = mView.findViewById<TextView>(R.id.tvDesc)

        title.text = marker.title
        desc.text = marker.subDescription

        mView.setOnClickListener {
            context.startActivity(
                Intent(context, PostDetailsActivity::class.java).apply {
                    putExtra(MapActivity.EXTRA_POST_ID, postId)
                }
            )
        }
    }

    override fun onClose() {}
}
