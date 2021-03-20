package ir.androidDev.homeLandDefenders

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.database.getIntOrNull
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ir.androidDev.homeLandDefenders.adapters.BiographyItemAdapter
import ir.androidDev.homeLandDefenders.dataModels.BiographyItem
import ir.androidDev.homeLandDefenders.database.Database
import ir.androidDev.homeLandDefenders.pages.BiographyPageActivity
import kotlinx.android.synthetic.main.activity_biography.*

class BiographyActivity : CustomizableActivity() {
	
	/* biography list */
	private val biographyItems: MutableList<BiographyItem> = ArrayList()
	
	/* database */
	private val database = Database(this)
	
	/* adapter */
	private var adapter: BiographyItemAdapter? = null
	
	/* auto download photos */
	private var autoDownload: Boolean? = null
	
	/* the fields for intent */
	companion object {
		const val BIO_ID = "bio_id"
		const val SCROLL_P = "scroll_pos"
	}
	
	override fun onCreate(savedInstanceState: Bundle?) {
		/* force direction to RTL */
		window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
		
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_biography)
		
		/* make the title marquee */
		tv_biographyActivity_titleTv.isSelected = true
		
		/* back to home button */
		iv_biographyActivity_back.setOnClickListener { finish() }
		
		/* color fix the search view */
		searchViewColorFix()
		
		/* open search box */
		sv_biographyActivity_searchView.setOnSearchClickListener {
			tv_biographyActivity_titleTv.visibility = View.GONE
			iv_biographyActivity_back.visibility = View.GONE
		}
		
		/* close search box */
		sv_biographyActivity_searchView.setOnCloseListener {
			tv_biographyActivity_titleTv.visibility = View.VISIBLE
			iv_biographyActivity_back.visibility = View.VISIBLE
			return@setOnCloseListener false
		}
		
		/* apply search filters */
		sv_biographyActivity_searchView.setOnQueryTextListener(object :
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
		
		/* get the data from database */
		getData()
		
		/* check if there is a saved state to load */
		val cursor = database.getRowBy(Database.TBL_SCROLL, "name", Database.TBL_BIOGRAPHY)
		if (cursor.getIntOrNull(1) == null) fab_biographyActivity_loadState.hide()
		
		/* load last saved state */
		fab_biographyActivity_loadState.setOnClickListener {
			startActivity(
				Intent(this, BiographyPageActivity::class.java)
					.putExtra(BIO_ID, cursor.getInt(1))
					.putExtra(SCROLL_P, cursor.getInt(2))
			)
			finish()
		}
		
		/* setup the recycler view */
		setupRecyclerView()
	}
	
	/**
	 * fixes the colors of search view buttons and texts
	 */
	private fun searchViewColorFix() {
		val sac: SearchView.SearchAutoComplete =
			sv_biographyActivity_searchView.findViewById(androidx.appcompat.R.id.search_src_text)
		sac.setHintTextColor(Color.WHITE)
		sac.setTextColor(Color.WHITE)
	}
	
	/**
	 * claim data from database
	 */
	private fun getData() {
		val cursor = database.getData(Database.TBL_BIOGRAPHY)
		
		while (cursor.moveToNext()) {
			val biographyItem = BiographyItem()
			biographyItem.id = cursor.getInt(0)
			biographyItem.name = cursor.getString(1)
			biographyItem.birth = cursor.getString(2)
			biographyItem.death = cursor.getString(3)
			biographyItem.rank = cursor.getString(4)
			biographyItem.age = cursor.getString(5)
			biographyItem.imgUrl = cursor.getString(6)
			biographyItems.add(biographyItem)
		}
		
		val ad = database.getRowBy(Database.TBL_SETTINGS, "name", "auto_download")
		autoDownload = ad.getInt(1) == 1
	}
	
	/**
	 * setups the recycler view
	 */
	private fun setupRecyclerView() {
		rv_biographyActivity_recyclerView.layoutManager =
			LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
		
		adapter = BiographyItemAdapter(this, biographyItems, autoDownload!!) {
			startActivity(Intent(this, BiographyPageActivity::class.java).putExtra(BIO_ID, it))
			finish()
		}
		rv_biographyActivity_recyclerView.adapter = adapter
		
		/* check if fab is available */
		if (fab_biographyActivity_loadState.isOrWillBeHidden) return
		
		/* hide/show floating action button by scroll */
		rv_biographyActivity_recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
			override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
				super.onScrolled(recyclerView, dx, dy)
				fab_biographyActivity_loadState.let { if (dy > 0) it.hide() else it.show() }
			}
		})
	}
}