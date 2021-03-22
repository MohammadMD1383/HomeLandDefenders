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
import ir.androidDev.homeLandDefenders.databinding.ActivityBiographyBinding
import ir.androidDev.homeLandDefenders.pages.BiographyPageActivity

class BiographyActivity : CustomizableActivity() {
	
	/* view binding */
	private lateinit var binding: ActivityBiographyBinding
	
	/* biography list */
	private val biographyItems: MutableList<BiographyItem> = ArrayList()
	
	/* database */
	private val database = Database(this)
	
	/* adapter */
	private var adapter: BiographyItemAdapter? = null
	
	/* auto download photos */
	private var autoDownload: Boolean? = null
	
	/* show load state fab */
	private var canShowLoadStateFab = false
	
	/* the fields for intent */
	companion object {
		const val BIO_ID = "bio_id"
		const val SCROLL_P = "scroll_pos"
	}
	
	override fun onCreate(savedInstanceState: Bundle?) {
		/* force direction to RTL */
		window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
		
		super.onCreate(savedInstanceState)
		binding = ActivityBiographyBinding.inflate(layoutInflater)
		val view = binding.root
		setContentView(view)
		
		/* make the title marquee */
		binding.tvBiographyActivityTitleTv.isSelected = true
		
		/* back to home button */
		binding.ivBiographyActivityBack.setOnClickListener { finish() }
		
		/* color fix the search view */
		searchViewColorFix()
		
		/* open search box */
		binding.svBiographyActivitySearchView.setOnSearchClickListener {
			binding.tvBiographyActivityTitleTv.visibility = View.GONE
			binding.ivBiographyActivityBack.visibility = View.GONE
		}
		
		/* close search box */
		binding.svBiographyActivitySearchView.setOnCloseListener {
			binding.tvBiographyActivityTitleTv.visibility = View.VISIBLE
			binding.ivBiographyActivityBack.visibility = View.VISIBLE
			return@setOnCloseListener false
		}
		
		/* apply search filters */
		binding.svBiographyActivitySearchView.setOnQueryTextListener(object :
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
		
		/* load last saved state */
		binding.fabBiographyActivityLoadState.setOnClickListener {
			val cursor = database.getRowBy(Database.TBL_SCROLL, "name", Database.TBL_BIOGRAPHY)
			
			startActivity(
				Intent(this, BiographyPageActivity::class.java)
					.putExtra(BIO_ID, cursor.getInt(1))
					.putExtra(SCROLL_P, cursor.getInt(2))
			)
		}
		
		/* setup the recycler view */
		setupRecyclerView()
	}
	
	override fun onResume() {
		super.onResume()
		
		/* check if there is a saved state to load */
		checkLoadStateAvailability()
	}
	
	/**
	 * fixes the colors of search view buttons and texts
	 */
	private fun searchViewColorFix() {
		val sac: SearchView.SearchAutoComplete = binding.svBiographyActivitySearchView.findViewById(androidx.appcompat.R.id.search_src_text)
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
	 * checks whether a scroll state is available in database and then shows the fab
	 */
	private fun checkLoadStateAvailability() {
		val cursor = database.getRowBy(Database.TBL_SCROLL, "name", Database.TBL_BIOGRAPHY)
		
		cursor.getIntOrNull(1).let { cond ->
			binding.fabBiographyActivityLoadState.let {
				if (cond == null) {
					canShowLoadStateFab = false
					it.hide()
				} else {
					canShowLoadStateFab = true
					it.show()
				}
			}
		}
	}
	
	/**
	 * setups the recycler view
	 */
	private fun setupRecyclerView() {
		binding.rvBiographyActivityRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
		
		adapter = BiographyItemAdapter(this, biographyItems, autoDownload!!) {
			startActivity(Intent(this, BiographyPageActivity::class.java).putExtra(BIO_ID, it))
		}
		binding.rvBiographyActivityRecyclerView.adapter = adapter
		
		/* hide/show floating action button by scroll */
		binding.rvBiographyActivityRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
			override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
				super.onScrolled(recyclerView, dx, dy)
				
				if (canShowLoadStateFab) binding.fabBiographyActivityLoadState.let { if (dy > 0) it.hide() else it.show() }
			}
		})
	}
}