package com.chchewy.flow.models


class User(val uid: String, val username: String, val goal: Float) {
    constructor() : this("", "", 0F)
}