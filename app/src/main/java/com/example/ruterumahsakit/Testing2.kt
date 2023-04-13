package com.example.ruterumahsakit

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.plugin.animation.MapAnimationOptions.Companion.mapAnimationOptions
import com.mapbox.maps.plugin.animation.easeTo
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.gestures.OnMapClickListener

class Testing2 : AppCompatActivity(), OnMapClickListener {

    private lateinit var mapboxMap: MapboxMap
    private var isAtStart = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mapView = MapView(this)
        setContentView(mapView)
        mapboxMap = mapView.getMapboxMap()
        mapboxMap.loadStyleUri(Style.MAPBOX_STREETS, object : Style.OnStyleLoaded {
            override fun onStyleLoaded(style: Style) {
                addAnnotationToMap(-7.0641242, 108.812442, mapView)
            }

        })
        val target = if (isAtStart) CAMERA_END else CAMERA_START

        try {
            mapboxMap.easeTo(
                target,
                mapAnimationOptions {
                    duration(3_000)
                }
            )
        } finally {
            Handler().postDelayed(Runnable {
                Toast.makeText(this, "Lokasi Rumah Sakit", Toast.LENGTH_SHORT).show()
            }, 3000)
        }
    }

    override fun onMapClick(point: Point): Boolean {
        val target = if (isAtStart) CAMERA_END else CAMERA_START
        isAtStart = !isAtStart
        mapboxMap.easeTo(
            target,
            mapAnimationOptions {
                duration(12_000)
            }
        )
        return true
    }

    private companion object {
        private val CAMERA_START = cameraOptions {
            center(Point.fromLngLat(80.0, 36.0))
            zoom(1.0)
            pitch(0.0)
            bearing(0.0)
        }
        private val CAMERA_END = cameraOptions {
            center(Point.fromLngLat(108.812442, -7.0641242))
            zoom(12.5)
            pitch(75.0)
            bearing(130.0)
        }

    }

    private fun addAnnotationToMap(lat: Double, lng: Double, mapview: MapView) {
// Create an instance of the Annotation API and get the PointAnnotationManager.
        bitmapFromDrawableRes(
            this@Testing2,
            R.drawable.baseline_location_on_24
        )?.let {
            val annotationApi = mapview.annotations
            val pointAnnotationManager = annotationApi.createPointAnnotationManager(mapview)
// Set options for the resulting symbol layer.
            val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
// Define a geographic coordinate.
                .withPoint(Point.fromLngLat(lng, lat))
// Specify the bitmap you assigned to the point annotation
// The bitmap will be added to map style automatically.
                .withIconImage(it)
// Add the resulting pointAnnotation to the map.
            pointAnnotationManager?.create(pointAnnotationOptions)
        }
    }

    private fun bitmapFromDrawableRes(context: Context, @DrawableRes resourceId: Int) =
        convertDrawableToBitmap(AppCompatResources.getDrawable(context, resourceId))

    private fun convertDrawableToBitmap(sourceDrawable: Drawable?): Bitmap? {
        if (sourceDrawable == null) {
            return null
        }
        return if (sourceDrawable is BitmapDrawable) {
            sourceDrawable.bitmap
        } else {
// copying drawable object to not manipulate on the same reference
            val constantState = sourceDrawable.constantState ?: return null
            val drawable = constantState.newDrawable().mutate()
            val bitmap: Bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth, drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }
    }


}