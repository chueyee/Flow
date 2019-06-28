package com.chchewy.flow.models

class PlannedEvent(val id: String, val text: String, val timestamp: Long) {
    constructor() : this("","",-1)
}