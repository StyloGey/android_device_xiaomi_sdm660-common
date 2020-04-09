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

import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

public class PerformanceTileService extends TileService {

    @Override
    public void onStartListening() {
        int currentState = Integer.parseInt(FileUtils.getValue(DeviceSettings.THERMAL_PATH));

        Tile tile = getQsTile();
        tile.setState(Tile.STATE_ACTIVE);
        tile.setLabel(getResources().getStringArray(R.array.thermal_profiles)[currentState]);

        tile.updateTile();
    }

    @Override
    public void onClick() {
        int currentState = Integer.parseInt(FileUtils.getValue(DeviceSettings.THERMAL_PATH));

        int nextState;
        if (currentState == 4) {
            nextState = 0;
        } else {
            nextState = currentState + 1;
        }

        Tile tile = getQsTile();
        FileUtils.setValue(DeviceSettings.THERMAL_PATH, nextState);
        tile.setLabel(getResources().getStringArray(R.array.thermal_profiles)[nextState]);

        tile.updateTile();
    }
}
