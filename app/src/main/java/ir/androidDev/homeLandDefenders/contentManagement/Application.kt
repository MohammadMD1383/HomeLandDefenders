package ir.androidDev.homeLandDefenders.contentManagement

import android.app.Application
import androidx.work.*
import com.squareup.picasso.Picasso
import ir.androidDev.homeLandDefenders.workManager.UpdateChecker
import java.util.concurrent.TimeUnit

open class WorkRegisterer : Application() {
	
	companion object {
		const val APP_UPDATE_CHECKER = "ApplicationUpdateChecker"
	}
	
	override fun onCreate() {
		super.onCreate()
		
		val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
		val dailyJob = PeriodicWorkRequestBuilder<UpdateChecker>(1, TimeUnit.DAYS).setConstraints(constraints).build()
		WorkManager.getInstance(this).enqueueUniquePeriodicWork(APP_UPDATE_CHECKER, ExistingPeriodicWorkPolicy.KEEP, dailyJob)
	}
}

class PicassoHandler : WorkRegisterer() {
	override fun onCreate() {
		super.onCreate()
		
		val builder = Picasso.Builder(this)
		
		val built = builder.build()

//		built.setIndicatorsEnabled(true)
//		built.isLoggingEnabled = true
		
		Picasso.setSingletonInstance(built)
	}
}
