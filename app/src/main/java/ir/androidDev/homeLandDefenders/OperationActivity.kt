package ir.androidDev.homeLandDefenders

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.database.getIntOrNull
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ir.androidDev.homeLandDefenders.adapters.OperationItemAdapter
import ir.androidDev.homeLandDefenders.dataModels.OperationItem
import ir.androidDev.homeLandDefenders.database.Database
import ir.androidDev.homeLandDefenders.pages.OperationPageActivity
import kotlinx.android.synthetic.main.activity_operations.*

class OperationActivity : CustomizableActivity() {
	
	/* items list */
	private val operationItems: MutableList<OperationItem> = ArrayList()
	
	/* database */
	private val database = Database(this)
	
	/* adapter */
	private var adapter: OperationItemAdapter? = null
	
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
		setContentView(R.layout.activity_operations)
		
		/* make the title marquee */
		tv_operationsActivity_titleTv.isSelected = true
		
		/* back to home button */
		iv_operationsActivity_back.setOnClickListener { finish() }
		
		/* open search view */
		sv_operationsActivity_searchView.setOnSearchClickListener {
			tv_operationsActivity_titleTv.visibility = View.GONE
			iv_operationsActivity_back.visibility = View.GONE
		}
		
		/* close search view */
		sv_operationsActivity_searchView.setOnCloseListener {
			tv_operationsActivity_titleTv.visibility = View.VISIBLE
			iv_operationsActivity_back.visibility = View.VISIBLE
			return@setOnCloseListener false
		}
		
		/* apply search filters */
		sv_operationsActivity_searchView.setOnQueryTextListener(object :
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
		
		/* search view color fix */
		searchViewColorFix()
		
		/* get data from database */
		getData()
		
		/* check if there is a saved state to load */
		val cursor = database.getRowBy(Database.TBL_SCROLL, "name", Database.TBL_OPERATION)
		if (cursor.getIntOrNull(1) == null) fab_operationsActivity_loadState.hide()
		
		/* load last saved state */
		fab_operationsActivity_loadState.setOnClickListener {
			startActivity(
				Intent(this, OperationPageActivity::class.java)
					.putExtra(BIO_ID, cursor.getInt(1))
					.putExtra(SCROLL_P, cursor.getInt(2))
			)
			finish()
		}
		
		/* setup recycler view */
		setupRecyclerView()
	}
	
	/**
	 * fixes the color of icons and texts in search view
	 */
	private fun searchViewColorFix() {
		val sac: SearchView.SearchAutoComplete =
			sv_operationsActivity_searchView.findViewById(androidx.appcompat.R.id.search_src_text)
		sac.setHintTextColor(Color.WHITE)
		sac.setTextColor(Color.WHITE)
	}
	
	/**
	 * claims data from database
	 */
	private fun getData() {
		val cursor = database.getData(Database.TBL_OPERATION)
		
		while (cursor.moveToNext()) {
			val operationItem = OperationItem()
			
			operationItem.id = cursor.getInt(0)
			operationItem.name = cursor.getString(1)
			operationItem.imgUrl = cursor.getString(2)
			
			operationItems.add(operationItem)
		}
		
		val ad = database.getRowBy(Database.TBL_SETTINGS, "name", "auto_download")
		autoDownload = ad.getInt(1) == 1
	}
	
	/**
	 * setups the recycler view
	 */
	private fun setupRecyclerView() {
		rv_operationsActivity_recyclerView.layoutManager =
			LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
		
		adapter = OperationItemAdapter(this, operationItems, autoDownload!!) {
			startActivity(Intent(this, OperationPageActivity::class.java).putExtra(BIO_ID, it))
			finish()
		}
		rv_operationsActivity_recyclerView.adapter = adapter
		
		/* check if fab is available */
		if (fab_operationsActivity_loadState.isOrWillBeHidden) return
		
		/* hide/show floating action button by scroll */
		rv_operationsActivity_recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
			override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
				super.onScrolled(recyclerView, dx, dy)
				fab_operationsActivity_loadState.let { if (dy > 0) it.hide() else it.show() }
			}
		})
	}
}