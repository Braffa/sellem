package controllers
/*
http://stackoverflow.com/questions/4170949/how-to-parse-json-in-scala-using-standard-scala-classes
 */


import java.net.{URLEncoder, URI, URL}

import com.sun.org.apache.xalan.internal.xsltc.trax.DOM2SAX
import forms.{AddProductForm, ProductForm, SearchCatalogueForm}
import org.joda.time.DateTime
import org.w3c.dom.{Element, NodeList, Document}
import views.html.helper.form

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

  lazy val addProductFormMapping: Mapping[AddProductForm] =
    mapping(
      "productid" -> text
    )(AddProductForm.apply)(AddProductForm.unapply)

  var addProductForm = Form[AddProductForm](addProductFormMapping)

  def showAddProductForm = Action {
    Logger.info("ProductController showAddProductForm")
    Ok(views.html.addProductForm(addProductForm))
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

  var productForm = Form[ProductForm](productFormMapping)

  def showProductForm = Action {
    Logger.info("ProductController showProductForm")
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
    val imageType = null
    val document = ProductLookUp.getProductsWithImage (productId, productIdType, responseGroup, condition, searchIndex, imageType)
    val products = asXml(document)
    Logger.info(products.toString())
    lOfProducts = ListBuffer [Product]()
    for (product <- (products \\ "product")) {
      val p = new Product (
        "newProduct",
        (product \ "author").text,
        (product \ "ImageSet" \ "ThumbnailImage" \ "URL").text,
        (product \ "ImageSet" \ "LargeImage" \ "URL").text,
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

  def postISBNAddProduct = Action { implicit request =>
    Logger.info("ProductController postISBNAddProduct")
    addProductForm.bindFromRequest.fold (
      formWithErrors => {
        Logger.info("form had errors" + formWithErrors.errorsAsJson)
        Ok("fucked Up")
        //Future.successful(BadRequest( views.html.addProductForm(formWithErrors)))
      },
      addProductFormIn => {
        Logger.info(addProductForm.toString)
        val productId = addProductFormIn.productid.replaceAll("[-]", "")
        productLookUp(productId, "EAN")
        if (lOfProducts.size == 0) {
          productLookUp(productId, "ISBN")
        }
        if (lOfProducts.size > 1) {
          Ok(views.html.listProducts("List of All products")((lOfProducts)))
        } else if (lOfProducts.size == 1) {
          val product = lOfProducts(0)
          var pf = new ProductForm(product.author, product.title, product.productId, product.manufacturer,
            product.productgroup, product.productidtype, product.productIndex,
            product.imageURL, product.imageLargeURL, product.source, product.sourceid)
          val filledform = productForm.fill(pf)
          Ok(views.html.productForm(filledform))
        } else {
          Ok(views.html.productForm(productForm))
        }
      }
    )
  }

  def postProductInput = Action.async { implicit request =>
    Logger.info("ProductController postProductInput")
    val product = lOfProducts (0)
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

  def saveProduct = Action.async { implicit request =>
    Logger.info("ProductController saveProduct")
    //if (lOfProducts.size > 1) {
      val product = lOfProducts (0)

    Logger.info("saveProduct - " + product.toString)

    val urlPar = "?author=" + URLEncoder.encode(product.author, "UTF-8") +
      "&imageURL=" + product.imageURL +
      "&imageLargeURL=" + product.imageLargeURL +
      "&manufacturer=" + URLEncoder.encode(product.manufacturer, "UTF-8") +
      "&productIndex=" + URLEncoder.encode(product.productIndex, "UTF-8") +
      "&productgroup=" + URLEncoder.encode(product.productgroup, "UTF-8") +
      "&productid=" + URLEncoder.encode(product.productId, "UTF-8") +
      "&productidtype=" + URLEncoder.encode(product.productidtype, "UTF-8") +
      "&source=" + URLEncoder.encode(product.source, "UTF-8") +
      "&sourceid=" + URLEncoder.encode(product.sourceid, "UTF-8") +
      "&title=" + URLEncoder.encode(product.title, "UTF-8")
    Logger.info(urlPar)
    val urlStr = "http://localhost:9010/addproduct" + urlPar
    Logger.info(urlStr)
    val responseFuture = WS.url(urlStr).get()
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

  //}
}
