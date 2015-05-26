package controllers
/*
http://stackoverflow.com/questions/4170949/how-to-parse-json-in-scala-using-standard-scala-classes
 */

import forms.{SearchCatalogueForm}
import org.joda.time.DateTime
import play.api._
import play.api.data.Forms._
import play.api.data.{Form, Mapping}
import play.api.libs.json._
import play.api.libs.ws.WS
import play.api.mvc._
import play.api.Play.current

import scala.collection.mutable.{ListBuffer}
import scala.concurrent.ExecutionContext.Implicits.global

import models.product.Product

import scala.concurrent.Future

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


  lazy val searchCatalogueMapping: Mapping[SearchCatalogueForm] =
    mapping(
      "author"  -> text,
      "title" -> text,
      "productId" -> text,
      "manufacturer" -> text
    )(SearchCatalogueForm.apply)(SearchCatalogueForm.unapply)

  val searchCatalogueForm = Form[SearchCatalogueForm](searchCatalogueMapping)

  def showSearchCatalog = Action {
    Logger.info("ProductController showSearchCatalog")
    var lOfProducts = ListBuffer[Product]()
    Ok(views.html.searchCatalogue(searchCatalogueForm))
  }

  def postSearchCatalog  = Action.async { implicit request =>
    Logger.info("ProductController postSearchCatalog")
    var lOfProducts = ListBuffer [Product]()

    searchCatalogueForm.bindFromRequest.fold (
      formWithErrors => {
        println("Form had errors")
        Future.successful(BadRequest( views.html.searchCatalogue(formWithErrors)))
      },
      catalogue => {
        Logger.info(catalogue.author)
        Logger.info(catalogue.title)
        Logger.info(catalogue.productId)
        Logger.info(catalogue.manufacturer)
        var lOfProducts = ListBuffer[Product]()
        val url = "http://localhost:9010/searchCatalogue?author=" + catalogue.author + "&title=" + catalogue.title + "&productId=" + catalogue.productId  + "&manufacturer=" + catalogue.manufacturer
        Logger.info(url)
        val responseFuture = WS.url(url).get()
        responseFuture map { response =>
          response.status match {
            case 200 => {
              val products = Json.toJson(response.json)
              var index = 0
              for (s <- (products \\ "_id")) {
                val product = new Product(
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
    )
    }
}
