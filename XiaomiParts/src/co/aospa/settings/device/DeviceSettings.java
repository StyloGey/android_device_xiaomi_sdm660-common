/*
 * Copyright 2020 The Xiaomi-SDM660 Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package co.aospa.settings.device;

import android.content.Intent;
import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragment;

import co.aospa.settings.device.kcal.KCalSettingsActivity;
import co.aospa.settings.device.preferences.NotificationLedSeekBarPreference;
import co.aospa.settings.device.preferences.SecureSettingListPreference;
import co.aospa.settings.device.preferences.SecureSettingSwitchPreference;
import co.aospa.settings.device.preferences.VibrationSeekBarPreference;

public class DeviceSettings extends PreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    public static final String CATEGORY_VIBRATOR = "vibration";
    public static final String PREF_VIBRATION_STRENGTH = "vibration_strength";
    public static final String VIBRATION_STRENGTH_PATH =
            "/sys/devices/virtual/timed_output/vibrator/vtg_level";

    static final int MIN_LED = 1;
    static final int MAX_LED = 64;

    public static final String CATEGORY_NOTIF = "notification_led";
    public static final String PREF_NOTIF_LED = "notification_led_brightness";
    public static final String NOTIF_LED_PATH = "/sys/class/leds/white/max_brightness";

    // value of vtg_min and vtg_max
    public static final int MIN_VIBRATION = 116;
    public static final int MAX_VIBRATION = 3596;

    private static final String PREF_DEVICE_KCAL = "device_kcal";

    public static final String PREF_THERMAL = "thermal";
    public static final String THERMAL_PATH =
            "/sys/devices/virtual/thermal/thermal_message/sconfig";

    private static final String CATEGORY_HALL_WAKEUP = "hall_wakeup";
    public static final String PREF_HALL_WAKEUP = "hall";
    public static final String HALL_WAKEUP_PATH = "/sys/module/hall/parameters/hall_toggle";
    public static final String HALL_WAKEUP_PROP = "persist.service.folio_daemon";

    private SecureSettingListPreference mThermal;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences_xiaomi_parts, rootKey);

        if (FileUtils.fileWritable(VIBRATION_STRENGTH_PATH)) {
            VibrationSeekBarPreference vibrationStrength =
                    (VibrationSeekBarPreference) findPreference(PREF_VIBRATION_STRENGTH);
            vibrationStrength.setOnPreferenceChangeListener(this);
        } else {
            getPreferenceScreen().removePreference(findPreference(CATEGORY_VIBRATOR));
        }

        if (FileUtils.fileWritable(NOTIF_LED_PATH)) {
            NotificationLedSeekBarPreference notifLedBrightness =
                    (NotificationLedSeekBarPreference) findPreference(PREF_NOTIF_LED);
            notifLedBrightness.setOnPreferenceChangeListener(this);
        } else {
            getPreferenceScreen().removePreference(findPreference(CATEGORY_NOTIF));
        }

        Preference kcal = findPreference(PREF_DEVICE_KCAL);

        kcal.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(getActivity().getApplicationContext(),
                    KCalSettingsActivity.class);
            startActivity(intent);
            return true;
        });

        mThermal = (SecureSettingListPreference) findPreference(PREF_THERMAL);
        mThermal.setValue(FileUtils.getValue(THERMAL_PATH));
        mThermal.setSummary(mThermal.getEntry());
        mThermal.setOnPreferenceChangeListener(this);

        if (FileUtils.fileWritable(HALL_WAKEUP_PATH)) {
            SecureSettingSwitchPreference hall = (SecureSettingSwitchPreference) findPreference(
                    PREF_HALL_WAKEUP);
            hall.setChecked(FileUtils.getValue(HALL_WAKEUP_PATH).equals("Y"));
            hall.setOnPreferenceChangeListener(this);
        } else {
            getPreferenceScreen().removePreference(findPreference(CATEGORY_HALL_WAKEUP));
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        final String key = preference.getKey();
        switch (key) {
            case PREF_NOTIF_LED:
                double ledMaxBrightnessValue = (int) value / 100.0 * (MAX_LED - MIN_LED) + MIN_LED;
                FileUtils.setValue(NOTIF_LED_PATH, ledMaxBrightnessValue);
                return true;

            case PREF_VIBRATION_STRENGTH:
                double vibrationValue =
                        (int) value / 100.0 * (MAX_VIBRATION - MIN_VIBRATION) + MIN_VIBRATION;
                FileUtils.setValue(VIBRATION_STRENGTH_PATH, vibrationValue);
                return true;

            case PREF_THERMAL:
                mThermal.setValue((String) value);
                mThermal.setSummary(mThermal.getEntry());
                FileUtils.setValue(THERMAL_PATH, (String) value);
                return false;

            case PREF_HALL_WAKEUP:
                FileUtils.setValue(HALL_WAKEUP_PATH, (boolean) value ? "Y" : "N");
                FileUtils.setProp(HALL_WAKEUP_PROP, (boolean) value);
                return true;
        }
        return false;
    }
}
