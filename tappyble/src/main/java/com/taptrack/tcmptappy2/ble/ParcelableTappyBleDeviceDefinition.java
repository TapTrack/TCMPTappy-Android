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

import android.os.Parcel;
import android.os.ParcelUuid;
import android.os.Parcelable;

import java.util.UUID;

public class ParcelableTappyBleDeviceDefinition
        implements com.taptrack.tcmptappy2.ble.TappyBleDeviceDefinition, Parcelable {
    private String name;
    private String address;
    private UUID serviceUuid;
    private UUID rxCharacteristicUuid;
    private UUID txCharacteristicUuid;


    public static final Parcelable.Creator<ParcelableTappyBleDeviceDefinition> CREATOR =
            new Parcelable.Creator<ParcelableTappyBleDeviceDefinition>() {

                @Override
                public ParcelableTappyBleDeviceDefinition createFromParcel(Parcel source) {
                    return new ParcelableTappyBleDeviceDefinition(source);
                }

                @Override
                public ParcelableTappyBleDeviceDefinition[] newArray(int size) {
                    return new ParcelableTappyBleDeviceDefinition[size];
                }
            };

    ParcelableTappyBleDeviceDefinition() {

    }

    public ParcelableTappyBleDeviceDefinition(com.taptrack.tcmptappy2.ble.TappyBleDeviceDefinition defn) {
        this.name = defn.getName();
        this.address = defn.getAddress();
        this.serviceUuid = defn.getSerialServiceUuid();
        this.rxCharacteristicUuid = defn.getRxCharacteristicUuid();
        this.txCharacteristicUuid = defn.getTxCharacteristicUuid();
    }

    public ParcelableTappyBleDeviceDefinition(String name, String address, UUID serviceUuid, UUID rxCharacteristicUuid, UUID txCharacteristicUuid) {
        this.name = name;
        this.address = address;
        this.serviceUuid = serviceUuid;
        this.rxCharacteristicUuid = rxCharacteristicUuid;
        this.txCharacteristicUuid = txCharacteristicUuid;
    }

    public ParcelableTappyBleDeviceDefinition(Parcel in) {
        this.name = in.readString();
        this.address = in.readString();
        ParcelUuid pServiceUuid = in.readParcelable(null);
        this.serviceUuid = pServiceUuid.getUuid();

        ParcelUuid pRxUuid = in.readParcelable(null);
        this.rxCharacteristicUuid = pRxUuid.getUuid();

        ParcelUuid pTxUuid = in.readParcelable(null);
        this.txCharacteristicUuid = pTxUuid.getUuid();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(address);
        dest.writeParcelable(new ParcelUuid(getSerialServiceUuid()), 0);
        dest.writeParcelable(new ParcelUuid(getRxCharacteristicUuid()),0);
        dest.writeParcelable(new ParcelUuid(getTxCharacteristicUuid()),0);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public UUID getSerialServiceUuid() {
        return serviceUuid;
    }

    @Override
    public UUID getTxCharacteristicUuid() {
        return txCharacteristicUuid;
    }

    @Override
    public UUID getRxCharacteristicUuid() {
        return rxCharacteristicUuid;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        com.taptrack.tcmptappy2.ble.TappyBleDeviceDefinition that = (com.taptrack.tcmptappy2.ble.TappyBleDeviceDefinition) o;

        if (getName() != null ? !getName().equals(that.getName()) : that.getName() != null)
            return false;
        if (getAddress() != null ? !getAddress().equals(that.getAddress()) : that.getAddress() != null)
            return false;
        if (getSerialServiceUuid() != null ? !getSerialServiceUuid().equals(that.getSerialServiceUuid()) : that.getSerialServiceUuid() != null)
            return false;
        if (getRxCharacteristicUuid() != null ? !getRxCharacteristicUuid().equals(that.getRxCharacteristicUuid()) : that.getRxCharacteristicUuid() != null)
            return false;
        return !(getTxCharacteristicUuid() != null ? !getTxCharacteristicUuid().equals(that.getTxCharacteristicUuid()) : that.getTxCharacteristicUuid() != null);

    }

    @Override
    public int hashCode() {
        int result = getName() != null ? getName().hashCode() : 0;
        result = 31 * result + (getAddress() != null ? getAddress().hashCode() : 0);
        result = 31 * result + (getSerialServiceUuid() != null ? getSerialServiceUuid().hashCode() : 0);
        result = 31 * result + (getRxCharacteristicUuid() != null ? getRxCharacteristicUuid().hashCode() : 0);
        result = 31 * result + (getTxCharacteristicUuid() != null ? getTxCharacteristicUuid().hashCode() : 0);
        return result;
    }
}

