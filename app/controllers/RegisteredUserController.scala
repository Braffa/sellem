package controllers


import models.authentication.RegisteredUser
import org.joda.time.DateTime

import play.api.Logger
import play.api.libs.json.Json
import play.api.libs.ws.WS
import play.api.mvc.{Action, Controller}

import play.api.Play.current

import scala.collection.mutable.ListBuffer

import scala.concurrent.ExecutionContext.Implicits.global


/**
 * Created by david.a.brayfield on 12/05/2015.
 */
object RegisteredUserController extends Controller{

  def convertDate (strDate: String): DateTime = new DateTime(java.lang.Long.parseLong(strDate))

  def listRegisteredUsers = Action.async {
    Logger.info("RegisteredUserController listRegisteredUsers")
    var lOfRegisteredUsers = ListBuffer [RegisteredUser]()

    val responseFuture = WS.url("http://localhost:9010/listJsonregisteredUsers").get()
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
            Logger.info(registeredUser.toString)
            lOfRegisteredUsers += registeredUser
            index += 1
          }
          //Ok(Json.toJson(registeredUsers))
          Ok(views.html.listRegisteredUsers("List of All Registered Users")((lOfRegisteredUsers)))
        }
        case _ => Ok("got here ")
    }
    }
  }
}
