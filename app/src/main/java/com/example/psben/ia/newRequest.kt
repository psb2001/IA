package com.example.psben.ia

class newRequest {
    var adminUid: String? = null
    var userUid: String? = null
    var userName:String? = null
    var userPhoto:String? = null
    var clubId: String? = null
    var clubName: String? = null
    var clubPhoto: String? = null
    var contentType:String? = null
    var content:String? = null
    var status:String? = null

    constructor(adminUid:String, userUid:String, userName:String, userPhoto:String, clubId:String, clubName:String, clubPhoto:String, contentType:String , content:String, status:String) {
        this.adminUid = adminUid
        this.userUid = userUid
        this.userName = userName
        this.userPhoto = userPhoto
        this.clubId = clubId
        this.clubName = clubName
        this.clubPhoto = clubPhoto
        this.contentType = contentType
        this.content = content
        this.status = status
    }
}