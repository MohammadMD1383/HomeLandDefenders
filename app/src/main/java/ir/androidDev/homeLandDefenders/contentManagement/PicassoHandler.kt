package ir.androidDev.homeLandDefenders.contentManagement

import android.app.Application
import com.squareup.picasso.Picasso

class PicassoHandler : Application() {
	override fun onCreate() {
		super.onCreate()
		
		val builder = Picasso.Builder(this)
		
		val built = builder.build()

//		built.setIndicatorsEnabled(true)
//		built.isLoggingEnabled = true
		
		Picasso.setSingletonInstance(built)
	}
}