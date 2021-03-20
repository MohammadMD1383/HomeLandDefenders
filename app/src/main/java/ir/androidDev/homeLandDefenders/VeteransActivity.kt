package ir.androidDev.homeLandDefenders

import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_veterans.*

class VeteransActivity : CustomizableActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		/* force direction to RTL */
		window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
		
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_veterans)
		
		/* make the title marquee */
		tv_veteransActivity_titleTv.isSelected = true
		
		/* back to home button */
		iv_veteransActivity_back.setOnClickListener { finish() }
	}
}