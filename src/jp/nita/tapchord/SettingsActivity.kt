package jp.nita.tapchord

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import android.widget.SeekBar.OnSeekBarChangeListener
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
        mScale = Statics.preferenceValue(this, Statics.PREF_SCALE, 0)
        mDarken = Statics.preferenceValue(this, Statics.PREF_DARKEN, 0)
        mVibration = Statics.preferenceValue(this, Statics.PREF_VIBRATION, 1)
        mVolume = Statics.preferenceValue(this, Statics.PREF_VOLUME, 30)
        mSamplingRate = Statics.preferenceValue(this, Statics.PREF_SAMPLING_RATE, 0)
        mWaveform = Statics.preferenceValue(this, Statics.PREF_WAVEFORM, 0)
        mSoundRange = Statics.preferenceValue(this, Statics.PREF_SOUND_RANGE, 0)
        mEnableEnvelope = Statics.preferenceValue(this, Statics.PREF_ENABLE_ENVELOPE, 0)
        mAttackTime = Statics.preferenceValue(this, Statics.PREF_ATTACK_TIME, 0)
        mDecayTime = Statics.preferenceValue(this, Statics.PREF_DECAY_TIME, 0)
        mSustainTime = Statics.preferenceValue(this, Statics.PREF_SUSTAIN_LEVEL, 0)
        mReleaseTime = Statics.preferenceValue(this, Statics.PREF_RELEASE_TIME, 0)
        mAnimationQuality = Statics.preferenceValue(this, Statics.PREF_ANIMATION_QUALITY, 0)
        mNeverShowAlphaReleased = Statics.preferenceValue(this, Statics.PREF_NEVER_SHOW_ALPHA_RELEASED, 0)
    }

    fun updateSettingsListView() {
        val items = findViewById(R.id.settings_items) as ListView
        val list: MutableList<Map<String, String?>> = ArrayList()
        run {
            var map: MutableMap<String, String?>
            map = HashMap()
            map["key"] = getString(R.string.settings_scale)
            map["value"] = Statics.longStringOfScale(mScale)
            list.add(map)
            map = HashMap()
            map["key"] = getString(R.string.settings_darken)
            map["value"] = Statics.onOrOffString(this, mDarken)
            list.add(map)
            map = HashMap()
            map["key"] = getString(R.string.settings_vibration)
            map["value"] = Statics.onOrOffString(this, mVibration)
            list.add(map)
            map = HashMap()
            map["key"] = getString(R.string.settings_volume)
            map["value"] = "" + Statics.valueOfVolume(mVolume)
            list.add(map)
            map = HashMap()
            map["key"] = getString(R.string.settings_sound_range)
            map["value"] = "" + Statics.stringOfSoundRange(mSoundRange)
            list.add(map)
            map = HashMap()
            map["key"] = getString(R.string.settings_waveform)
            map["value"] = Statics.valueOfWaveform(mWaveform, this)
            list.add(map)

            /*
             * map=new HashMap<String,String>(); map.put("key",
             * getString(R.string.settings_envelope)); map.put("value",
             * ""+Statics.getStringOfEnvelope(enableEnvelope,attackTime,
             * decayTime,sustainLevel,releaseTime,this)); list.add(map);
             */map = HashMap()
            map["key"] = getString(R.string.settings_sampling_rate)
            map["value"] = ("" + Statics.valueOfSamplingRate(mSamplingRate) + " "
                    + getString(R.string.settings_sampling_rate_hz))
            list.add(map)
            map = HashMap()
            map["key"] = getString(R.string.settings_animation_quality)
            map["value"] = Statics.stringOfAnimationQuality(mAnimationQuality, this)
            list.add(map)
            map = HashMap()
            map["key"] = getString(R.string.settings_reset_message_dialogs)
            map["value"] = getString(R.string.settings_reset_message_dialogs_description)
            list.add(map)
        }
        val adapter = SimpleAdapter(this, list, android.R.layout.simple_expandable_list_item_2, arrayOf("key", "value"), intArrayOf(android.R.id.text1, android.R.id.text2))
        items.adapter = adapter
        items.onItemClickListener = this
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

    override fun onClick(arg0: View) {
        if (arg0.id == R.id.settings_ok) {
            finish()
        }
    }

    override fun onItemClick(arg0: AdapterView<*>, arg1: View, arg2: Int, arg3: Long) {
        mPosition = arg0.firstVisiblePosition
        when (arg2) {
            0 -> {
                val list: Array<String?> = arrayOfNulls<String>(15)
                var i = -7
                while (i <= 7) {
                    list[i + 7] = Statics.longStringOfScale(i)
                    i++
                }
                AlertDialog.Builder(this@SettingsActivity).setTitle(getString(R.string.settings_scale))
                        .setSingleChoiceItems(list, mScale + 7) { arg0, arg1 ->
                            setScale(arg1 - 7)
                            arg0.dismiss()
                            (findViewById(R.id.settings_items) as ListView).setSelection(mPosition)
                        }.show()
            }
            1 -> {
                val list: Array<String?> = arrayOfNulls<String>(2)
                list[0] = getString(R.string.off)
                list[1] = getString(R.string.on)
                AlertDialog.Builder(this@SettingsActivity).setTitle(getString(R.string.settings_darken))
                        .setSingleChoiceItems(list, mDarken) { arg0, arg1 ->
                            if (arg1 != mDarken) {
                                setDarken(arg1)
                                finish()
                            }
                            arg0.dismiss()
                            (findViewById(R.id.settings_items) as ListView).setSelection(mPosition)
                        }.show()
            }
            2 -> {
                val list: Array<String?> = arrayOfNulls<String>(2)
                list[0] = getString(R.string.off)
                list[1] = getString(R.string.on)
                AlertDialog.Builder(this@SettingsActivity).setTitle(getString(R.string.settings_vibration))
                        .setSingleChoiceItems(list, mVibration) { arg0, arg1 ->
                            setVibration(arg1)
                            arg0.dismiss()
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
                        .setSingleChoiceItems(list, mWaveform) { arg0, arg1 ->
                            setWaveform(arg1)
                            arg0.dismiss()
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
                        .setSingleChoiceItems(list, mSamplingRate + 3) { arg0, arg1 ->
                            setSamplingRate(arg1 - 3)
                            arg0.dismiss()
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
                        .setSingleChoiceItems(list, mAnimationQuality + 1) { arg0, arg1 ->
                            setAnimationQuality(arg1 - 1)
                            arg0.dismiss()
                            (findViewById(R.id.settings_items) as ListView).setSelection(mPosition)
                        }.show()
            }
            8 -> {
                updatePreferenceValues()
                if (mNeverShowAlphaReleased <= 0) {
                    AlertDialog.Builder(this@SettingsActivity).setTitle(getString(R.string.settings_reset_message_dialogs))
                            .setMessage(getString(R.string.settings_reset_message_dialogs_no_need))
                            .setPositiveButton(getString(R.string.ok)) { dialog, which -> }.show()
                } else {
                    AlertDialog.Builder(this@SettingsActivity).setTitle(getString(R.string.settings_reset_message_dialogs))
                            .setMessage(R.string.settings_reset_message_dialogs_confirm)
                            .setPositiveButton(getString(R.string.ok)) { dialog, which ->
                                setNeverShowAlphaReleased(0)
                                Toast.makeText(this@SettingsActivity, getString(R.string.settings_reset_message_dialogs_finished), Toast.LENGTH_LONG).show()
                                (findViewById(R.id.settings_items) as ListView).setSelection(mPosition)
                            }.setNegativeButton(getString(R.string.cancel)) { dialog, which -> (findViewById(R.id.settings_items) as ListView).setSelection(mPosition) }.show()
                }
            }
            else -> {
            }
        }
    }

    fun setScale(s: Int) {
        mScale = s
        Statics.setPreferenceValue(this, Statics.PREF_SCALE, mScale)
        updatePreferenceValues()
        updateSettingsListView()
    }

    fun setDarken(d: Int) {
        mDarken = d
        Statics.setPreferenceValue(this, Statics.PREF_DARKEN, mDarken)
        updatePreferenceValues()
        updateSettingsListView()
    }

    fun setVibration(v: Int) {
        mVibration = v
        Statics.setPreferenceValue(this, Statics.PREF_VIBRATION, mVibration)
        updatePreferenceValues()
        updateSettingsListView()
    }

    fun setSamplingRate(sr: Int) {
        mSamplingRate = sr
        Statics.setPreferenceValue(this, Statics.PREF_SAMPLING_RATE, mSamplingRate)
        updatePreferenceValues()
        updateSettingsListView()
    }

    fun setVolume(v: Int) {
        mVolume = v
        Statics.setPreferenceValue(this, Statics.PREF_VOLUME, mVolume)
        updatePreferenceValues()
        updateSettingsListView()
    }

    fun setSoundRange(sr: Int) {
        mSoundRange = sr
        Statics.setPreferenceValue(this, Statics.PREF_SOUND_RANGE, sr)
        updatePreferenceValues()
        updateSettingsListView()
    }

    fun setAttackTime(at: Int) {
        mAttackTime = at
        Statics.setPreferenceValue(this, Statics.PREF_ATTACK_TIME, at)
        updatePreferenceValues()
        updateSettingsListView()
    }

    fun setDecayTime(dt: Int) {
        mDecayTime = dt
        Statics.setPreferenceValue(this, Statics.PREF_DECAY_TIME, dt)
        updatePreferenceValues()
        updateSettingsListView()
    }

    fun setSustainLevel(sl: Int) {
        mSustainTime = sl
        Statics.setPreferenceValue(this, Statics.PREF_SUSTAIN_LEVEL, sl - 100)
        updatePreferenceValues()
        updateSettingsListView()
    }

    fun setReleaseTime(rt: Int) {
        mReleaseTime = rt
        Statics.setPreferenceValue(this, Statics.PREF_RELEASE_TIME, rt)
        updatePreferenceValues()
        updateSettingsListView()
    }

    fun setWaveform(wf: Int) {
        mWaveform = wf
        Statics.setPreferenceValue(this, Statics.PREF_WAVEFORM, mWaveform)
        updatePreferenceValues()
        updateSettingsListView()
    }

    fun setEnableEnvelope(ee: Int) {
        mEnableEnvelope = ee
        Statics.setPreferenceValue(this, Statics.PREF_ENABLE_ENVELOPE, mEnableEnvelope)
        updatePreferenceValues()
        updateSettingsListView()
    }

    fun setAnimationQuality(aq: Int) {
        mAnimationQuality = aq
        Statics.setPreferenceValue(this, Statics.PREF_ANIMATION_QUALITY, mAnimationQuality)
        MainActivity.setAnimationQuality(aq)
        updatePreferenceValues()
        updateSettingsListView()
    }

    fun setNeverShowAlphaReleased(nsar: Int) {
        mNeverShowAlphaReleased = nsar
        Statics.setPreferenceValue(this, Statics.PREF_NEVER_SHOW_ALPHA_RELEASED, mNeverShowAlphaReleased)
        updatePreferenceValues()
        updateSettingsListView()
    }
}