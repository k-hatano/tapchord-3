package jp.nita.tapchord.Activity

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import android.widget.SeekBar.OnSeekBarChangeListener
import jp.nita.tapchord.R
import jp.nita.tapchord.Util.Statics
import java.util.*
import kotlin.collections.HashMap

class SettingsActivity : Activity(), View.OnClickListener, OnItemClickListener {
    private var mDarken = 0
    private var mScale = 0
    private var mVolume = 0
    private var mSamplingRate = 0
    private var mWaveform = 0
    private var mVibration = 0
    private var mSoundRange = 0
    private var mEnableEnvelope = 0
    private var mAttackTime = 0
    private var mDecayTime = 0
    private var mSustainTime = 0
    private var mReleaseTime = 0
    private var mAnimationQuality = 0
    private var mNeverShowAlphaReleased = 0
    private var mPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updatePreferenceValues()
        setTheme(android.R.style.Theme_Light)
        setContentView(R.layout.activity_settings)
        val button: Button
        button = findViewById(R.id.settings_ok) as Button
        button.setOnClickListener(this)
    }

    fun updatePreferenceValues() {
        mScale = Statics.prefValue(this, Statics.PREF_SCALE, 0)
        mDarken = Statics.prefValue(this, Statics.PREF_DARKEN, 0)
        mVibration = Statics.prefValue(this, Statics.PREF_VIBRATION, 1)
        mVolume = Statics.prefValue(this, Statics.PREF_VOLUME, 30)
        mSamplingRate = Statics.prefValue(this, Statics.PREF_SAMPLING_RATE, 0)
        mWaveform = Statics.prefValue(this, Statics.PREF_WAVEFORM, 0)
        mSoundRange = Statics.prefValue(this, Statics.PREF_SOUND_RANGE, 0)
        mEnableEnvelope = Statics.prefValue(this, Statics.PREF_ENABLE_ENVELOPE, 0)
        mAttackTime = Statics.prefValue(this, Statics.PREF_ATTACK_TIME, 0)
        mDecayTime = Statics.prefValue(this, Statics.PREF_DECAY_TIME, 0)
        mSustainTime = Statics.prefValue(this, Statics.PREF_SUSTAIN_LEVEL, 0)
        mReleaseTime = Statics.prefValue(this, Statics.PREF_RELEASE_TIME, 0)
        mAnimationQuality = Statics.prefValue(this, Statics.PREF_ANIMATION_QUALITY, 0)
        mNeverShowAlphaReleased = Statics.prefValue(this, Statics.PREF_NEVER_SHOW_ALPHA_RELEASED, 0)
    }

    fun updateSettingsListView() {
        val items = findViewById(R.id.settings_items) as ListView
        val list: MutableList<Map<String, String?>> = ArrayList()
        run {
            var map: MutableMap<String, String>
            list.add(makeKeyValueHash(getString(R.string.settings_scale),
                    Statics.longStringOfScale(mScale)))
            list.add(makeKeyValueHash(getString(R.string.settings_darken),
                    Statics.onOrOffString(this, mDarken)))
            list.add(makeKeyValueHash(getString(R.string.settings_vibration),
                    Statics.onOrOffString(this, mVibration)))
            list.add(makeKeyValueHash(getString(R.string.settings_volume),
                    "" + Statics.valueOfVolume(mVolume)))
            list.add(makeKeyValueHash(getString(R.string.settings_sound_range),
                    Statics.stringOfSoundRange(mSoundRange)))
            list.add(makeKeyValueHash(getString(R.string.settings_waveform),
                    Statics.valueOfWaveform(mWaveform, this)))

            /*
             * map=new HashMap<String,String>(); map.put("key",
             * getString(R.string.settings_envelope)); map.put("value",
             * ""+Statics.getStringOfEnvelope(enableEnvelope,attackTime,
             * decayTime,sustainLevel,releaseTime,this)); list.add(map);
             */

            list.add(makeKeyValueHash(getString(R.string.settings_sampling_rate),
                    ("" + Statics.valueOfSamplingRate(mSamplingRate) + " " + getString(R.string.settings_sampling_rate_hz))))
            list.add(makeKeyValueHash(getString(R.string.settings_animation_quality),
                    Statics.stringOfAnimationQuality(mAnimationQuality, this)))
        }
        val adapter = SimpleAdapter(this, list, android.R.layout.simple_expandable_list_item_2, arrayOf("key", "value"), intArrayOf(android.R.id.text1, android.R.id.text2))
        items.adapter = adapter
        items.onItemClickListener = this
    }

    fun makeKeyValueHash(key: String, value: String): MutableMap<String, String> {
        var map = HashMap<String, String>()
        map["key"] = key
        map["value"] = value
        return map
    }

    public override fun onResume() {
        super.onResume()
        updatePreferenceValues()
        updateSettingsListView()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.settings, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_ok) {
            finish()
            return true
        }
        return false
    }

    override fun onClick(view: View) {
        if (view.id == R.id.settings_ok) {
            finish()
        }
    }

    override fun onItemClick(adapterView: AdapterView<*>, view: View, which: Int, arg3: Long) {
        mPosition = adapterView.firstVisiblePosition
        when (which) {
            0 -> {
                val list: Array<String?> = arrayOfNulls<String>(15)
                var i = -7
                while (i <= 7) {
                    list[i + 7] = Statics.longStringOfScale(i)
                    i++
                }
                AlertDialog.Builder(this@SettingsActivity).setTitle(getString(R.string.settings_scale))
                        .setSingleChoiceItems(list, mScale + 7) { dialog, which ->
                            setScale(which - 7)
                            dialog.dismiss()
                            (findViewById(R.id.settings_items) as ListView).setSelection(mPosition)
                        }.show()
            }
            1 -> {
                val list: Array<String?> = arrayOfNulls<String>(2)
                list[0] = getString(R.string.off)
                list[1] = getString(R.string.on)
                AlertDialog.Builder(this@SettingsActivity).setTitle(getString(R.string.settings_darken))
                        .setSingleChoiceItems(list, mDarken) { dialog, which ->
                            if (which != mDarken) {
                                setDarken(which)
                                finish()
                            }
                            dialog.dismiss()
                            (findViewById(R.id.settings_items) as ListView).setSelection(mPosition)
                        }.show()
            }
            2 -> {
                val list: Array<String?> = arrayOfNulls<String>(2)
                list[0] = getString(R.string.off)
                list[1] = getString(R.string.on)
                AlertDialog.Builder(this@SettingsActivity).setTitle(getString(R.string.settings_vibration))
                        .setSingleChoiceItems(list, mVibration) { dialog, which ->
                            setVibration(which)
                            dialog.dismiss()
                            (findViewById(R.id.settings_items) as ListView).setSelection(mPosition)
                        }.show()
            }
            3 -> {
                val vol = mVolume + 50
                val volumeView = TextView(this)
                volumeView.text = "" + vol
                volumeView.setTextAppearance(this, android.R.style.TextAppearance_Inverse)
                val seekBar = SeekBar(this)
                seekBar.progress = vol
                seekBar.max = 100
                seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                        volumeView.text = "" + progress
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar) {}
                    override fun onStopTrackingTouch(seekBar: SeekBar) {}
                })
                val layout = LinearLayout(this)
                layout.orientation = LinearLayout.VERTICAL
                layout.addView(volumeView)
                layout.addView(seekBar)
                layout.setPadding(8, 8, 8, 8)
                AlertDialog.Builder(this@SettingsActivity).setTitle(getString(R.string.settings_volume)).setView(layout)
                        .setPositiveButton(getString(R.string.ok)) { dialog, which ->
                            setVolume(seekBar.progress - 50)
                            (findViewById(R.id.settings_items) as ListView).setSelection(mPosition)
                        }.setNegativeButton(getString(R.string.cancel)) { dialog, which -> (findViewById(R.id.settings_items) as ListView).setSelection(mPosition) }.show()
            }
            4 -> {
                val rangeView = TextView(this)
                rangeView.text = "" + Statics.stringOfSoundRange(mSoundRange)
                rangeView.setTextAppearance(this, android.R.style.TextAppearance_Inverse)
                val seekBar = SeekBar(this)
                seekBar.progress = mSoundRange + 24
                seekBar.max = 48
                seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                        rangeView.text = "" + Statics.stringOfSoundRange(seekBar.progress - 24)
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar) {}
                    override fun onStopTrackingTouch(seekBar: SeekBar) {}
                })
                val layout = LinearLayout(this)
                layout.orientation = LinearLayout.VERTICAL
                layout.addView(rangeView)
                layout.addView(seekBar)
                layout.setPadding(8, 8, 8, 8)
                AlertDialog.Builder(this@SettingsActivity).setTitle(getString(R.string.settings_sound_range))
                        .setView(layout).setPositiveButton(getString(R.string.ok)) { dialog, which ->
                            setSoundRange(seekBar.progress - 24)
                            (findViewById(R.id.settings_items) as ListView).setSelection(mPosition)
                        }.setNegativeButton(getString(R.string.cancel)) { dialog, which -> (findViewById(R.id.settings_items) as ListView).setSelection(mPosition) }.show()
            }
            5 -> {
                val list: Array<String?> = arrayOfNulls<String>(7)
                var i = 0
                while (i < list.size) {
                    list[i] = Statics.valueOfWaveform(i, this)
                    i++
                }
                AlertDialog.Builder(this@SettingsActivity).setTitle(getString(R.string.settings_waveform))
                        .setSingleChoiceItems(list, mWaveform) { dialog, which ->
                            setWaveform(which)
                            dialog.dismiss()
                            (findViewById(R.id.settings_items) as ListView).setSelection(mPosition)
                        }.show()
            }
            6 -> {
                val list: Array<String?> = arrayOfNulls<String>(4)
                var i = 0
                while (i < 4) {
                    list[i] = ("" + Statics.valueOfSamplingRate(i - 3) + " "
                            + getString(R.string.settings_sampling_rate_hz))
                    i++
                }
                AlertDialog.Builder(this@SettingsActivity).setTitle(getString(R.string.settings_sampling_rate))
                        .setSingleChoiceItems(list, mSamplingRate + 3) { dialog, which ->
                            setSamplingRate(which - 3)
                            dialog.dismiss()
                            (findViewById(R.id.settings_items) as ListView).setSelection(mPosition)
                        }.show()
            }
            7 -> {
                val list: Array<String?> = arrayOfNulls<String>(3)
                var i = 0
                while (i < 3) {
                    list[i] = "" + Statics.stringOfAnimationQuality(i - 1, this@SettingsActivity)
                    i++
                }
                AlertDialog.Builder(this@SettingsActivity).setTitle(getString(R.string.settings_animation_quality))
                        .setSingleChoiceItems(list, mAnimationQuality + 1) { dialog, which ->
                            setAnimationQuality(which - 1)
                            dialog.dismiss()
                            (findViewById(R.id.settings_items) as ListView).setSelection(mPosition)
                        }.show()
            }
            else -> {
            }
        }
    }

    fun setScale(scale: Int) {
        mScale = scale
        Statics.setPrefValue(this, Statics.PREF_SCALE, mScale)
        updatePreferenceValues()
        updateSettingsListView()
    }

    fun setDarken(darken: Int) {
        mDarken = darken
        Statics.setPrefValue(this, Statics.PREF_DARKEN, mDarken)
        updatePreferenceValues()
        updateSettingsListView()
    }

    fun setVibration(vibration: Int) {
        mVibration = vibration
        Statics.setPrefValue(this, Statics.PREF_VIBRATION, mVibration)
        updatePreferenceValues()
        updateSettingsListView()
    }

    fun setSamplingRate(samplingRate: Int) {
        mSamplingRate = samplingRate
        Statics.setPrefValue(this, Statics.PREF_SAMPLING_RATE, mSamplingRate)
        updatePreferenceValues()
        updateSettingsListView()
    }

    fun setVolume(volume: Int) {
        mVolume = volume
        Statics.setPrefValue(this, Statics.PREF_VOLUME, mVolume)
        updatePreferenceValues()
        updateSettingsListView()
    }

    fun setSoundRange(soundRange: Int) {
        mSoundRange = soundRange
        Statics.setPrefValue(this, Statics.PREF_SOUND_RANGE, soundRange)
        updatePreferenceValues()
        updateSettingsListView()
    }

    fun setAttackTime(attackTime: Int) {
        mAttackTime = attackTime
        Statics.setPrefValue(this, Statics.PREF_ATTACK_TIME, attackTime)
        updatePreferenceValues()
        updateSettingsListView()
    }

    fun setDecayTime(decayTime: Int) {
        mDecayTime = decayTime
        Statics.setPrefValue(this, Statics.PREF_DECAY_TIME, decayTime)
        updatePreferenceValues()
        updateSettingsListView()
    }

    fun setSustainLevel(sustainLevel: Int) {
        mSustainTime = sustainLevel
        Statics.setPrefValue(this, Statics.PREF_SUSTAIN_LEVEL, sustainLevel - 100)
        updatePreferenceValues()
        updateSettingsListView()
    }

    fun setReleaseTime(releaseTime: Int) {
        mReleaseTime = releaseTime
        Statics.setPrefValue(this, Statics.PREF_RELEASE_TIME, releaseTime)
        updatePreferenceValues()
        updateSettingsListView()
    }

    fun setWaveform(waveform: Int) {
        mWaveform = waveform
        Statics.setPrefValue(this, Statics.PREF_WAVEFORM, mWaveform)
        updatePreferenceValues()
        updateSettingsListView()
    }

    fun setEnableEnvelope(enableEnvelope: Int) {
        mEnableEnvelope = enableEnvelope
        Statics.setPrefValue(this, Statics.PREF_ENABLE_ENVELOPE, mEnableEnvelope)
        updatePreferenceValues()
        updateSettingsListView()
    }

    fun setAnimationQuality(animationQuality: Int) {
        mAnimationQuality = animationQuality
        Statics.setPrefValue(this, Statics.PREF_ANIMATION_QUALITY, mAnimationQuality)
        MainActivity.setAnimationQuality(animationQuality)
        updatePreferenceValues()
        updateSettingsListView()
    }

    fun setNeverShowAlphaReleased(neverShowAlphaReleased: Int) {
        mNeverShowAlphaReleased = neverShowAlphaReleased
        Statics.setPrefValue(this, Statics.PREF_NEVER_SHOW_ALPHA_RELEASED, mNeverShowAlphaReleased)
        updatePreferenceValues()
        updateSettingsListView()
    }
}