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
                     updDate: DateTime)



