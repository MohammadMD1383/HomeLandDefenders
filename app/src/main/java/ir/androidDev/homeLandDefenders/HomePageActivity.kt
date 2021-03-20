package ir.androidDev.homeLandDefenders

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.style.TextAppearanceSpan
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import ir.androidDev.homeLandDefenders.connections.Url
import ir.androidDev.homeLandDefenders.contentManagement.ContentManager
import ir.androidDev.homeLandDefenders.contentManagement.NewContent
import ir.androidDev.homeLandDefenders.database.Database
import ir.androidDev.homeLandDefenders.fragments.*
import ir.androidDev.homeLandDefenders.utility.Util
import kotlinx.android.synthetic.main.activity_home_page.*
import kotlinx.android.synthetic.main.home_page_drawer_header.view.*

class HomePageActivity : CustomizableActivity() {
	
	/* database */
	private val database = Database(this)
	
	/* define fragments */
	private val appSettings = AppSettingsFragment()
	private val comments = CommentsFragment()
	private val sendContent = SendContentFragment()
	private val contactUs = ContactUsFragment()
	private val aboutUs = AboutUsFragment()
	
	/* util object */
	private val util = Util(this)
	
	override fun onCreate(savedInstanceState: Bundle?) {
		/* force direction to RTL */
		window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
		
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_home_page)
		
		/* make the title marquee */
		tv_homePageActivity_titleTv.isSelected = true
		
		/* Navigation view group text color fix */
		nvgViewColorFix()
		
		/* Navigation view and drawer layout */
		val aToggle = ActionBarDrawerToggle(
			this,
			drl_homePageActivity_container,
			R.string.action_bar_drawer_toggle_open,
			R.string.action_bar_drawer_toggle_close
		)
		drl_homePageActivity_container.addDrawerListener(aToggle)
		aToggle.syncState()
		
		/* show the user nick name */
		showUserNickName()
		
		/* set navigation item select listener */
		nvg_homeActivity_nav.setNavigationItemSelectedListener { handleNvgItemsClick(it) }
		
		/* drawer open button */
		iv_homePageActivity_openNav.setOnClickListener {
			drl_homePageActivity_container.openDrawer(GravityCompat.START)
		}
		
		/* main buttons intents */
		/* مقدمه */
		btn_homePageActivity_introduction.setOnClickListener {
			startActivity(Intent(this, IntroductionActivity::class.java))
		}
		
		/* زندگی نامه */
		btn_homePageActivity_biography.setOnClickListener {
			startActivity(Intent(this, BiographyActivity::class.java))
		}
		
		/* وصیت نامه */
		btn_homePageActivity_testament.setOnClickListener {
			startActivity(Intent(this, TestamentActivity::class.java))
		}
		
		/* نقشه عملیات ها */
		btn_homePageActivity_operations.setOnClickListener {
			startActivity(Intent(this, OperationActivity::class.java))
		}
		
		/* تاریخچه و جزئیات جنگ */
		btn_homePageActivity_warHistory.setOnClickListener {
			startActivity(Intent(this, WarHistoryActivity::class.java))
		}
		
		/* جانبازان گرانقدر */
		btn_homePageActivity_veterans.setOnClickListener {
			startActivity(Intent(this, VeteransActivity::class.java))
		}
	}
	
	/**
	 * fixes some colors of nvg view
	 */
	private fun nvgViewColorFix() {
		val toolsSection = nvg_homeActivity_nav.menu.findItem(R.id.group_nvg_drawer_tools)
		val spannableString = SpannableString(toolsSection.title)
		
		spannableString.setSpan(TextAppearanceSpan(this, R.style.GroupTitleTextAppearance), 0, spannableString.length, 0)
		
		toolsSection.title = spannableString
	}
	
	
	/**
	 * claims and shows the user nickname in the navigation drawer gotten from database
	 */
	private fun showUserNickName() {
		val cursor = database.getRowBy(Database.TBL_SETTINGS, "name", "nick_name")
		nvg_homeActivity_nav.getHeaderView(0).nvg_headerView_textView.text = cursor.getString(1)
	}
	
	/**
	 * handles on nvg view item click
	 */
	private fun handleNvgItemsClick(item: MenuItem): Boolean {
		when (item.itemId) {
			R.id.item_nvg_drawer_home -> {
				showHome()
				setPageTitle(resources.getString(R.string.app_name))
			}
			
			R.id.item_nvg_drawer_settings -> {
				showFragment(appSettings)
				setPageTitle(resources.getString(R.string.drawer_string_settings))
			}
			
			R.id.item_nvg_drawer_comments -> {
				showCommentsFragment()
			}
			
			R.id.item_nvg_drawer_addContent -> {
				showSendContentFragment()
			}
			
			R.id.item_nvg_drawer_contactUs -> {
				showFragment(contactUs)
				setPageTitle(resources.getString(R.string.drawer_string_contact_us))
			}
			
			R.id.item_nvg_drawer_aboutUs -> {
				showFragment(aboutUs)
				setPageTitle(resources.getString(R.string.drawer_string_about_us))
			}
			
			R.id.item_nvg_drawer_suggest -> {
				suggestToFriends()
			}
			
			R.id.item_nvg_drawer_downloadContent -> {
				downloadContent()
			}
			
			R.id.item_nvg_drawer_repairContent -> {
				repairContent()
			}
		}
		
		drl_homePageActivity_container.closeDrawer(GravityCompat.START)
		
		return true
	}
	
	/**
	 * changes the page title
	 *
	 * @param title the new title
	 */
	private fun setPageTitle(title: String) {
		tv_homePageActivity_titleTv.text = title
	}
	
	/**
	 * shows the selected fragment
	 *
	 * @param fragment the fragment to show
	 */
	private fun showFragment(fragment: Fragment) {
		val fragmentManager = supportFragmentManager
		val fragmentTransaction = fragmentManager.beginTransaction()
		
		fragmentManager.popBackStackImmediate()
		
		fragmentTransaction.replace(R.id.fr_homePageActivity_frgContainer, fragment)
		fragmentTransaction.addToBackStack("$fragment")
		fragmentTransaction.commit()
	}
	
	/**
	 * pops back stack to home page
	 */
	private fun showHome() {
		supportFragmentManager.popBackStackImmediate()
	}
	
	/**
	 * checks internet connection first
	 * then checks sending comment availability
	 * then shows the fragment
	 */
	private fun showCommentsFragment() {
		val d = util.makeProgressDialog(getString(R.string.download_volley_string_check_connection), false)
		if (!ContentManager.isConnected(this)) {
			d.dismiss()
			Toast.makeText(this, getString(R.string.string_please_connect), Toast.LENGTH_SHORT).show()
			Handler(Looper.getMainLooper()).post { nvg_homeActivity_nav.menu.getItem(0).isChecked = true }
			showHome()
			return
		}
		
		d.setMessage(getString(R.string.home_page_string_check_status))
		util.getBooleanFromServer(Url.let { it.OPTIONS + it.SEND_COMMENT }, {
			if (!it) {
				Toast.makeText(this, getString(R.string.home_page_string_comment_not_possible), Toast.LENGTH_LONG).show()
				nvg_homeActivity_nav.menu.getItem(0).isChecked = true
				showHome()
				d.dismiss()
				return@getBooleanFromServer
			}
			
			d.dismiss()
			showFragment(comments)
			setPageTitle(resources.getString(R.string.drawer_string_comments))
		}, {
			Toast.makeText(this, getString(R.string.home_page_string_error_get_from_server), Toast.LENGTH_SHORT).show()
			showHome()
			d.dismiss()
		})
	}
	
	/**
	 * checks internet connection first
	 * then checks sending content availability
	 * then shows the fragment
	 */
	private fun showSendContentFragment() {
		val d = util.makeProgressDialog(getString(R.string.download_volley_string_check_connection), false)
		if (!ContentManager.isConnected(this)) {
			d.dismiss()
			Toast.makeText(this, getString(R.string.string_please_connect), Toast.LENGTH_SHORT).show()
			Handler(Looper.getMainLooper()).post { nvg_homeActivity_nav.menu.getItem(0).isChecked = true }
			showHome()
			return
		}
		
		d.setMessage(getString(R.string.home_page_string_check_status))
		util.getBooleanFromServer(Url.let { it.OPTIONS + it.SEND_CONTENT }, {
			if (!it) {
				Toast.makeText(this, getString(R.string.home_page_string_add_content_not_possible), Toast.LENGTH_LONG).show()
				nvg_homeActivity_nav.menu.getItem(0).isChecked = true
				showHome()
				d.dismiss()
				return@getBooleanFromServer
			}
			
			d.dismiss()
			showFragment(sendContent)
			setPageTitle(resources.getString(R.string.drawer_string_add_content))
		}, {
			Toast.makeText(this, getString(R.string.home_page_string_error_get_from_server), Toast.LENGTH_SHORT).show()
			showHome()
			d.dismiss()
		})
	}
	
	/**
	 * checks internet connection
	 * then prompts user to accept the agreements
	 * then clears all data
	 * then downloads them all again
	 */
	private fun repairContent() {
		val d = util.makeProgressDialog(getString(R.string.download_volley_string_check_connection), false)
		if (!ContentManager.isConnected(this)) {
			d.dismiss()
			Toast.makeText(this, getString(R.string.string_please_connect), Toast.LENGTH_SHORT).show()
			return
		}
		
		util.makeAlertDialog(getString(R.string.drawer_string_repair_content), getString(R.string.sure_to_repair_content), false, {
			d.dismiss()
			
			database.clearTable(Database.TBL_BIOGRAPHY)
			database.clearTable(Database.TBL_TESTAMENT)
			database.clearTable(Database.TBL_OPERATION)
			
			database.resetColumnValues(Database.TBL_SCROLL, "ext_id")
			database.resetColumnValues(Database.TBL_SCROLL, "scroll")
			
			ContentManager(this).checkContents {
				it.download({
					Toast.makeText(this, getString(R.string.string_please_connect), Toast.LENGTH_SHORT).show()
				}, {
					Toast.makeText(this, getString(R.string.home_page_download_done), Toast.LENGTH_SHORT).show()
				})
			}
		}, { d.dismiss() })
	}
	
	/**
	 * checks internet connection
	 * then prompts user to accept the agreement
	 * on user confirm downloads new contents
	 */
	private fun downloadContent() {
		val d = util.makeProgressDialog(getString(R.string.download_volley_string_check_connection), false)
		if (!ContentManager.isConnected(this)) {
			d.dismiss()
			Toast.makeText(this, getString(R.string.string_please_connect), Toast.LENGTH_SHORT).show()
			return
		}
		
		util.makeAlertDialog(getString(R.string.drawer_string_download_content), getString(R.string.sure_to_download_content), false, {
			d.dismiss()
			
			NewContent(this).download({
				Toast.makeText(this, getString(R.string.string_please_connect), Toast.LENGTH_SHORT).show()
			}, {
				Toast.makeText(this, getString(R.string.home_page_download_done), Toast.LENGTH_SHORT).show()
			})
		}, { d.dismiss() })
	}
	
	/**
	 * shares the download link of the app
	 */
	private fun suggestToFriends() {
		val shareIntent = Intent(Intent.ACTION_SEND)
		shareIntent.type = "text/plain"
		shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
		shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.suggest_string_share_text) + Url.DOWNLOAD)
		startActivity(Intent.createChooser(shareIntent, getString(R.string.suggest_string_share_with)))
	}
	
	override fun onBackPressed() {
		if (drl_homePageActivity_container.isDrawerOpen(GravityCompat.START))
			drl_homePageActivity_container.closeDrawer(GravityCompat.START)
		else super.onBackPressed()
		
		/* change the nvg drawer selected item to home after back press */
		if (supportFragmentManager.backStackEntryCount == 0) {
			setPageTitle(resources.getString(R.string.app_name))
			nvg_homeActivity_nav.menu.getItem(0).isChecked = true
		}
	}
}