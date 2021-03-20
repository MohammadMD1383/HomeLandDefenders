package ir.androidDev.homeLandDefenders

import android.content.ContentValues
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.database.getIntOrNull
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.DefaultRetryPolicy
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.appbar.AppBarLayout
import ir.androidDev.homeLandDefenders.adapters.WarHistoryAdapter
import ir.androidDev.homeLandDefenders.connections.Url
import ir.androidDev.homeLandDefenders.contentManagement.ContentManager
import ir.androidDev.homeLandDefenders.dataModels.WarHistoryPart
import ir.androidDev.homeLandDefenders.database.Database
import kotlinx.android.synthetic.main.activity_war_history.*

class WarHistoryActivity : CustomizableActivity() {
	
	/* war history parts */
	companion object {
		private var warHistoryParts: MutableList<WarHistoryPart> = ArrayList()
	}
	
	/* database */
	private val database = Database(this)
	
	/* adapter */
	private var adapter: WarHistoryAdapter? = null
	
	/* auto download */
	private var autoDownload: Boolean? = null
	
	/* volley request queue */
	private var queue: RequestQueue? = null
	
	/* last scroll position */
	private var lastScroll = 0
	
	override fun onCreate(savedInstanceState: Bundle?) {
		/* force direction to RTL */
		window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
		
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_war_history)
		
		/* make the title marquee */
		tv_warHistoryActivity_titleTv.isSelected = true
		
		/* load download preferences */
		loadPreferences()
		
		/* setup recycler view */
		setupRecyclerView()
		
		/* gather the scroll state */
		gatherScrollState()
		
		/* load the text */
		loadContent()
		
		/* back to home button */
		iv_warHistoryActivity_back.setOnClickListener { finish() }
		
		/* on refresh click */
		iv_warHistoryActivity_refresh.setOnClickListener { loadContent() }
		
		/* on load scroll state click */
		iv_warHistoryActivity_loadState.setOnClickListener { nsv_warHistoryActivity_mainContainer.smoothScrollTo(0, lastScroll, 1500) }
		
		/* on save scroll state click */
		fab_warHistoryActivity_saveState.setOnClickListener { saveScrollState() }
		
		/* on scroll hide/show fab */
		var oldI = 0
		apb_warHistoryPageActivity_appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, i ->
			fab_warHistoryActivity_saveState.let { if (i < oldI) it.hide() else it.show() }
			oldI = i + 1
		})
	}
	
	/**
	 * loads the auto download permission from database
	 */
	private fun loadPreferences() {
		autoDownload = database.getRowBy(Database.TBL_SETTINGS, "name", "auto_download").getInt(1) == 1
	}
	
	/**
	 * setups the recycler view
	 */
	private fun setupRecyclerView() {
		rv_warHistoryActivity_recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
		adapter = WarHistoryAdapter(this, warHistoryParts, autoDownload!!)
		rv_warHistoryActivity_recyclerView.adapter = adapter
	}
	
	/**
	 * gathers the scroll state
	 */
	private fun gatherScrollState() {
		val scrollPos = database.getRowBy(Database.TBL_SCROLL, "name", Database.WAR_HISTORY).getIntOrNull(2)
		
		if (scrollPos == null) iv_warHistoryActivity_loadState.visibility = View.GONE else lastScroll = scrollPos
	}
	
	/**
	 * checks internet connection
	 * sends request to server
	 * claims and shows the text
	 */
	private fun loadContent() {
		/* check if doesn't need to download */
		if (warHistoryParts.size > 0) {
			loaders(prb = false, ref = false)
			return
		}
		
		/* create volley request queue if is null */
		if (queue == null) queue = Volley.newRequestQueue(this)
		
		loaders(prb = true, ref = false)
		
		if (!ContentManager.isConnected(this)) {
			loaders(prb = false, ref = true)
			Toast.makeText(this, getString(R.string.string_please_connect), Toast.LENGTH_SHORT).show()
			return
		}
		
		val request = object : JsonArrayRequest(Url.let { it.REPOSITORY_V1 + it.GET + it.WAR_HISTORY }, {
			for (i in 0 until it.length()) {
				val jsonObject = it.getJSONObject(i)
				val warHistoryPart = WarHistoryPart()
				
				warHistoryPart.text = jsonObject.getString("text")
				warHistoryPart.imgUrl = jsonObject.getString("img_url")
				
				warHistoryParts.add(warHistoryPart)
			}
			adapter!!.notifyDataSetChanged()
			loaders(prb = false, ref = false)
		}, {
			Toast.makeText(this, getString(R.string.json_download_volley_fail_string), Toast.LENGTH_SHORT).show()
			loaders(prb = false, ref = true)
		}) {
			override fun getHeaders(): MutableMap<String, String> {
				return ContentManager.getCookie(this@WarHistoryActivity)
			}
		}
		
		/* set request retry policy */
		request.retryPolicy = DefaultRetryPolicy(20000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
		
		/* add request to queue and send */
		queue!!.add(request)
	}
	
	/**
	 * saves the scroll state on fab click
	 */
	private fun saveScrollState() {
		val scroll = nsv_warHistoryActivity_mainContainer.scrollY
		
		val cv = ContentValues()
		cv.put("scroll", scroll)
		
		database.updateBy(Database.TBL_SCROLL, cv, "name", Database.WAR_HISTORY)
		lastScroll = scroll
		
		Toast.makeText(this, getString(R.string.save_state_string_saved), Toast.LENGTH_SHORT).show()
		
		iv_warHistoryActivity_loadState.let { if (it.visibility == View.GONE) it.visibility = View.VISIBLE }
	}
	
	/**
	 * changes the visibility of progressBar and refresh button
	 *
	 * @param prb the ProgressBar
	 * @param ref the Refresh button
	 */
	private fun loaders(prb: Boolean, ref: Boolean) {
		iv_warHistoryActivity_refresh.visibility = if (ref) View.VISIBLE else View.GONE
		prb_warHistoryActivity_progressBar.visibility = if (prb) View.VISIBLE else View.GONE
	}
}