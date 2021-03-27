package ir.androidDev.homeLandDefenders.adapters

import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.databinding.BindingAdapter

object BindingAdapter {
	
	/**
	 * parses html and sets as the textView text
	 *
	 * @param textView
	 * @param html the html text to be parsed
	 */
	@BindingAdapter("app:htmlText")
	@JvmStatic
	fun setHtmlText(textView: TextView, html: String) {
		textView.text = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_COMPACT)
	}
	
	/**
	 * enables or disables the LinkMovementMethod to the given textView
	 *
	 * @param textView
	 * @param boolean whether to enable or disable the LinkMovementMethod
	 */
	@BindingAdapter("app:enableLinkMovementMethod")
	@JvmStatic
	fun enableLinkMovementMethod(textView: TextView, boolean: Boolean) {
		textView.movementMethod = if (boolean) LinkMovementMethod.getInstance() else null
	}
}