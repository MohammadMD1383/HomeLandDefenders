package ir.androidDev.homeLandDefenders.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import ir.androidDev.homeLandDefenders.R
import ir.androidDev.homeLandDefenders.connections.Url
import ir.androidDev.homeLandDefenders.contentManagement.ContentManager
import ir.androidDev.homeLandDefenders.database.Database
import ir.androidDev.homeLandDefenders.utility.Util
import kotlinx.android.synthetic.main.fragment_comments.*
import kotlinx.android.synthetic.main.fragment_comments.view.*

class CommentsFragment : Fragment() {
	
	private var rootView: View? = null
	
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		if (rootView == null) {
			rootView = inflater.inflate(R.layout.fragment_comments, container, false)
			
			/* set on send click listener */
			setOnSendClickListener()
		}
		return rootView
	}
	
	/**
	 * first checks the comment body if not null
	 * then checks connectivity
	 * then makes request and send it
	 */
	private fun setOnSendClickListener() {
		rootView!!.btn_commentsFragment_send.setOnClickListener {
			if (et_commentsFragment_comment.text.trim().isEmpty()) {
				Toast.makeText(context, getString(R.string.comments_fragment_please_fill_your_comment), Toast.LENGTH_SHORT).show()
				return@setOnClickListener
			}
			
			if (!ContentManager.isConnected(context!!)) {
				Toast.makeText(context!!, getString(R.string.string_please_connect), Toast.LENGTH_SHORT).show()
				return@setOnClickListener
			}
			
			val d = Util(context!!).makeProgressDialog(getString(R.string.comments_fragment_string_sending), false)
			
			val dataMap = createDataMap()
			
			val request = object : StringRequest(Method.POST, Url.COMMENT, {
				d.dismiss()
				Toast.makeText(context, getString(R.string.comments_fragment_string_comment_sent), Toast.LENGTH_SHORT).show()
				et_commentsFragment_comment.setText("")
			}, {
				d.dismiss()
				Toast.makeText(context!!, getString(R.string.comments_fragment_string_send_error), Toast.LENGTH_SHORT).show()
			}) {
				override fun getParams(): MutableMap<String, String> {
					return dataMap
				}
				
				@Throws(AuthFailureError::class)
				override fun getHeaders(): MutableMap<String, String> {
					val params: MutableMap<String, String> = HashMap()
					params["Content-Type"] = "application/x-www-form-urlencoded"
					params.putAll(ContentManager.getCookie(context!!))
					return params
				}
			}
			
			request.retryPolicy = DefaultRetryPolicy(20000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
			
			Volley.newRequestQueue(context!!).add(request)
		}
	}
	
	/**
	 * creates the data map for request body
	 *
	 * {e.g PHP} $_POST['comment'] = THE COMMENT
	 */
	private fun createDataMap(): MutableMap<String, String> {
		val userName = Database(context).getRowBy(Database.TBL_SETTINGS, "name", "nick_name").getString(1)
		
		val map: MutableMap<String, String> = HashMap()
		
		map["user"] = userName
		map["comment"] = rootView!!.et_commentsFragment_comment.text.toString().trim()
		
		return map
	}
}