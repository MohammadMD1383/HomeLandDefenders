package ir.androidDev.homeLandDefenders.contentManagement

import android.content.Context
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import ir.androidDev.homeLandDefenders.R
import ir.androidDev.homeLandDefenders.connections.Url
import ir.androidDev.homeLandDefenders.database.Database
import org.json.JSONArray

class NewContent(private val context: Context) : ContentManager(context) {
	override fun download(onNoInternetListener: () -> Unit, onFinishedListener: () -> Unit) {
		/* check network connection */
		val d = util.makeProgressDialog(context.getString(R.string.download_volley_string_check_connection), false)
		if (!isConnected(context)) {
			d.dismiss()
			onNoInternetListener()
			return
		}
		
		/* start progress of download */
		d.setMessage(context.getString(R.string.download_volley_string_downloading))
		
		/* create request queue */
		val queue = Volley.newRequestQueue(context)
		
		/* create the array of contents to download */
		val cnt = arrayOf(Url.BIOGRAPHY, Url.TESTAMENT, Url.OPERATION)
		
		/* create download counter */
		var dCounter = cnt.size
		
		/* creating requests */
		for (i in cnt.indices) {
			val request = object : StringRequest(Method.POST, Url.let { it.REPOSITORY_V1 + it.NEW + cnt[i] }, {
				if (it.trim().isNotEmpty()) {
					/* create json from response */
					val json = JSONArray(it)
					
					/* save data */
					when (cnt[i]) {
						Url.BIOGRAPHY -> saveToBiography(json) {
							if (--dCounter == 0) {
								d.dismiss()
								onFinishedListener()
							}
						}
						Url.TESTAMENT -> saveToTestament(json) {
							if (--dCounter == 0) {
								d.dismiss()
								onFinishedListener()
							}
						}
						Url.OPERATION -> saveToOperation(json) {
							if (--dCounter == 0) {
								d.dismiss()
								onFinishedListener()
							}
						}
					}
				} else {
					if (--dCounter == 0) {
						d.dismiss()
						onFinishedListener()
					}
				}
			}, {
				if (--dCounter == 0) onFinishedListener()
				Toast.makeText(context, context.getString(R.string.json_download_volley_fail_string), Toast.LENGTH_LONG).show()
			}) {
				override fun getParams(): MutableMap<String, String> {
					val map: MutableMap<String, String> = HashMap()
					
					when (cnt[i]) {
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
					params["Content-Type"] = "application/x-www-form-urlencoded"
					params.putAll(getCookie(context))
					return params
				}
			}
			
			/* setting retry policy */
			request.retryPolicy = DefaultRetryPolicy(20000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
			
			/* adding request to queue */
			queue.add(request)
		}
	}
}