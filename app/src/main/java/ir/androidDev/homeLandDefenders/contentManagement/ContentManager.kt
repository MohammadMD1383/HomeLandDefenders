package ir.androidDev.homeLandDefenders.contentManagement

import android.content.ContentValues
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Handler
import android.os.Looper
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.core.database.getStringOrNull
import com.android.volley.DefaultRetryPolicy
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import ir.androidDev.homeLandDefenders.R
import ir.androidDev.homeLandDefenders.connections.Url
import ir.androidDev.homeLandDefenders.database.Database
import ir.androidDev.homeLandDefenders.utility.Util
import org.json.JSONArray

open class ContentManager(private val context: Context) {
	
	/* cookie hash map */
	companion object {
		/* the cookie needed to connect to server */
		private var mCookie: String? = null
		
		fun getCookie(context: Context): MutableMap<String, String> {
			if (mCookie == null) {
				Handler(Looper.getMainLooper()).post {
					val mWebView = WebView(context)
					mWebView.settings.javaScriptEnabled = true
					mWebView.webViewClient = object : WebViewClient() {
						override fun onPageFinished(view: WebView?, url: String?) {
							mCookie = CookieManager.getInstance().getCookie(Url.COOKIE_SERVER)
							mWebView.destroy()
						}
					}
					mWebView.loadUrl(Url.COOKIE_SERVER)
				}
			}
			while (mCookie == null) continue
			return hashMapOf(Pair("Cookie", mCookie!!))
		}
		
		
		/**
		 * checks network availability for download
		 */
		fun isConnected(context: Context): Boolean {
			val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
			val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
			return activeNetwork?.isConnectedOrConnecting == true
		}
	}
	
	/* database */
	protected val database = Database(context)
	
	/* Util object */
	protected val util = Util(context)
	
	/* missing parts */
	private val missingDataList: MutableList<String> = ArrayList()
	
	/**
	 * checks the contents in database if there is no record then it will be downloaded
	 */
	fun checkContents(onCheckFinished: (cm: ContentManager) -> Unit) {
		/* get nick name from user */
		if (database.getRowBy(Database.TBL_SETTINGS, "name", "nick_name").getStringOrNull(1) == null) {
			util.makePromptDialog(
				context.getString(R.string.app_settings_string_nick_name),
				context.getString(R.string.app_settings_string_please_choose_nick_name),
				false,
				30
			) {
				val cv = ContentValues()
				cv.put("value", it)
				database.updateBy(Database.TBL_SETTINGS, cv, "name", "nick_name")
				
				if (database.getItemsCount(Database.TBL_BIOGRAPHY) == 0) missingDataList.add(Url.BIOGRAPHY)
				if (database.getItemsCount(Database.TBL_TESTAMENT) == 0) missingDataList.add(Url.TESTAMENT)
				if (database.getItemsCount(Database.TBL_OPERATION) == 0) missingDataList.add(Url.OPERATION)
				
				onCheckFinished(this)
			}
		} else {
			if (database.getItemsCount(Database.TBL_BIOGRAPHY) == 0) missingDataList.add(Url.BIOGRAPHY)
			if (database.getItemsCount(Database.TBL_TESTAMENT) == 0) missingDataList.add(Url.TESTAMENT)
			if (database.getItemsCount(Database.TBL_OPERATION) == 0) missingDataList.add(Url.OPERATION)
			
			onCheckFinished(this)
		}
	}
	
	/**
	 * downloads missed data
	 *
	 * @param onNoInternetListener when there is no internet connection
	 * @param onFinishedListener on finished downloading and saving to database or not needed to download
	 */
	open fun download(onNoInternetListener: () -> Unit, onFinishedListener: () -> Unit) {
		/* check missed data */
		if (missingDataList.size == 0) {
			onFinishedListener()
			return
		}
		
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
		
		/* create counter for number of downloads */
		var dCounter = missingDataList.size
		
		/* create requests and send them */
		for (i in missingDataList.indices) {
			val request = object : JsonArrayRequest(
				Url.REPOSITORY_V1 + Url.GET + missingDataList[i],
				{
					when (missingDataList[i]) {
						Url.BIOGRAPHY -> saveToBiography(it) {
							if (--dCounter == 0) d.dismiss(); onFinishedListener()
						}
						Url.TESTAMENT -> saveToTestament(it) {
							if (--dCounter == 0) d.dismiss(); onFinishedListener()
						}
						Url.OPERATION -> saveToOperation(it) {
							if (--dCounter == 0) d.dismiss(); onFinishedListener()
						}
					}
				},
				{
					if (--dCounter == 0) d.dismiss(); onFinishedListener()
					Toast.makeText(context, context.getString(R.string.json_download_volley_fail_string), Toast.LENGTH_LONG).show()
				}
			) {
				override fun getHeaders(): MutableMap<String, String> {
					return getCookie(context)
				}
			}
			
			/* setting retry policy */
			request.retryPolicy =
				DefaultRetryPolicy(20000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
			
			/* adding to queue and send */
			queue.add(request)
		}
	}
	
	/**
	 * saves biography downloaded data to database
	 *
	 * @param jsonArray the json downloaded by download method
	 * @param onSaveFinished on finished saving contents
	 */
	protected fun saveToBiography(jsonArray: JSONArray, onSaveFinished: () -> Unit) {
		for (i in 0 until jsonArray.length()) {
			val jsonObject = jsonArray.getJSONObject(i)
			val cv = ContentValues()
			
			cv.put("id", jsonObject.getInt("id"))
			cv.put("name", jsonObject.getString("name"))
			cv.put("birth", jsonObject.getString("birth"))
			cv.put("death", jsonObject.getString("death"))
			cv.put("rank", jsonObject.getString("rank"))
			cv.put("age", jsonObject.getString("age"))
			cv.put("img_url", jsonObject.getString("img_url"))
			cv.put("inner_img_url", jsonObject.getString("inner_img_url"))
			cv.put("text", jsonObject.getString("text"))
			
			database.insert(Database.TBL_BIOGRAPHY, cv)
		}
		onSaveFinished()
	}
	
	/**
	 * saves testament downloaded data to database
	 *
	 * @param jsonArray the json downloaded by download method
	 * @param onSaveFinished on finished saving contents
	 */
	protected fun saveToTestament(jsonArray: JSONArray, onSaveFinished: () -> Unit) {
		for (i in 0 until jsonArray.length()) {
			val jsonObject = jsonArray.getJSONObject(i)
			val cv = ContentValues()
			
			cv.put("id", jsonObject.getInt("id"))
			cv.put("name", jsonObject.getString("name"))
			cv.put("img_url", jsonObject.getString("img_url"))
			cv.put("inner_img_url", jsonObject.getString("inner_img_url"))
			cv.put("text", jsonObject.getString("text"))
			
			database.insert(Database.TBL_TESTAMENT, cv)
		}
		onSaveFinished()
	}
	
	/**
	 * saves operation downloaded data to database
	 *
	 * @param jsonArray the json downloaded by download method
	 * @param onSaveFinished on finished saving contents
	 */
	protected fun saveToOperation(jsonArray: JSONArray, onSaveFinished: () -> Unit) {
		for (i in 0 until jsonArray.length()) {
			val jsonObject = jsonArray.getJSONObject(i)
			val cv = ContentValues()
			
			cv.put("id", jsonObject.getInt("id"))
			cv.put("name", jsonObject.getString("name"))
			cv.put("img_url", jsonObject.getString("img_url"))
			cv.put("inner_img_url", jsonObject.getString("inner_img_url"))
			cv.put("text", jsonObject.getString("text"))
			
			database.insert(Database.TBL_OPERATION, cv)
		}
		onSaveFinished()
	}
}