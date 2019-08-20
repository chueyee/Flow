package com.chchewy.flow.models


class User(val uid: String, val username: String, val goal: Float, val profileImage: String) {
    constructor() : this("", "", 0F, "")
}