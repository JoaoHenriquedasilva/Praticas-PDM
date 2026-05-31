package com.sistemaestudos.db.fb

import com.sistemaestudos.model.User

class FBUser {
    var name : String ? = null
    var email : String? = null
    fun toUser() = User(name!!, email!!)
}
fun User.toFBUser() : FBUser {
    val fbUser = FBUser()
    fbUser.name = this.name
    fbUser.email = this.email
    return fbUser
}