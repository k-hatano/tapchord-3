package jp.nita.tapchord.Activity

import android.app.Activity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import android.widget.SeekBar.OnSeekBarChangeListener
import jp.nita.tapchord.R
import jp.nita.tapchord.Util.Dialogs.dialogBuilder
import jp.nita.tapchord.Util.PREF_ANIMATION_QUALITY
import jp.nita.tapchord.Util.PREF_ATTACK_TIME
import jp.nita.tapchord.Util.PREF_DARKEN
import jp.nita.tapchord.Util.PREF_DECAY_TIME
import jp.nita.tapchord.Util.PREF_ENABLE_ENVELOPE
import jp.nita.tapchord.Util.PREF_NEVER_SHOW_ALPHA_RELEASED
import jp.nita.tapchord.Util.PREF_RELEASE_TIME
import jp.nita.tapchord.Util.PREF_SAMPLING_RATE
import jp.nita.tapchord.Util.PREF_SCALE
import jp.nita.tapchord.Util.PREF_SOUND_RANGE
import jp.nita.tapchord.Util.PREF_SUSTAIN_LEVEL
import jp.nita.tapchord.Util.PREF_VIBRATION
import jp.nita.tapchord.Util.PREF_VOLUME
import jp.nita.tapchord.Util.PREF_WAVEFORM
import jp.nita.tapchord.Util.longStringOfScale
import jp.nita.tapchord.Util.onOrOffString
import jp.nita.tapchord.Util.prefValue
import jp.nita.tapchord.Util.setPrefValue
import jp.nita.tapchord.Util.stringOfAnimationQuality
import jp.nita.tapchord.Util.stringOfSoundRange
import jp.nita.tapchord.Util.valueOfSamplingRate
import jp.nita.tapchord.Util.valueOfVolume
import jp.nita.tapchord.Util.valueOfWaveform
import java.util.*

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
        mScale = prefValue(this, PREF_SCALE, 0)
        mDarken = prefValue(this, PREF_DARKEN, 0)
        mVibration = prefValue(this, PREF_VIBRATION, 1)
        mVolume = prefValue(this, PREF_VOLUME, 30)
        mSamplingRate = prefValue(this, PREF_SAMPLING_RATE, 0)
        mWaveform = prefValue(this, PREF_WAVEFORM, 0)
        mSoundRange = prefValue(this, PREF_SOUND_RANGE, 0)
        mEnableEnvelope = prefValue(this, PREF_ENABLE_ENVELOPE, 0)
        mAttackTime = prefValue(this, PREF_ATTACK_TIME, 0)
        mDecayTime = prefValue(this, PREF_DECAY_TIME, 0)
        mSustainTime = prefValue(this, PREF_SUSTAIN_LEVEL, 0)
        mReleaseTime = prefValue(this, PREF_RELEASE_TIME, 0)
        mAnimationQuality = prefValue(this, PREF_ANIMATION_QUALITY, 0)
        mNeverShowAlphaReleased = prefValue(this, PREF_NEVER_SHOW_ALPHA_RELEASED, 0)
    }

    fun updateSettingsListView() {
        val items = findViewById(R.id.settings_items) as ListView
        val list: MutableList<Map<String, String?>> = ArrayList()
        run {
            var map: MutableMap<String, String>
            list.add(keyValueHash(R.string.settings_scale, longStringOfScale(mScale)))
            list.add(keyValueHash(R.string.settings_darken, onOrOffString(this, mDarken)))
            list.add(keyValueHash(R.string.settings_vibration, onOrOffString(this, mVibration)))
            list.add(keyValueHash(R.string.settings_volume, "" + valueOfVolume(mVolume)))
            list.add(keyValueHash(R.string.settings_sound_range, stringOfSoundRange(mSoundRange)))
            list.add(keyValueHash(R.string.settings_waveform, valueOfWaveform(mWaveform, this)))

            /*
             * map=new HashMap<String,String>(); map.put("key",
             * getString(R.string.settings_envelope)); map.put("value",
             * ""+getStringOfEnvelope(enableEnvelope,attackTime,
             * decayTime,sustainLevel,releaseTime,this)); list.add(map);
             */

            list.add(keyValueHash(R.string.settings_sampling_rate, ("" + valueOfSamplingRate(mSamplingRate) + " " + getString(R.string.settings_sampling_rate_hz))))
            list.add(keyValueHash(R.string.settings_animation_quality, stringOfAnimationQuality(mAnimationQuality, this)))
        }
        val adapter = SimpleAdapter(this, list, android.R.layout.simple_expandable_list_item_2, arrayOf("key", "value"), intArrayOf(android.R.id.text1, android.R.id.text2))
        items.adapter = adapter
        items.onItemClickListener = this
    }

    private fun keyValueHash(keyId: Int, value: String): Map<String, String> {
        return mapOf("key" to getString(keyId), "value" to value)
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
        val finalSettingsItems = findViewById(R.id.settings_items) as ListView
        mPosition = adapterView.firstVisiblePosition
        when (which) {
            0 -> {
                val list: Array<String?> = arrayOfNulls<String>(15)
                var i = -7
                while (i <= 7) {
                    list[i + 7] = longStringOfScale(i)
                    i++
                }
                dialogBuilder(this, R.string.settings_scale)
                        .setSingleChoiceItems(list, mScale + 7) { dialog, which ->
                            setScale(which - 7)
                            dialog.dismiss()
                            finalSettingsItems.setSelection(mPosition)
                        }.show()
            }
            1 -> {
                val list: Array<String?> = arrayOf(getString(R.string.off), getString(R.string.on))

                dialogBuilder(this, R.string.settings_darken)
                        .setSingleChoiceItems(list, mDarken) { dialog, which ->
                            if (which != mDarken) {
                                setDarken(which)
                                finish()
                            }
                            dialog.dismiss()
                            finalSettingsItems.setSelection(mPosition)
                        }.show()
            }
            2 -> {
                val list: Array<String?> = arrayOf(getString(R.string.off), getString(R.string.on))

                dialogBuilder(this, R.string.settings_vibration)
                        .setSingleChoiceItems(list, mVibration) { dialog, which ->
                            setVibration(which)
                            dialog.dismiss()
                            finalSettingsItems.setSelection(mPosition)
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

                dialogBuilder(this, R.string.settings_volume)
                        .setPositiveButton(getString(R.string.ok)) { dialog, which ->
                            setVolume(seekBar.progress - 50)
                            finalSettingsItems.setSelection(mPosition)
                        }.setNegativeButton(getString(R.string.cancel)) { dialog, which -> finalSettingsItems.setSelection(mPosition) }.show()
            }
            4 -> {
                val rangeView = TextView(this)
                rangeView.text = "" + stringOfSoundRange(mSoundRange)
                rangeView.setTextAppearance(this, android.R.style.TextAppearance_Inverse)

                val seekBar = SeekBar(this)
                seekBar.progress = mSoundRange + 24
                seekBar.max = 48
                seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                        rangeView.text = "" + stringOfSoundRange(seekBar.progress - 24)
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar) {}
                    override fun onStopTrackingTouch(seekBar: SeekBar) {}
                })

                val layout = LinearLayout(this)
                layout.orientation = LinearLayout.VERTICAL
                layout.addView(rangeView)
                layout.addView(seekBar)
                layout.setPadding(8, 8, 8, 8)

                dialogBuilder(this, R.string.settings_sound_range)
                        .setView(layout).setPositiveButton(getString(R.string.ok)) { dialog, which ->
                            setSoundRange(seekBar.progress - 24)
                            finalSettingsItems.setSelection(mPosition)
                        }.setNegativeButton(getString(R.string.cancel)) { dialog, which -> finalSettingsItems.setSelection(mPosition) }.show()
            }
            5 -> {
                val list: Array<String?> = arrayOfNulls<String>(7)
                var i = 0
                while (i < list.size) {
                    list[i] = valueOfWaveform(i, this)
                    i++
                }

                dialogBuilder(this, R.string.settings_waveform)
                        .setSingleChoiceItems(list, mWaveform) { dialog, which ->
                            setWaveform(which)
                            dialog.dismiss()
                            finalSettingsItems.setSelection(mPosition)
                        }.show()
            }
            6 -> {
                val list: Array<String?> = arrayOfNulls<String>(4)
                var i = 0
                while (i < 4) {
                    list[i] = ("" + valueOfSamplingRate(i - 3) + " "
                            + getString(R.string.settings_sampling_rate_hz))
                    i++
                }
                dialogBuilder(this, R.string.settings_sampling_rate)
                        .setSingleChoiceItems(list, mSamplingRate + 3) { dialog, which ->
                            setSamplingRate(which - 3)
                            dialog.dismiss()
                            finalSettingsItems.setSelection(mPosition)
                        }.show()
            }
            7 -> {
                val list: Array<String?> = arrayOfNulls<String>(3)
                var i = 0
                while (i < 3) {
                    list[i] = "" + stringOfAnimationQuality(i - 1, this@SettingsActivity)
                    i++
                }
                dialogBuilder(this, R.string.settings_animation_quality)
                        .setSingleChoiceItems(list, mAnimationQuality + 1) { dialog, which ->
                            setAnimationQuality(which - 1)
                            dialog.dismiss()
                            finalSettingsItems.setSelection(mPosition)
                        }.show()
            }
            else -> {
            }
        }
    }

    fun setScale(scale: Int) {
        mScale = scale
        setPrefValue(this, PREF_SCALE, mScale)
        updatePreferenceValues()
        updateSettingsListView()
    }

    fun setDarken(darken: Int) {
        mDarken = darken
        setPrefValue(this, PREF_DARKEN, mDarken)
        updatePreferenceValues()
        updateSettingsListView()
    }

    fun setVibration(vibration: Int) {
        mVibration = vibration
        setPrefValue(this, PREF_VIBRATION, mVibration)
        updatePreferenceValues()
        updateSettingsListView()
    }

    fun setSamplingRate(samplingRate: Int) {
        mSamplingRate = samplingRate
        setPrefValue(this, PREF_SAMPLING_RATE, mSamplingRate)
        updatePreferenceValues()
        updateSettingsListView()
    }

    fun setVolume(volume: Int) {
        mVolume = volume
        setPrefValue(this, PREF_VOLUME, mVolume)
        updatePreferenceValues()
        updateSettingsListView()
    }

    fun setSoundRange(soundRange: Int) {
        mSoundRange = soundRange
        setPrefValue(this, PREF_SOUND_RANGE, soundRange)
        updatePreferenceValues()
        updateSettingsListView()
    }

    fun setAttackTime(attackTime: Int) {
        mAttackTime = attackTime
        setPrefValue(this, PREF_ATTACK_TIME, attackTime)
        updatePreferenceValues()
        updateSettingsListView()
    }

    fun setDecayTime(decayTime: Int) {
        mDecayTime = decayTime
        setPrefValue(this, PREF_DECAY_TIME, decayTime)
        updatePreferenceValues()
        updateSettingsListView()
    }

    fun setSustainLevel(sustainLevel: Int) {
        mSustainTime = sustainLevel
        setPrefValue(this, PREF_SUSTAIN_LEVEL, sustainLevel - 100)
        updatePreferenceValues()
        updateSettingsListView()
    }

    fun setReleaseTime(releaseTime: Int) {
        mReleaseTime = releaseTime
        setPrefValue(this, PREF_RELEASE_TIME, releaseTime)
        updatePreferenceValues()
        updateSettingsListView()
    }

    fun setWaveform(waveform: Int) {
        mWaveform = waveform
        setPrefValue(this, PREF_WAVEFORM, mWaveform)
        updatePreferenceValues()
        updateSettingsListView()
    }

    fun setEnableEnvelope(enableEnvelope: Int) {
        mEnableEnvelope = enableEnvelope
        setPrefValue(this, PREF_ENABLE_ENVELOPE, mEnableEnvelope)
        updatePreferenceValues()
        updateSettingsListView()
    }

    fun setAnimationQuality(animationQuality: Int) {
        mAnimationQuality = animationQuality
        setPrefValue(this, PREF_ANIMATION_QUALITY, mAnimationQuality)
        MainActivity.setAnimationQuality(animationQuality)
        updatePreferenceValues()
        updateSettingsListView()
    }

    fun setNeverShowAlphaReleased(neverShowAlphaReleased: Int) {
        mNeverShowAlphaReleased = neverShowAlphaReleased
        setPrefValue(this, PREF_NEVER_SHOW_ALPHA_RELEASED, mNeverShowAlphaReleased)
        updatePreferenceValues()
        updateSettingsListView()
    }
}