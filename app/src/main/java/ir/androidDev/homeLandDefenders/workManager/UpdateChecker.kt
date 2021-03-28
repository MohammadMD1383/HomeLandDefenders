package ir.androidDev.homeLandDefenders.workManager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import ir.androidDev.homeLandDefenders.HomePageActivity
import ir.androidDev.homeLandDefenders.R
import ir.androidDev.homeLandDefenders.connections.Url
import ir.androidDev.homeLandDefenders.contentManagement.ContentManager
import ir.androidDev.homeLandDefenders.database.Database
import org.json.JSONArray

class UpdateChecker(private val context: Context, workerParameters: WorkerParameters) : Worker(context, workerParameters) {
	
	companion object {
		private const val NOTIFICATION_CHANNEL_ID = "HLD-UPDATES"
		private const val NOTIFICATION_CHANNEL_NAME = "به روز رسانی"
		private const val NOTIFICATION_CHANNEL_DESCRIPTION = "اطلاعیه به روز رسانی‌های نرم افزار"
		
		private const val APP_UPDATE_NOTIFICATION_ID = 1
		private const val DATA_UPDATE_NOTIFICATION_ID = 2
	}
	
	/* volley request queue */
	private lateinit var queue: RequestQueue
	
	/* database */
	private lateinit var database: Database
	
	override fun doWork(): Result {
		/* create volley request queue */
		queue = Volley.newRequestQueue(context)
		
		/* create database object */
		database = Database(context)
		
		/* register the updates notification channel */
		registerNotificationChannel()
		
		/* check if allowed then check for application newer version availability */
		with(database.getRowBy(Database.TBL_SETTINGS, "name", "app_update")) {
			if (getInt(1) == 1) checkForAppUpdate()
			close()
		}
		
		/* check if allowed then check for new content availability */
		with(database.getRowBy(Database.TBL_SETTINGS, "name", "new_data")) {
			if (getInt(1) == 1) checkForNewData()
			close()
		}
		
		return Result.success()
	}
	
	/**
	 * checks for app update by comparing current and server app version
	 */
	private fun checkForAppUpdate() {
		val title = "به روز رسانی"
		val text = "نسخه جدید موجود است. برای دانلود کلیک کنید."
		val intent = Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(Url.DOWNLOAD) }
		
		val request = object : StringRequest(Method.GET, Url.APP_VERSION_CODE, {
			if (it.toInt() > ContentManager.APPLICATION_VERSION_CODE) {
				sendNotification(title, text, intent, APP_UPDATE_NOTIFICATION_ID)
			}
		}, null) {
			override fun getHeaders(): MutableMap<String, String> = ContentManager.getCookie(context)
		}
		
		/* set retry policy for request */
		request.retryPolicy = DefaultRetryPolicy(20000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
		
		/* add request to queue */
		queue.add(request)
	}
	
	/**
	 * checks for new data if available
	 */
	private fun checkForNewData() {
		val title = "اطلاعات جدید"
		val text = "اطلاعات جدید موجود است."
		val intent = Intent(context, HomePageActivity::class.java)
		
		val urls = arrayOf(Url.BIOGRAPHY, Url.TESTAMENT, Url.OPERATION)
		val requests = arrayOfNulls<StringRequest>(urls.size)
		
		fun sendAnotherRequest(i: Int) {
			if (i + 1 < requests.size) queue.add(requests[i + 1])
		}
		
		for (i in urls.indices) {
			val request = object : StringRequest(Method.POST, with(Url) { REPOSITORY_V1 + CHECK_NEW + urls[i] }, {
				if (it.toBoolean()) sendNotification(title, text, intent, DATA_UPDATE_NOTIFICATION_ID) else sendAnotherRequest(i)
			}, null) {
				override fun getParams(): MutableMap<String, String> {
					val map: MutableMap<String, String> = HashMap()
					
					when (urls[i]) {
						Url.BIOGRAPHY -> {
							map["ids"] = JSONArray(database.getArrayOfIds(Database.TBL_BIOGRAPHY)).toString(4)
						}
						Url.TESTAMENT -> {
							map["ids"] = JSONArray(database.getArrayOfIds(Database.TBL_TESTAMENT)).toString(4)
						}
						Url.OPERATION -> {
							map["ids"] = JSONArray(database.getArrayOfIds(Database.TBL_OPERATION)).toString(4)
						}
					}
					
					return map
				}
				
				@Throws(AuthFailureError::class)
				override fun getHeaders(): MutableMap<String, String> {
					val params: MutableMap<String, String> = HashMap()
					params["Content-Type"] = ContentManager.POST_HEADER
					params.putAll(ContentManager.getCookie(context))
					return params
				}
			}
			
			/* set retry policy */
			request.retryPolicy = DefaultRetryPolicy(20000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
			
			/* add to requests array */
			requests[i] = request
		}
		
		queue.add(requests[0])
	}
	
	/**
	 * sends a notification to user
	 *
	 * @param title the notification title
	 * @param text the notification text
	 * @param intent intent to the starting activity
	 * @param notificationId the notification ID
	 */
	private fun sendNotification(title: String, text: String, intent: Intent, notificationId: Int) {
		val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
			.setSmallIcon(R.mipmap.ic_launcher)
			.setContentTitle(title)
			.setContentText(text)
			.setPriority(NotificationCompat.PRIORITY_DEFAULT)
			.setContentIntent(PendingIntent.getActivity(context, 0, intent, 0))
			.setAutoCancel(true)
			.build()
		
		NotificationManagerCompat.from(context).notify(notificationId, notification)
	}
	
	/**
	 * registers a notification channel if the running device api is 26+
	 * (android 8 and higher)
	 */
	private fun registerNotificationChannel() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT).apply {
				description = NOTIFICATION_CHANNEL_DESCRIPTION
			}
			
			val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
			notificationManager.createNotificationChannel(channel)
		}
	}
}