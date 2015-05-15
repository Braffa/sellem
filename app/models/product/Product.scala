package models.product

import org.joda.time.DateTime

case class Product ( oid: String,
                     author: String,
                    imageURL: String,
                    imageLargeURL: String,
                    manufacturer: String,
                    productIndex: String,
                    productgroup: String,
                    productId: String,
                    productidtype: String,
                    source: String,
                    sourceid: String,
                    title: String,
                    crDate: DateTime,
                    updDate: DateTime) {

  override def toString: String = {
    var p = "Product \n"
    p += "\n oid           - " + oid
    p += "\n author        - " + author
    p += "\n imageURL      - " + imageURL
    p += "\n imageLargeURL - " + imageLargeURL
    p += "\n manufacturer  - " + manufacturer
    p += "\n productIndex  - " + productIndex
    p += "\n productgroup  - " + productgroup
    p += "\n productId     - " + productId
    p += "\n productidtype - " + productidtype
    p += "\n source        - " + source
    p += "\n sourceid      - " + sourceid
    p += "\n title         - " + title
    p += "\n crDate        - " + crDate
    p += "\n updDate       - " + updDate + "\n]"
    p
  }
}
