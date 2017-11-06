package com.taptrack.experiments.rancheria.model

import io.realm.RealmObject
import io.realm.annotations.Index
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

open class RealmTcmpCommunique() : RealmObject() {
    @Required
    @PrimaryKey
    var communiqueId: String? = null
    @Required
    @Index
    var messageTime: Long? = null
    @Required
    var deviceId: String? = null
    @Required
    var deviceName: String? = null
    @Required
    var isCommand: Boolean? = null
    @Required
    var message: ByteArray? = null
}