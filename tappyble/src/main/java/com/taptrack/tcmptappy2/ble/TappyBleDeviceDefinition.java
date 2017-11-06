/*
 * Copyright (c) 2016. Papyrus Electronics, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * you may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.taptrack.tcmptappy2.ble;

import java.util.UUID;

public interface TappyBleDeviceDefinition {
    public final static UUID DEFAULT_TRUCONNECT_SERVICE_UUID
            = UUID.fromString("175f8f23-a570-49bd-9627-815a6a27de2a");
    public final static UUID DEFAULT_TRUCONNECT_RX_CHARACTERISTIC_UUID
            = UUID.fromString("1cce1ea8-bd34-4813-a00a-c76e028fadcb");
    public final static UUID DEFAULT_TRUCONNECT_TX_CHARACTERISTIC_UUID
            = UUID.fromString("cacc07ff-ffff-4c48-8fae-a9ef71b75e26");

    String getName();
    String getAddress();
    public UUID getSerialServiceUuid();
    public UUID getTxCharacteristicUuid();
    public UUID getRxCharacteristicUuid();
}
