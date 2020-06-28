package jp.nita.tapchord

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.*
import android.widget.ImageView.ScaleType
import java.util.*

class MainActivity : Activity() {
    var mNeverShowAlphaReleased = 0
    private var mHeart: Heart? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        updatePreferences()
        volumeControlStream = AudioManager.STREAM_MUSIC
        mHeart = Heart()
        mHeart!!.start()
        if (mNeverShowAlphaReleased <= 0) {
            showAlphaVersionInformationDialog()
        }
    }

    fun showAlphaVersionInformationDialog() {
        val messageTextView = TextView(this)
        messageTextView.setTextAppearance(this, android.R.style.TextAppearance_Inverse)
        messageTextView.text = getString(R.string.version_201_alpha_released_message)
        val cautionTextView = TextView(this)
        cautionTextView.setTextAppearance(this, android.R.style.TextAppearance_Inverse)
        cautionTextView.text = getString(R.string.version_201_alpha_released_caution)
        val locale = Locale.getDefault().language
        val betaImage = ImageView(this)
        if (locale == "ja") {
            betaImage.setImageDrawable(resources.getDrawable(R.drawable.beta_ja))
        } else {
            betaImage.setImageDrawable(resources.getDrawable(R.drawable.beta_en))
        }
        betaImage.setPadding(resources.getDimensionPixelSize(R.dimen.beta_image_padding),
                resources.getDimensionPixelSize(R.dimen.beta_image_padding),
                resources.getDimensionPixelSize(R.dimen.beta_image_padding),
                resources.getDimensionPixelSize(R.dimen.beta_image_padding))
        betaImage.scaleType = ScaleType.FIT_XY
        betaImage.setAdjustViewBounds(true)
        betaImage.setMaxWidth(resources.getDimensionPixelSize(R.dimen.beta_image_width_max))
        betaImage.setMaxHeight(resources.getDimensionPixelSize(R.dimen.beta_image_width_max))
        val neverShowAgainCheckBox = CheckBox(this)
        neverShowAgainCheckBox.setTextAppearance(this, android.R.style.TextAppearance_Inverse)
        neverShowAgainCheckBox.setTextColor(neverShowAgainCheckBox.textColors.defaultColor)
        neverShowAgainCheckBox.text = getString(R.string.never_show_again)
        val linearLayout = LinearLayout(this)
        linearLayout.setPadding(resources.getDimensionPixelSize(R.dimen.beta_dialog_padding),
                resources.getDimensionPixelSize(R.dimen.beta_dialog_padding),
                resources.getDimensionPixelSize(R.dimen.beta_dialog_padding),
                resources.getDimensionPixelSize(R.dimen.beta_dialog_padding))
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.addView(messageTextView)
        linearLayout.addView(betaImage)
        linearLayout.addView(cautionTextView)
        linearLayout.addView(neverShowAgainCheckBox)
        val scrollView = ScrollView(this)
        scrollView.addView(linearLayout)
        val finalActivity = this
        val dialog = AlertDialog.Builder(this)
                .setTitle(getString(R.string.version_201_alpha_released_title))
                .setIcon(android.R.drawable.ic_dialog_info).setView(scrollView)
                .setPositiveButton(getString(R.string.remind_me_later)) { dialog, which ->
                    if (neverShowAgainCheckBox.isChecked) {
                        Statics.setPreferenceValue(finalActivity, Statics.PREF_NEVER_SHOW_ALPHA_RELEASED, 1)
                    }
                }.setNeutralButton(getString(R.string.go_to_google_play)) { dialog, which ->
                    if (neverShowAgainCheckBox.isChecked) {
                        Statics.setPreferenceValue(finalActivity, Statics.PREF_NEVER_SHOW_ALPHA_RELEASED, 1)
                    }
                    val uri = Uri.parse("https://play.google.com/store/apps/details?id=jp.nita.tapchord")
                    val i = Intent(Intent.ACTION_VIEW, uri)
                    startActivity(i)
                }.create()
        dialog.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        dialog.show()
    }

    override fun onPause() {
        super.onPause()
        (findViewById(R.id.tapChordView) as TapChordView).activityPaused(this)
        mHeart!!.sleep()
    }

    override fun onResume() {
        super.onResume()
        (findViewById(R.id.tapChordView) as TapChordView).activityResumed(this)
        mHeart!!.wake()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.action_quit -> AlertDialog.Builder(this).setTitle(getString(R.string.action_quit))
                    .setMessage(getString(R.string.message_quit))
                    .setPositiveButton(getString(R.string.ok)) { dialog, which -> finish() }.setNegativeButton(getString(R.string.cancel)) { dialog, which -> }.show()
        }
        return false
    }

    internal inner class Heart : Thread(), Runnable {
        private var awake = true
        private var alive = true
        override fun run() {
            val view = findViewById(R.id.tapChordView) as TapChordView
            while (alive) {
                try {
                    sleep(heartBeatInterval.toLong())
                    if (awake) view.heartbeat(heartBeatInterval)
                } catch (e: InterruptedException) {
                    die()
                }
            }
        }

        fun wake() {
            awake = true
        }

        fun sleep() {
            awake = false
        }

        fun die() {
            alive = false
        }
    }

    fun updatePreferences() {
        val animationQuality = Statics.preferenceValue(this, Statics.PREF_ANIMATION_QUALITY, 0)
        setAnimationQuality(animationQuality)
        mNeverShowAlphaReleased = Statics.preferenceValue(this, Statics.PREF_NEVER_SHOW_ALPHA_RELEASED, 0)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AlertDialog.Builder(this).setTitle(getString(R.string.action_quit))
                    .setMessage(getString(R.string.message_quit))
                    .setPositiveButton(getString(R.string.ok)) { dialog, which -> finish() }.setNegativeButton(getString(R.string.cancel)) { dialog, which -> }.show()
            // } else if (keyCode == KeyEvent.KEYCODE_CAMERA || keyCode ==
            // KeyEvent.KEYCODE_BACKSLASH) {
            // TapChordView.debugMode = !TapChordView.debugMode;
            // ((TapChordView) findViewById(R.id.tapChordView)).invalidate();
        } else {
            val result: Boolean
            result = (findViewById(R.id.tapChordView) as TapChordView).keyPressed(keyCode, event)
            if (!result) {
                return super.onKeyDown(keyCode, event)
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        val result: Boolean
        result = (findViewById(R.id.tapChordView) as TapChordView).keyReleased(keyCode, event)
        return super.onKeyUp(keyCode, event)
    }

    override fun onKeyLongPress(keyCode: Int, event: KeyEvent): Boolean {
        val result: Boolean
        result = (findViewById(R.id.tapChordView) as TapChordView).keyLongPressed(keyCode, event)
        return super.onKeyLongPress(keyCode, event)
    }

    companion object {
        @JvmField
        var heartBeatInterval = 5
        @JvmStatic
        fun setAnimationQuality(aq: Int) {
            heartBeatInterval = when (aq) {
                -1 -> 25
                1 -> 1
                else -> 5
            }
        }
    }
}