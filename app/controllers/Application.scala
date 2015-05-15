package controllers
/*
http://stackoverflow.com/questions/4170949/how-to-parse-json-in-scala-using-standard-scala-classes
 */

import controllers.LoginController._
import models.authentication.RegisteredUser
import play.api._
import play.api.mvc._

import scala.collection.mutable.ListBuffer

object Application extends Controller {

  //def index = Action {
  //  Logger.info("Application index")
  //  Ok(views.html.index("Your new application is ready."))
  //}

  def index = Action {
    Logger.info("LoginController showLogin")
    var lOfRegisteredUser = ListBuffer[RegisteredUser]()
    Ok(views.html.index(loginForm))
  }

}
