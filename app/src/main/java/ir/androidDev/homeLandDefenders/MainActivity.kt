package ir.androidDev.homeLandDefenders

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ir.androidDev.homeLandDefenders.contentManagement.ContentManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
	
	/* Intent to home page */
	private var i: Intent? = null
	
	/* Delayed intent */
	private val handler = Handler(Looper.getMainLooper())
	
	override fun onCreate(savedInstanceState: Bundle?) {
		/* force direction to RTL */
		window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
		
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		
		/* intent */
		i = Intent(this, HomePageActivity::class.java)
		
		/* check database */
		checkContents()
	}
	
	/**
	 * checks the database and downloads missing data
	 */
	private fun checkContents() {
		ContentManager(this).checkContents {
			it.download({
				Toast.makeText(this, getString(R.string.string_please_connect), Toast.LENGTH_LONG).show()
				finish()
			}, { resume() })
		}
	}
	
	/**
	 * continue loading application
	 */
	private fun resume() {
		/* make the page visible */
		mainActivity_welcome.visibility = View.VISIBLE
		
		/* intent by delay */
		handler.postDelayed(
			{
				startActivity(i)
				
				/* change the transition of intent */
				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
				
				finish()
			},
			5000
		)
		
		/* intent On Click */
		mainActivity_welcome.setOnClickListener {
			/* remove the delayed intent */
			handler.removeCallbacksAndMessages(null)
			
			startActivity(i)
			
			/* change the transition of intent */
			overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
			
			finish()
		}
	}
}