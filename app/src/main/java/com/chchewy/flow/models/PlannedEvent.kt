package com.chchewy.flow.models

import com.google.gson.annotations.SerializedName

class PlannedEvent {
    @SerializedName("id")
    var id: String = "defaultValue"

    @SerializedName("message")
    var message: String = "defaultValue"
}