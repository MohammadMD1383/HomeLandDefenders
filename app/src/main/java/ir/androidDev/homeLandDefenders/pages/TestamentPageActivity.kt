package ir.androidDev.homeLandDefenders.pages

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.appbar.AppBarLayout
import com.squareup.picasso.Callback
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import ir.androidDev.homeLandDefenders.CustomizableActivity
import ir.androidDev.homeLandDefenders.R
import ir.androidDev.homeLandDefenders.TestamentActivity
import ir.androidDev.homeLandDefenders.database.Database
import kotlinx.android.synthetic.main.activity_testament_page.*

class TestamentPageActivity : CustomizableActivity() {
	
	/* database */
	private val database = Database(this)
	
	override fun onCreate(savedInstanceState: Bundle?) {
		/* force direction to RTL */
		window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
		
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_testament_page)
		
		/* make the title marquee */
		tv_testamentPageActivity_titleTv.isSelected = true
		
		/* check parameters passed in intent */
		checkIntentParams()
		
		/* get data and show on page */
		getAndShowData()
		
		/* back to TestamentActivity */
		iv_testamentPageActivity_back.setOnClickListener {
			startActivity(Intent(this, TestamentActivity::class.java))
			finish()
		}
		
		/* save scroll position */
		fab_testamentPageActivity_saveState.setOnClickListener {
			val cv = ContentValues()
			cv.put("ext_id", intent.getIntExtra(TestamentActivity.BIO_ID, 0))
			cv.put("scroll", nsv_testamentPageActivity_mainContainer.scrollY)
			database.updateBy(Database.TBL_SCROLL, cv, "name", Database.TBL_TESTAMENT)
			
			Toast.makeText(this, getString(R.string.save_state_string_saved), Toast.LENGTH_SHORT).show()
		}
		
		/* on scroll hide/show fab */
		var oldI = 0
		apb_testamentPageActivity_appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, i ->
			fab_testamentPageActivity_saveState.let { if (i < oldI) it.hide() else it.show() }
			oldI = i + 1
		})
	}
	
	/**
	 * checks that this activity started with all required parameters
	 */
	private fun checkIntentParams() {
		if (!intent.hasExtra(TestamentActivity.BIO_ID)) {
			startActivity(Intent(this, TestamentActivity::class.java))
			finish()
		}
	}
	
	/**
	 * claims and shows the data gotten from database
	 */
	private fun getAndShowData() {
		val cursor = database.getRowById(Database.TBL_TESTAMENT, intent.getIntExtra(TestamentActivity.BIO_ID, 0))
		
		tv_testamentPageActivity_titleTv.text = cursor.getString(1)
		tv_testamentPageActivity_mainTv.text = cursor.getString(4)
		
		/* get permission for download */
		val ad = database.getRowBy(Database.TBL_SETTINGS, "name", "auto_download").getInt(1) == 1
		
		/* generate image url and placeholder */
		val url = cursor.getString(3)
		val placeholder = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_photo_24, null)!!
		
		/* check if there is no photo then set the visibility to GONE */
		if (url.contains("void(0)", true)) fr_testamentPageActivity_imageContainer.visibility = View.GONE
		
		/* sets photo if cached else downloads it and then shows */
		Picasso.get().load(url).centerInside().fit().networkPolicy(NetworkPolicy.OFFLINE).placeholder(placeholder)
			.into(iv_testamentPageActivity_topPhoto, object : Callback {
				override fun onSuccess() {}
				override fun onError(e: Exception?) {
					if (ad)
						Picasso.get().load(url).centerInside().fit().placeholder(placeholder)
							.into(iv_testamentPageActivity_topPhoto)
				}
			})
		
		if (intent.hasExtra(TestamentActivity.SCROLL_P)) {
			Handler(Looper.getMainLooper()).postDelayed({
				nsv_testamentPageActivity_mainContainer.smoothScrollTo(0, intent.getIntExtra(TestamentActivity.SCROLL_P, 0), 1500)
			}, 0)
		}
	}
	
	override fun onBackPressed() {
		startActivity(Intent(this, TestamentActivity::class.java))
		finish()
		super.onBackPressed()
	}
}