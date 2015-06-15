package forms

/**
 * Created by david.a.brayfield on 12/05/2015.
 */
case class ProductForm (
                         author: String,
                         title: String,
                         productid: String,
                         manufacturer: String,
                         productgroup: String,
                         productidtype: String,
                         productIndex: String,
                         imageURL: String,
                         imageLargeURL: String,
                         source: String,
                         sourceid: String) {
  override def toString: String = {"\nProduct" +
                                   "\nauthor        - " + author +
                                   "\ntitle         - " + title +
                                   "\nproductid     - " + productid +
                                   "\nmanufacturer  - " + manufacturer +
                                   "\nproductgroup  - " + productgroup +
                                   "\nproductidtype - " + productidtype +
                                   "\nproductIndex  - " + productIndex +
                                   "\nimageURL      - " + imageURL +
                                   "\nimageLargeURL - " + imageLargeURL +
                                   "\nsource        - " + source +
                                   "\nsourceid      - " + sourceid}

}
