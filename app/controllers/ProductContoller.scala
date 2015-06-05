package controllers
/*
http://stackoverflow.com/questions/4170949/how-to-parse-json-in-scala-using-standard-scala-classes
 */


import com.sun.org.apache.xalan.internal.xsltc.trax.DOM2SAX
import forms.{ProductForm, SearchCatalogueForm}
import org.joda.time.DateTime
import org.w3c.dom.{Element, NodeList, Document}

import scala.xml.Node

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

import com.braffa.productlookup.amazon.ProductLookUp

import scala.concurrent.Future
import scala.xml.parsing.NoBindingFactoryAdapter

object ProductController extends Controller {

  var lOfProducts = ListBuffer [Product]()

  def convertDate (strDate: String): DateTime = new DateTime(java.lang.Long.parseLong(strDate))

  def  listProducts = Action.async {
    Logger.info("ProductController listProducts")

    lOfProducts = ListBuffer [Product]()

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

  def findMyProducts = Action.async {
    Logger.info("ProductController findMyProducts")
    lOfProducts = ListBuffer [Product]()
    val userId = "Braffa"
    val url = "http://localhost:9010/findmyproducts?userId=" + userId
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
    lOfProducts = ListBuffer[Product]()
    Ok(views.html.searchCatalogue(searchCatalogueForm))
  }

  def postSearchCatalog  = Action.async { implicit request =>
    Logger.info("ProductController postSearchCatalog")
    lOfProducts = ListBuffer [Product]()

    searchCatalogueForm.bindFromRequest.fold (
      formWithErrors => {
        println("Form had errors")
        Future.successful(BadRequest( views.html.searchCatalogue(formWithErrors)))
      },
      catalogue => {
        Logger.info("input from screen - " + catalogue.toString)
        lOfProducts = ListBuffer[Product]()
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

  lazy val productFormMapping: Mapping[ProductForm] =
    mapping(
      "author"  -> text,
      "title" -> text,
      "productid" -> text,
      "manufacturer" -> text,
      "productgroup" -> text,
      "productidtype" -> text,
      "productIndex" -> text,
      "imageURL" -> text,
      "imageLargeURL" -> text,
      "source" -> text,
      "sourceid" -> text
    )(ProductForm.apply)(ProductForm.unapply)

  val productForm = Form[ProductForm](productFormMapping)

  def showProductForm = Action {
    Logger.info("ProductController showProductForm")
    productLookUp ("5060088823156", "EAN")
    Ok(views.html.productForm(productForm))
  }

  def asXml(dom: org.w3c.dom.Node): Node = {
    val dom2sax = new DOM2SAX(dom)
    val adapter = new NoBindingFactoryAdapter
    dom2sax.setContentHandler(adapter)
    dom2sax.parse()
    return adapter.rootElem
  }

  def productLookUp (productId: String, productIdType: String) = {
    Logger.info("ProductController productLookUp")
    val responseGroup = null
    val condition = null
    val searchIndex = "All"
    val imageType = "ThumbnailImage"
    val document = ProductLookUp.getProductsWithImage (productId, productIdType, responseGroup, condition, searchIndex, imageType)
    val products = asXml(document)
    Logger.info(products.toString())
    lOfProducts = ListBuffer [Product]()
    for (product <- (products \\ "product")) {
      Logger.info ("image url - " + (product \ "ImageSet" \ "ThumbnailImage").text)
      val p = new Product (
        "newProduct",
        (product \ "author").text,
        (product \ "ImageSet" \ "ThumbnailImage" \ "URL").text,
        (product \ "ImageSet" \ "ThumbnailImage" \ "URL").text,
        (product \ "manufacturer").text,
        "0",
        (product \ "productgroup").text,
        (product \ "productid").text,
        (product \ "productidtype").text,
        (product \ "source").text,
        (product \ "sourceid").text,
        (product \ "title").text,
        new DateTime(),
        new DateTime()
        )
      lOfProducts += p
      Logger.info(p.toString)
    }
  }

  def postISBNInput = Action { implicit request =>
    Logger.info("ProductController postISBNInput")
    productForm.bindFromRequest.fold (
      formWithErrors => {
        Logger.info("form had errors" + formWithErrors.errorsAsJson)
        Ok("fucked Up")
        //Future.successful(BadRequest( views.html.productForm(formWithErrors)))
      },
      productForm => {
        Logger.info(productForm.toString)
        val productId = productForm.productid
        productLookUp(productId, "EAN")
        Ok(views.html.listProducts("List of All products")((lOfProducts)))
      }
    )
  }

  def postProductInput = Action.async { implicit request =>
    Logger.info("ProductController postProductInput")
    productForm.bindFromRequest.fold (
      formWithErrors => {
        Logger.info("form had errors" + formWithErrors.errorsAsJson)
        Future.successful(BadRequest( views.html.productForm(formWithErrors)))
      },
      productForm => {
        Logger.info(productForm.toString)
        val url = "http://localhost:9010/addproduct?author=" + productForm.author +
          "&title=" + productForm.title +
          "&productid=" + productForm.productid +
          "&manufacturer=" + productForm.manufacturer +
          "&productgroup=" + productForm.productgroup +
          "&productidtype=" + productForm.productidtype +
          "&productIndex=" + productForm.productIndex +
          "&imageURL=" + productForm.imageURL +
          "&imageLargeURL=" + productForm.imageLargeURL +
          "&source=" + productForm.source +
          "&sourceid=" + productForm.sourceid
        Logger.info(url)
        val responseFuture = WS.url(url).get()
        responseFuture map { response =>
          response.status match {
            case 200 => {
              Redirect(routes.ProductController.listProducts)
            }
            case _ => {
              Logger.info("help " + response.status)
              Ok("ProductController postProductInput - got here 2")
            }
          }
        }
      }
    )
  }
}
