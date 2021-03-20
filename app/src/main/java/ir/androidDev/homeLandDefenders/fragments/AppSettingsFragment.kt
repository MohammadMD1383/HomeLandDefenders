package ir.androidDev.homeLandDefenders.fragments

import android.content.ContentValues
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import ir.androidDev.homeLandDefenders.R
import ir.androidDev.homeLandDefenders.database.Database
import ir.androidDev.homeLandDefenders.utility.Util
import kotlinx.android.synthetic.main.fragment_app_settings.*
import kotlinx.android.synthetic.main.fragment_app_settings.view.*
import kotlin.system.exitProcess

class AppSettingsFragment : Fragment() {
	
	/* the whole view of fragment */
	private var rootView: View? = null
	
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		if (rootView == null) {
			rootView = inflater.inflate(R.layout.fragment_app_settings, container, false)
			
			/* database */
			val database = Database(activity!!.applicationContext)
			
			/* setup spinners */
			setupFontSpinner()
			setupFontSizesSpinner()
			
			/* load settings */
			loadSettings(database)
			
			/* on save click listener */
			setOnSaveClickListener(database)
		}
		return rootView
	}
	
	/**
	 * setups the spinner for app's font family
	 */
	private fun setupFontSpinner() {
		rootView!!.spin_appSettingsFragment_fontSpinner.adapter = ArrayAdapter(
			activity!!.applicationContext,
			R.layout.spinner_layout,
			R.id.tv_spinnerLayout_textView,
			resources.getStringArray(R.array.font_names)
		)
	}
	
	/**
	 * setups the spinner for app's font size
	 */
	private fun setupFontSizesSpinner() {
		rootView!!.spin_appSettingsFragment_fontSizeSpinner.adapter = ArrayAdapter(
			activity!!.applicationContext,
			R.layout.spinner_layout,
			R.id.tv_spinnerLayout_textView,
			resources.getStringArray(R.array.font_sizes)
		)
	}
	
	/**
	 * loads app's settings from database
	 */
	private fun loadSettings(db: Database) {
		val cursor1 = db.getRowBy(Database.TBL_SETTINGS, "name", "nick_name")
		rootView!!.editTextTextPersonName.setText(cursor1.getString(1))
		
		val cursor2 = db.getRowBy(Database.TBL_SETTINGS, "name", "font_family")
		val cursor3 = db.getRowBy(Database.TBL_SETTINGS, "name", "font_size")
		
		val fontFamilies = resources.getStringArray(R.array.font_names)
		
		rootView!!.spin_appSettingsFragment_fontSpinner.setSelection(
			when (cursor2.getString(1)) {
				fontFamilies[0] -> 0
				fontFamilies[1] -> 1
				fontFamilies[2] -> 2
				fontFamilies[3] -> 3
				fontFamilies[4] -> 4
				else -> 0
			}
		)
		
		val fontSizes = resources.getStringArray(R.array.font_sizes)
		
		rootView!!.spin_appSettingsFragment_fontSizeSpinner.setSelection(
			when (cursor3.getString(1)) {
				fontSizes[0] -> 0
				fontSizes[1] -> 1
				fontSizes[2] -> 2
				fontSizes[3] -> 3
				fontSizes[4] -> 4
				fontSizes[5] -> 5
				else -> 1
			}
		)
		
		val cursor4 = db.getRowBy(Database.TBL_SETTINGS, "name", "auto_download")
		
		if (cursor4.getInt(1) == 0) {
			rootView!!.sch_appSettingsFragment_autoDownload.isChecked = false
		}
	}
	
	/**
	 * sets the action of save button
	 */
	private fun setOnSaveClickListener(db: Database) {
		rootView!!.btn_appSettingsFragment_save.setOnClickListener {
			if (editTextTextPersonName.text.trim().isEmpty()) {
				Toast.makeText(activity!!.applicationContext, getString(R.string.app_settings_string_please_fill_nick_name), Toast.LENGTH_SHORT)
					.show()
				return@setOnClickListener
			}
			
			val cv1 = ContentValues()
			val cv2 = ContentValues()
			val cv3 = ContentValues()
			val cv4 = ContentValues()
			
			cv1.put("value", editTextTextPersonName.text.toString())
			cv2.put("value", spin_appSettingsFragment_fontSpinner.selectedItem.toString())
			cv3.put("value", spin_appSettingsFragment_fontSizeSpinner.selectedItem.toString())
			cv4.put("value", sch_appSettingsFragment_autoDownload.isChecked.let { if (it) 1 else 0 })
			
			db.updateBy(Database.TBL_SETTINGS, cv1, "name", "nick_name")
			db.updateBy(Database.TBL_SETTINGS, cv2, "name", "font_family")
			db.updateBy(Database.TBL_SETTINGS, cv3, "name", "font_size")
			db.updateBy(Database.TBL_SETTINGS, cv4, "name", "auto_download")
			
			Util(context!!).makeAlertDialog(
				getString(R.string.app_settings_string_title),
				getString(R.string.app_settings_string_please_restart_app),
				false
			) { exitProcess(0) }
		}
	}
}