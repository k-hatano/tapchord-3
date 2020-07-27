package jp.nita.tapchord.Activity

import android.app.Activity
import android.content.Intent
import android.media.AudioManager
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import jp.nita.tapchord.R
import jp.nita.tapchord.Util.Dialogs.dialogBuilder
import jp.nita.tapchord.Util.PREF_ANIMATION_QUALITY
import jp.nita.tapchord.Util.PREF_NEVER_SHOW_ALPHA_RELEASED
import jp.nita.tapchord.Util.prefValue
import jp.nita.tapchord.View.TapChordView

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
            R.id.action_quit -> {
                dialogBuilder(this, R.string.action_quit, R.string.message_quit)
                        .setPositiveButton(getString(R.string.ok)) { dialog, which -> finish() }
                        .setNegativeButton(getString(R.string.cancel)) { dialog, which -> }
                        .show()
            }

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
        val animationQuality = prefValue(this, PREF_ANIMATION_QUALITY, 0)
        setAnimationQuality(animationQuality)
        mNeverShowAlphaReleased = prefValue(this, PREF_NEVER_SHOW_ALPHA_RELEASED, 0)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            dialogBuilder(this, R.string.action_quit, R.string.message_quit)
                    .setPositiveButton(getString(R.string.ok)) { dialog, which -> finish() }
                    .setNegativeButton(getString(R.string.cancel)) { dialog, which -> }.show()

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
        return false
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        (findViewById(R.id.tapChordView) as TapChordView).keyReleased(keyCode, event)
        return super.onKeyUp(keyCode, event)
    }

    override fun onKeyLongPress(keyCode: Int, event: KeyEvent): Boolean {
        (findViewById(R.id.tapChordView) as TapChordView).keyLongPressed(keyCode, event)
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