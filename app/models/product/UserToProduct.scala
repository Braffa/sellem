package models.product

import org.joda.time.DateTime

/**
 * Created by david.a.brayfield on 09/04/2015.
 */
case class UserToProduct (_id: String,
                     productId: String,
                     productIndex: String,
                     userId: String,
                     crDate: DateTime,
                     updDate: DateTime) {
  override def toString: String = {
    var p = "UserToProduct \n"
    p += "\n _id          - " + _id
    p += "\n productId    - " + productId
    p += "\n productIndex - " + productIndex
    p += "\n userId       - " + userId
    p += "\n crDate       - " + crDate
    p += "\n updDate      - " + updDate + "\n]"
    p
  }
}



