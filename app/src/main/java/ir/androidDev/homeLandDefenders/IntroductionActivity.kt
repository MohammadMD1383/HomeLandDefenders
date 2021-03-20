package ir.androidDev.homeLandDefenders

import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_introduction.*

class IntroductionActivity : CustomizableActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		/* force direction to RTL */
		window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
		
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_introduction)
		
		/* make the title marquee */
		tv_introductionActivity_titleTv.isSelected = true
		
		/* back to home page by button */
		iv_introductionActivity_back.setOnClickListener { finish() }
	}
}