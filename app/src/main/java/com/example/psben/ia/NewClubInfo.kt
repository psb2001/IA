package com.example.psben.ia

class NewClubInfo {
    var uid:String? = null
    var name:String? = null
    var category:String? = null
    var description:String? = null
    var photo:String? = null
    var adminName:String? = null
    constructor(name:String, category:String, photo:String, uid:String, adminName:String, description:String) {
        this.name = name
        this.category = category
        this.description = description
        this.photo = photo
        this.uid = uid
        this.adminName = adminName
    }
}