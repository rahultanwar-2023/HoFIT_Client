package com.hofit.hofitclient.model

import java.io.Serializable

class TimeSlot : Serializable {
    var isChecked = false

    var name: String? = null

    @JvmName("getName1")
    fun getName(): String? {
        return name
    }

    @JvmName("setName1")
    fun setName(name: String?) {
        this.name = name
    }
}