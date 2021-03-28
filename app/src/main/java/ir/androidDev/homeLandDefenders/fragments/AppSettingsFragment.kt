package ir.androidDev.homeLandDefenders.fragments

import android.content.ContentValues
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import ir.androidDev.homeLandDefenders.R
import ir.androidDev.homeLandDefenders.database.Database
import ir.androidDev.homeLandDefenders.databinding.FragmentAppSettingsBinding
import ir.androidDev.homeLandDefenders.utility.Util
import kotlin.system.exitProcess

class AppSettingsFragment : Fragment() {
	
	/* view binding */
	private var binding: FragmentAppSettingsBinding? = null
	
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		if (binding == null) {
			binding = DataBindingUtil.inflate(inflater, R.layout.fragment_app_settings, container, false)
			
			/* database */
			val database = Database(activity!!.applicationContext)
			
			/* setup spinners */
			setupFontSpinner()
			setupFontSizesSpinner()
			
			/* load settings */
			loadSettings(database)
			
			/* hides/shows the fab save button on scroll */
			setOnScrollAutoHideSaveButton()
			
			/* on save click listener */
			setOnSaveClickListener(database)
		}
		return binding!!.root
	}
	
	/**
	 * setups the spinner for app's font family
	 */
	private fun setupFontSpinner() {
		binding!!.spinAppSettingsFragmentFontSpinner.adapter = ArrayAdapter(
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
		binding!!.spinAppSettingsFragmentFontSizeSpinner.adapter = ArrayAdapter(
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
		binding!!.editTextTextPersonName.setText(cursor1.getString(1))
		cursor1.close()
		
		val cursor2 = db.getRowBy(Database.TBL_SETTINGS, "name", "font_family")
		val fontFamilies = resources.getStringArray(R.array.font_names)
		
		binding!!.spinAppSettingsFragmentFontSpinner.setSelection(
			when (cursor2.getString(1)) {
				fontFamilies[0] -> 0
				fontFamilies[1] -> 1
				fontFamilies[2] -> 2
				fontFamilies[3] -> 3
				fontFamilies[4] -> 4
				else -> 0
			}
		)
		cursor2.close()
		
		val cursor3 = db.getRowBy(Database.TBL_SETTINGS, "name", "font_size")
		val fontSizes = resources.getStringArray(R.array.font_sizes)
		
		binding!!.spinAppSettingsFragmentFontSizeSpinner.setSelection(
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
		cursor3.close()
		
		val cursor4 = db.getRowBy(Database.TBL_SETTINGS, "name", "auto_download")
		if (cursor4.getInt(1) == 0) binding!!.schAppSettingsFragmentAutoDownload.isChecked = false
		cursor4.close()
		
		val cursor5 = db.getRowBy(Database.TBL_SETTINGS, "name", "app_update")
		if (cursor5.getInt(1) == 0) binding!!.schAppSettingsFragmentAppUpdate.isChecked = false
		cursor5.close()
		
		val cursor6 = db.getRowBy(Database.TBL_SETTINGS, "name", "new_data")
		if (cursor6.getInt(1) == 0) binding!!.schAppSettingsFragmentNewData.isChecked = false
		cursor6.close()
	}
	
	/**
	 * shows/hides the fab save button based on page scroll
	 */
	private fun setOnScrollAutoHideSaveButton() {
		binding!!.nsvAppSettingsFragmentScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
			binding!!.fabAppSettingsFragmentSave.let {
				if (scrollY > oldScrollY) it.hide() else it.show()
			}
		})
	}
	
	/**
	 * sets the action of save button
	 */
	private fun setOnSaveClickListener(db: Database) {
		binding!!.fabAppSettingsFragmentSave.setOnClickListener {
			if (binding!!.editTextTextPersonName.text.trim().isEmpty()) {
				Toast.makeText(activity!!.applicationContext, getString(R.string.app_settings_string_please_fill_nick_name), Toast.LENGTH_SHORT).show()
				return@setOnClickListener
			}
			
			val cv1 = ContentValues()
			val cv2 = ContentValues()
			val cv3 = ContentValues()
			val cv4 = ContentValues()
			val cv5 = ContentValues()
			val cv6 = ContentValues()
			
			cv1.put("value", binding!!.editTextTextPersonName.text.toString())
			cv2.put("value", binding!!.spinAppSettingsFragmentFontSpinner.selectedItem.toString())
			cv3.put("value", binding!!.spinAppSettingsFragmentFontSizeSpinner.selectedItem.toString())
			cv4.put("value", binding!!.schAppSettingsFragmentAutoDownload.isChecked.let { if (it) 1 else 0 })
			cv5.put("value", binding!!.schAppSettingsFragmentAppUpdate.isChecked.let { if (it) 1 else 0 })
			cv6.put("value", binding!!.schAppSettingsFragmentNewData.isChecked.let { if (it) 1 else 0 })
			
			db.updateBy(Database.TBL_SETTINGS, cv1, "name", "nick_name")
			db.updateBy(Database.TBL_SETTINGS, cv2, "name", "font_family")
			db.updateBy(Database.TBL_SETTINGS, cv3, "name", "font_size")
			db.updateBy(Database.TBL_SETTINGS, cv4, "name", "auto_download")
			db.updateBy(Database.TBL_SETTINGS, cv5, "name", "app_update")
			db.updateBy(Database.TBL_SETTINGS, cv6, "name", "new_data")
			
			Util(context!!).makeAlertDialog(
				getString(R.string.app_settings_string_title),
				getString(R.string.app_settings_string_please_restart_app),
				false
			) { exitProcess(0) }
		}
	}
}