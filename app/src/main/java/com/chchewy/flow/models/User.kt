package com.chchewy.flow.models


class User(val uid: String, var username: String, val goal: Float, val profileImageUrl: String) {
    constructor() : this("", "", 0F, "")
}