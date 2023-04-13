package com.example.ruterumahsakit

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import com.mapbox.geojson.Point
import com.mapbox.maps.*
import com.mapbox.maps.MapInitOptions.Companion.defaultPluginList
import com.mapbox.maps.MapInitOptions.Companion.getDefaultMapOptions
import com.mapbox.maps.MapInitOptions.Companion.getDefaultResourceOptions
import com.mapbox.maps.plugin.Plugin
import com.mapbox.maps.plugin.animation.camera
import com.mapbox.maps.plugin.animation.MapAnimationOptions.Companion.mapAnimationOptions
import com.mapbox.maps.plugin.animation.easeTo
import com.mapbox.maps.plugin.gestures.OnMapClickListener
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager

class MainActivity : AppCompatActivity(), OnMapClickListener {

    private lateinit var mapView: MapView
    private lateinit var buttons: Button
    private lateinit var mapBoxMap: MapboxMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // set initial camera position
        val initialCameraOptions = CameraOptions.Builder()
            .center(Point.fromLngLat(108.812442, -7.0641242))
            .pitch(45.0)
            .zoom(11.5)
            .bearing(-17.6)
            .build()

        mapView = findViewById(R.id.mapview)
        buttons = findViewById(R.id.btn)
        mapView = MapView(
            this,
            MapInitOptions(this, cameraOptions = initialCameraOptions, textureView = true)
        )

        mapBoxMap = mapView.getMapboxMap()

        mapBoxMap.loadStyleUri(Style.MAPBOX_STREETS)

        mapBoxMap.easeTo(
            CameraOptions.Builder().center(Point.fromLngLat(108.812442, -7.0641242))
                .pitch(45.0)
                .zoom(11.5)
                .bearing(-17.6)
                .build()
        )

        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS, object : Style.OnStyleLoaded {

            override fun onStyleLoaded(style: Style) {
                addAnnotationToMap(-6.8749143, 109.04974445)
                addAnnotationToMap(-6.8735408, 108.8764529)
                addAnnotationToMap(-7.2657805, 109.0122948)
                addAnnotationToMap(-6.86449356688, 109.017651568)
                addAnnotationToMap(-7.2626568, 109.0105722)
                addAnnotationToMap(-6.87141257873, 109.041250197)
                addAnnotationToMap(-7.26041, 109.005534)
                addAnnotationToMap(-6.96527932862, 108.984271237)

            }


        })

        buttons.setOnClickListener { startActivity(Intent(this, Testing2::class.java)) }


    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }


    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }


    private fun addAnnotationToMap(lat: Double, lng: Double) {
// Create an instance of the Annotation API and get the PointAnnotationManager.
        bitmapFromDrawableRes(
            this@MainActivity,
            R.drawable.baseline_location_on_24
        )?.let {
            val annotationApi = mapView?.annotations
            val pointAnnotationManager = annotationApi?.createPointAnnotationManager(mapView!!)
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

    override fun onMapClick(point: Point): Boolean {
        mapBoxMap.easeTo(
            CameraOptions.Builder().center(Point.fromLngLat(108.812442, -7.0641242))
                .pitch(45.0)
                .zoom(11.5)
                .bearing(-17.6)
                .build(),
            mapAnimationOptions {
                duration(12_000)
            }
        )

        return true
    }


}