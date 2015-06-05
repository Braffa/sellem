package forms

/**
 * Created by david.a.brayfield on 12/05/2015.
 */
case class SearchCatalogueForm    (
            author: String,
            title: String,
            productId: String,
            manufacturer: String) {

  override def toString: String = { "\nauthor       - " + author +
                                    "\ntitle        - " + title +
                                    "\nproductId    - " + productId +
                                    "\nmanufacturer - " + manufacturer
  }
}
