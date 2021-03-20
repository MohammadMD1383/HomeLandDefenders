package ir.androidDev.homeLandDefenders

import android.graphics.Typeface
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import ir.androidDev.homeLandDefenders.database.Database

open class CustomizableActivity : AppCompatActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		/* load app's settings preferences */
		loadThemeSettings()
		
		super.onCreate(savedInstanceState)
	}
	
	/**
	 * loads app's preferences from database and apply them
	 */
	private fun loadThemeSettings() {
		val database = Database(this)
		
		val cursor1 = database.getRowBy(Database.TBL_SETTINGS, "name", "font_family")
		val fontFamilies = resources.getStringArray(R.array.font_names)
		
		setTheme(
			when (cursor1.getString(1)) {
				fontFamilies[0] -> R.style.AppTheme_FontFamily_iran_sans
				fontFamilies[1] -> R.style.AppTheme_FontFamily_yekan
				fontFamilies[2] -> R.style.AppTheme_FontFamily_roya
				fontFamilies[3] -> R.style.AppTheme_FontFamily_koodak
				fontFamilies[4] -> R.style.AppTheme_FontFamily_nazanin
				else -> R.style.AppTheme_FontFamily_iran_sans
			}
		)
		
		val cursor2 = database.getRowBy(Database.TBL_SETTINGS, "name", "font_size")
		val fontSizes = resources.getStringArray(R.array.font_sizes)
		
		setTheme(
			when (cursor2.getString(1)) {
				fontSizes[0] -> R.style.AppTheme_fontSize14
				fontSizes[1] -> R.style.AppTheme_fontSize16
				fontSizes[2] -> R.style.AppTheme_fontSize18
				fontSizes[3] -> R.style.AppTheme_fontSize20
				fontSizes[4] -> R.style.AppTheme_fontSize22
				fontSizes[5] -> R.style.AppTheme_fontSize24
				else -> R.style.AppTheme_fontSize16
			}
		)
	}
}