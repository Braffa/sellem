package models.authentication

import org.joda.time.DateTime

case class RegisteredUser (_id: String,
                   authorityLevel: String, 
                   email: String, 
                   firstName: String, 
                   lastName: String, 
                   password: String,
                   telephone: String, 
                   userId: String,
                   crDate: DateTime,
                   updDate: DateTime) {

  override def toString: String = {
    var p = "Rehistered User \n"
    p += "\n _id            - " + _id
    p += "\n authorityLevel - " + authorityLevel
    p += "\n email          - " + email
    p += "\n firstName      - " + firstName
    p += "\n lastName       - " + lastName
    p += "\n password       - " + password
    p += "\n telephone      - " + telephone
    p += "\n userId         - " + userId
    p += "\n crDate         - " + crDate
    p += "\n updDate        - " + updDate + "\n]"
    p
  }
}