package ir.androidDev.homeLandDefenders

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.database.getIntOrNull
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ir.androidDev.homeLandDefenders.adapters.TestamentItemAdapter
import ir.androidDev.homeLandDefenders.dataModels.TestamentItem
import ir.androidDev.homeLandDefenders.database.Database
import ir.androidDev.homeLandDefenders.pages.TestamentPageActivity
import kotlinx.android.synthetic.main.activity_testament.*

class TestamentActivity : CustomizableActivity() {
	
	/* items list */
	private val testamentItems: MutableList<TestamentItem> = ArrayList()
	
	/* database */
	private val database = Database(this)
	
	/* adapter */
	private var adapter: TestamentItemAdapter? = null
	
	/* auto download */
	private var autoDownload: Boolean? = null
	
	/* fields */
	companion object {
		const val BIO_ID = "bio_id"
		const val SCROLL_P = "scroll_pos"
	}
	
	override fun onCreate(savedInstanceState: Bundle?) {
		/* force direction to RTL */
		window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
		
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_testament)
		
		/* make the title marquee */
		tv_testamentActivity_titleTv.isSelected = true
		
		/* back to home button */
		iv_testamentActivity_back.setOnClickListener { finish() }
		
		/* search view color fix */
		searchViewColorFix()
		
		/* open search view */
		sv_testamentActivity_searchView.setOnSearchClickListener {
			tv_testamentActivity_titleTv.visibility = View.GONE
			iv_testamentActivity_back.visibility = View.GONE
		}
		
		/* close search view */
		sv_testamentActivity_searchView.setOnCloseListener {
			tv_testamentActivity_titleTv.visibility = View.VISIBLE
			iv_testamentActivity_back.visibility = View.VISIBLE
			return@setOnCloseListener false
		}
		
		/* apply search filters */
		sv_testamentActivity_searchView.setOnQueryTextListener(object :
			SearchView.OnQueryTextListener {
			override fun onQueryTextSubmit(query: String?): Boolean {
				adapter!!.filter(query)
				return true
			}
			
			override fun onQueryTextChange(newText: String?): Boolean {
				adapter!!.filter(newText)
				return true
			}
		})
		
		/* get data from database */
		getData()
		
		/* check if there is a saved state to load */
		val cursor = database.getRowBy(Database.TBL_SCROLL, "name", Database.TBL_TESTAMENT)
		if (cursor.getIntOrNull(1) == null) fab_testamentActivity_loadState.hide()
		
		/* load last saved state */
		fab_testamentActivity_loadState.setOnClickListener {
			startActivity(
				Intent(this, TestamentPageActivity::class.java)
					.putExtra(BIO_ID, cursor.getInt(1))
					.putExtra(SCROLL_P, cursor.getInt(2))
			)
			finish()
		}
		
		/* setup the recycler view */
		setupRecyclerView()
	}
	
	/**
	 * fixes the colors of texts and icons of search view
	 */
	private fun searchViewColorFix() {
		val sac: SearchView.SearchAutoComplete =
			sv_testamentActivity_searchView.findViewById(androidx.appcompat.R.id.search_src_text)
		sac.setTextColor(Color.WHITE)
		sac.setHintTextColor(Color.WHITE)
	}
	
	/**
	 * claims data from database
	 */
	private fun getData() {
		val cursor = database.getData(Database.TBL_TESTAMENT)
		
		while (cursor.moveToNext()) {
			val testamentItem = TestamentItem()
			
			testamentItem.id = cursor.getInt(0)
			testamentItem.name = cursor.getString(1)
			testamentItem.imgUrl = cursor.getString(2)
			
			testamentItems.add(testamentItem)
		}
		
		val ad = database.getRowBy(Database.TBL_SETTINGS, "name", "auto_download")
		autoDownload = ad.getInt(1) == 1
	}
	
	/**
	 * setups recycler view
	 */
	private fun setupRecyclerView() {
		rv_testamentActivity_recyclerView.layoutManager =
			LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
		
		adapter = TestamentItemAdapter(this, testamentItems, autoDownload!!) {
			startActivity(Intent(this, TestamentPageActivity::class.java).putExtra(BIO_ID, it))
			finish()
		}
		rv_testamentActivity_recyclerView.adapter = adapter
		
		/* check if fab is available */
		if (fab_testamentActivity_loadState.isOrWillBeHidden) return
		
		/* hide/show floating action button by scroll */
		rv_testamentActivity_recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
			override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
				super.onScrolled(recyclerView, dx, dy)
				fab_testamentActivity_loadState.let { if (dy > 0) it.hide() else it.show() }
			}
		})
	}
}