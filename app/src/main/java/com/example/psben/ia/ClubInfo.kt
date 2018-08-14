package com.example.psben.ia

class ClubInfo {
    // adminUid = uid
    var name:String? = null
    var category:String? = null
    var photo:String? = null
    var uid:String? = null
    var adminName:String? = null
    var description:String? = null
    var clubId:String? = null
    var requirements:String? = null
    constructor(name:String, category:String, photo:String, uid:String, adminName:String, description:String, clubId:String, requirements:String) {
        this.name = name
        this.category = category
        this.photo = photo
        this.uid = uid
        this.adminName = adminName
        this.description = description
        this.clubId = clubId
        this.requirements = requirements
    }
}