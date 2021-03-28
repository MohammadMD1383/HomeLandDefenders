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
import ir.androidDev.homeLandDefenders.databinding.ActivityTestamentBinding
import ir.androidDev.homeLandDefenders.pages.TestamentPageActivity

class TestamentActivity : CustomizableActivity() {
	
	/* view binding */
	private lateinit var binding: ActivityTestamentBinding
	
	/* items list */
	private val testamentItems: MutableList<TestamentItem> = ArrayList()
	
	/* database */
	private val database = Database(this)
	
	/* adapter */
	private var adapter: TestamentItemAdapter? = null
	
	/* auto download */
	private var autoDownload: Boolean? = null
	
	/* show load state fab */
	private var canShowLoadStateFab = false
	
	/* fields */
	companion object {
		const val BIO_ID = "bio_id"
		const val SCROLL_P = "scroll_pos"
	}
	
	override fun onCreate(savedInstanceState: Bundle?) {
		/* force direction to RTL */
		window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
		
		super.onCreate(savedInstanceState)
		binding = ActivityTestamentBinding.inflate(layoutInflater)
		val view = binding.root
		setContentView(view)
		
		/* make the title marquee */
		binding.tvTestamentActivityTitleTv.isSelected = true
		
		/* back to home button */
		binding.ivTestamentActivityBack.setOnClickListener { finish() }
		
		/* search view color fix */
		searchViewColorFix()
		
		/* open search view */
		binding.svTestamentActivitySearchView.setOnSearchClickListener {
			binding.tvTestamentActivityTitleTv.visibility = View.GONE
			binding.ivTestamentActivityBack.visibility = View.GONE
		}
		
		/* close search view */
		binding.svTestamentActivitySearchView.setOnCloseListener {
			binding.tvTestamentActivityTitleTv.visibility = View.VISIBLE
			binding.ivTestamentActivityBack.visibility = View.VISIBLE
			return@setOnCloseListener false
		}
		
		/* apply search filters */
		binding.svTestamentActivitySearchView.setOnQueryTextListener(object :
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
		
		/* load last saved state */
		binding.fabTestamentActivityLoadState.setOnClickListener {
			with(database.getRowBy(Database.TBL_SCROLL, "name", Database.TBL_TESTAMENT)) {
				startActivity(
					Intent(this@TestamentActivity, TestamentPageActivity::class.java)
						.putExtra(BIO_ID, getInt(1))
						.putExtra(SCROLL_P, getInt(2))
				)
				close()
			}
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
	 * fixes the colors of texts and icons of search view
	 */
	private fun searchViewColorFix() {
		val sac: SearchView.SearchAutoComplete = binding.svTestamentActivitySearchView.findViewById(androidx.appcompat.R.id.search_src_text)
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
		
		cursor.close()
	}
	
	/**
	 * checks whether a scroll state is available in database and then shows the fab
	 */
	private fun checkLoadStateAvailability() {
		val cursor = database.getRowBy(Database.TBL_SCROLL, "name", Database.TBL_TESTAMENT)
		cursor.getIntOrNull(1).let { cond ->
			binding.fabTestamentActivityLoadState.let {
				if (cond == null) {
					canShowLoadStateFab = false
					it.hide()
				} else {
					canShowLoadStateFab = true
					it.show()
				}
			}
		}
		
		cursor.close()
	}
	
	/**
	 * setups recycler view
	 */
	private fun setupRecyclerView() {
		binding.rvTestamentActivityRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
		
		adapter = TestamentItemAdapter(this, testamentItems, autoDownload!!) {
			startActivity(Intent(this, TestamentPageActivity::class.java).putExtra(BIO_ID, it))
		}
		binding.rvTestamentActivityRecyclerView.adapter = adapter
		
		/* check if fab is available */
		if (binding.fabTestamentActivityLoadState.isOrWillBeHidden) return
		
		/* hide/show floating action button by scroll */
		binding.rvTestamentActivityRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
			override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
				super.onScrolled(recyclerView, dx, dy)
				
				if (canShowLoadStateFab) binding.fabTestamentActivityLoadState.let { if (dy > 0) it.hide() else it.show() }
			}
		})
	}
}