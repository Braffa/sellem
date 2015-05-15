package controllers
/*
http://stackoverflow.com/questions/4170949/how-to-parse-json-in-scala-using-standard-scala-classes
 */

import org.joda.time.DateTime
import play.api._
import play.api.libs.json._
import play.api.libs.ws.WS
import play.api.mvc._
import play.api.Play.current

import scala.collection.mutable.{ListBuffer}
import scala.concurrent.ExecutionContext.Implicits.global

import models.product.Product

object ProductController extends Controller {

  def createProduct (products: String): String = {
    var p = "Product \n[BSONObjectID  - "
    p
  }

  def convertDate (strDate: String): DateTime = new DateTime(java.lang.Long.parseLong(strDate))

  def listProducts = Action.async {
    Logger.info("ProductController listProducts")

    var lOfProducts = ListBuffer [Product]()

    val responseFuture = WS.url("http://localhost:9010/listJsonProducts").get()
    responseFuture map { response =>
      response.status match {
        case 200 => {
          val products = Json.toJson(response.json)
          var index = 0
          for (s <- (products \\ "_id")) {
            val product = new Product (
              (products \\ "_id")(index).toString.replace('"', ' ').trim,
              (products \\ "author")(index).toString.replace('"', ' ').trim,
              (products \\ "imageURL")(index).toString.replace('"', ' ').trim,
              (products \\ "imageLargeURL")(index).toString.replace('"', ' ').trim,
              (products \\ "manufacturer")(index).toString.replace('"', ' ').trim,
              (products \\ "productIndex")(index).toString.replace('"', ' ').trim,
              (products \\ "productgroup")(index).toString.replace('"', ' ').trim,
              (products \\ "productId")(index).toString.replace('"', ' ').trim,
              (products \\ "productidtype")(index).toString.replace('"', ' ').trim,
              (products \\ "source")(index).toString.replace('"', ' ').trim,
              (products \\ "sourceid")(index).toString.replace('"', ' ').trim,
              (products \\ "title")(index).toString.replace('"', ' ').trim,
              convertDate((products \\ "crDate")(index).toString.replace('"', ' ').trim),
              convertDate((products \\ "updDate")(index).toString.replace('"', ' ').trim)
            )
            Logger.info(product.toString)
            lOfProducts += product
            index += 1
          }
          Ok(views.html.listProducts("List of All products")((lOfProducts)))
        }
        case _ => Ok("got here ")
      }
    }
  }
}
