package controllers

import forms.LoginForm
import models.authentication.RegisteredUser
import org.joda.time.DateTime
import play.api.Logger
import play.api.libs.json.Json
import play.api.libs.ws.WS
import play.api.mvc._
import play.api.data.Forms._
import play.api.data._

import play.api.Play.current
import scala.concurrent.ExecutionContext.Implicits.global

import scala.collection.mutable.ListBuffer
import scala.concurrent.Future

import scala.concurrent._
import play.api.mvc.Results._

/**
 * Created by david.a.brayfield on 12/05/2015.
 */
object LoginController extends Controller {

  def convertDate (strDate: String): DateTime = new DateTime(java.lang.Long.parseLong(strDate))

  lazy val loginMapping: Mapping[LoginForm] =
    mapping(
      "userId" -> nonEmptyText,
      "password" -> nonEmptyText
    )(LoginForm.apply)(LoginForm.unapply)

  val loginForm = Form[LoginForm](loginMapping)

  def showLogin = Action {
    Logger.info("LoginController showLogin")
    var lOfRegisteredUser = ListBuffer[RegisteredUser]()
    Ok(views.html.login(loginForm))
  }

  def post  = Action.async { implicit request =>
    Logger.info("LoginController post")
    loginForm.bindFromRequest.fold (
      formWithErrors => {
        Future.successful(BadRequest( views.html.login(formWithErrors)))
      },
      login => {
        Logger.info(login.userId)
        Logger.info(login.password)
        var lOfRegisteredUser = ListBuffer[RegisteredUser]()
        val url = "http://localhost:9010/login?userId=" + login.userId
        Logger.info(url)
        val responseFuture = WS.url(url).get()

        responseFuture map { response =>
          response.status match {
            case 200 => {
              val registeredUsers = Json.toJson(response.json)
              var index = 0
              for (s <- (registeredUsers \\ "_id")) {
                val registeredUser = new RegisteredUser (
                  (registeredUsers \\ "_id")(index).toString.replace('"', ' ').trim,
                  (registeredUsers \\ "authorityLevel")(index).toString.replace('"', ' ').trim,
                  (registeredUsers \\ "email")(index).toString.replace('"', ' ').trim,
                  (registeredUsers \\ "firstName")(index).toString.replace('"', ' ').trim,
                  (registeredUsers \\ "lastName")(index).toString.replace('"', ' ').trim,
                  (registeredUsers \\ "password")(index).toString.replace('"', ' ').trim,
                  (registeredUsers \\ "telephone")(index).toString.replace('"', ' ').trim,
                  (registeredUsers \\ "userId")(index).toString.replace('"', ' ').trim,
                  convertDate((registeredUsers \\ "crDate")(index).toString.replace('"', ' ').trim),
                  convertDate((registeredUsers \\ "updDate")(index).toString.replace('"', ' ').trim)
                )
                Logger.info("found " + registeredUser.toString)
                lOfRegisteredUser += registeredUser
                index += 1
              }
              Redirect(routes.ProductController.listProducts)
            }
            case _ => Ok("got here 2")
          }
        }
      }
    )
  }
}
