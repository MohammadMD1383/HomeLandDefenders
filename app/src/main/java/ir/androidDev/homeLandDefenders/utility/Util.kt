package ir.androidDev.homeLandDefenders.utility

import android.app.ProgressDialog
import android.content.Context
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.view.Gravity
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.android.volley.DefaultRetryPolicy
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import ir.androidDev.homeLandDefenders.R
import ir.androidDev.homeLandDefenders.contentManagement.ContentManager


class Util(private val context: Context) {
	/**
	 * makes a progress dialog and returns it
	 *
	 * @param message the message of dialog
	 * @param cancellable the cancellable status of dialog by click outside it
	 */
	fun makeProgressDialog(message: String, cancellable: Boolean): ProgressDialog {
		val pd = ProgressDialog(context)
		pd.setMessage(message)
		pd.setCancelable(cancellable)
		pd.show()
		return pd
	}
	
	/**
	 * makes a prompt dialog using alert dialog and shows it
	 * after "ok" clicked if the input is not empty the input value will be returned by a callback
	 * else a toast requires the user to fill input with something that is acceptable
	 *
	 * @param title the title of dialog
	 * @param message the message of dialog
	 * @param cancellable the cancellable status of dialog by click outside it
	 * @param maxInputLength the max input length that user can insert (max Chars)
	 * @param doWithGivenValue the call back that contains the input value
	 */
	fun makePromptDialog(title: String, message: String, cancellable: Boolean, maxInputLength: Int, doWithGivenValue: (s: String) -> Unit) {
		val alert: AlertDialog.Builder = AlertDialog.Builder(context)
		
		alert.setTitle(title)
		alert.setMessage(message)
		
		val input = EditText(context)
		input.gravity = Gravity.CENTER
		input.setLines(1)
		input.maxLines = 1
		input.isSingleLine = true
		input.filters = arrayOf<InputFilter>(LengthFilter(maxInputLength))
		alert.setView(input)
		
		alert.setPositiveButton(context.getString(R.string.util_dialog_positive_button), null)
		
		alert.setCancelable(cancellable)
		
		val a: AlertDialog = alert.create()
		a.show()
		
		a.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
			if (input.text.toString().isEmpty()) {
				Toast.makeText(context, context.getString(R.string.app_settings_string_please_fill_nick_name), Toast.LENGTH_SHORT).show()
				return@setOnClickListener
			}
			doWithGivenValue(input.text.toString())
			a.dismiss()
		}
	}
	
	/**
	 * makes an alert dialog with a positive button
	 * and calls a callback on positive button click [onDismiss]
	 *
	 * @param title the title of dialog
	 * @param message the message of dialog
	 * @param cancellable the cancellable status of dialog by click outside it
	 * @param onDismiss the callback
	 */
	fun makeAlertDialog(title: String, message: String, cancellable: Boolean, onDismiss: () -> Unit) {
		val alert: AlertDialog.Builder = AlertDialog.Builder(context)
		
		alert.setTitle(title)
		alert.setMessage(message)
		
		alert.setPositiveButton(context.getString(R.string.util_dialog_positive_button)) { _, _ -> onDismiss() }
		
		alert.setCancelable(cancellable)
		
		val a: AlertDialog = alert.create()
		a.show()
	}
	
	/**
	 * makes an alert dialog with positive and negative buttons
	 * and calls a callback on even positive or negative button click [onConfirm] AND [onDismiss]
	 *
	 * @param title the title of dialog
	 * @param message the message of dialog
	 * @param cancellable the cancellable status of dialog by click outside it
	 * @param onDismiss the callback called on negative button click
	 * @param onConfirm the callback called on positive button click
	 */
	fun makeAlertDialog(title: String, message: String, cancellable: Boolean, onConfirm: () -> Unit, onDismiss: () -> Unit) {
		val alert: AlertDialog.Builder = AlertDialog.Builder(context)
		
		alert.setTitle(title)
		alert.setMessage(message)
		
		alert.setPositiveButton(context.getString(R.string.util_dialog_positive_button)) { _, _ -> onConfirm() }
		alert.setNegativeButton(context.getString(R.string.util_dialog_negative_button)) { _, _ -> onDismiss() }
		
		alert.setCancelable(cancellable)
		
		val a: AlertDialog = alert.create()
		a.show()
	}
	
	/**
	 * sends a request and returns a boolean that gotten from server on a callback [onResponse]
	 *
	 * @param url the url to fetch data
	 * @param onResponse the callback called after getting response and parsing it into boolean
	 * @param onError the callback called after happening an error due to volley errors
	 */
	fun getBooleanFromServer(url: String, onResponse: (b: Boolean) -> Unit, onError: () -> Unit) {
		val request = object : StringRequest(url, { onResponse(it.toBoolean()) }, { onError() }) {
			override fun getHeaders(): MutableMap<String, String> {
				return ContentManager.getCookie(context)
			}
		}
		request.retryPolicy = DefaultRetryPolicy(20000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
		Volley.newRequestQueue(context).add(request)
	}
}