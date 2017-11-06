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

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class TappyBleMtuChunkingStream {
    private static final String TAG = TappyBleMtuChunkingStream.class.getName();

    private ByteArrayInputStream mSendInputStream = new ByteArrayInputStream(new byte[0]);
    private final Lock readLock;
    private final Lock writeLock;

    private static final int MTU_LIMIT = 20; //maximum packet size

    {
        ReadWriteLock rwLock = new ReentrantReadWriteLock();
        readLock = rwLock.readLock();
        writeLock = rwLock.writeLock();
    }

    protected int getMtuLimit() {
        return MTU_LIMIT;
    }

    public boolean hasBytes() {
        boolean hasBytes = false;
        readLock.lock();
        hasBytes = mSendInputStream.available() > 0;
        readLock.unlock();
        return hasBytes;
    }

    public void lockRead() {
        readLock.lock();
    }

    public void unlockRead() {
        readLock.unlock();
    }

    public byte[] getNextChunk() {
        byte[] dataToSend = new byte[0];
        readLock.lock();
        if (mSendInputStream != null) {
            int available = mSendInputStream.available();
            int chunkSize = getMtuLimit();
            if (available > 0) {
                int toRead = available > chunkSize ? chunkSize : available;
                byte[] sendBytes = new byte[toRead];
                mSendInputStream.read(sendBytes, 0, toRead);
                dataToSend = sendBytes;
            }
        }
        readLock.unlock();

        return dataToSend;
    }

    public void writeToBuffer(byte[] newData) {
        writeLock.lock();
        byte[] existingBytes = new byte[0];
        if (mSendInputStream != null && mSendInputStream.available() > 0) {
            existingBytes = new byte[mSendInputStream.available()];
            mSendInputStream.read(existingBytes, 0, mSendInputStream.available());
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(existingBytes.length + newData.length);
        try {
            outputStream.write(existingBytes);
            outputStream.write(newData);
        } catch (IOException e) {
            Log.wtf(TAG,e);
        }

        try {
            mSendInputStream.close();
        } catch (IOException e) {
            Log.wtf(TAG,e);
        }
        mSendInputStream = new ByteArrayInputStream(outputStream.toByteArray());
        writeLock.unlock();
    }
}
