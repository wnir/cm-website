package controllers

import models.{User, Comment}
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._
import play.modules.reactivemongo.MongoController
import play.mvc.Results.Redirect
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.BSONDocument
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}

/**
 * Created by DELL on 22/02/2015.
 */
object SecureZone extends Controller with MongoController {

  val collection = db[BSONCollection]("users")

  lazy val loginForm = Form(
    tuple(
      "email" -> nonEmptyText,
      "password" -> nonEmptyText)/* verifying("Invalid user or password ", result => result match {
      case (email, password) => {
        println("user = " + email + " with password " + password);
        val query = BSONDocument("email" -> email, "password" -> password)
        val found = collection.find(query).cursor[User]
        val result = found.collect[List]()
        Await.result(result, 10 seconds).nonEmpty
      }
      case _ => false
    })*/
  )

  def login = Action { implicit request =>
    Ok(views.html.admin.login(loginForm))
  }

  def authenticate = Action.async { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithError => scala.concurrent.Future(Redirect(routes.SecureZone.login).flashing("error" -> "Login/Mdp incorrects")),
      user => {
        val query = BSONDocument("email" -> user._1, "password" -> user._2)
        val cursor = collection.find(query).cursor[User]
        val futureList: Future[List[User]] = cursor.collect[List]()
//        futureList.filter(list => list.size > 0)
        futureList map { usersList =>
          if (usersList.size == 1) Redirect(routes.Comments.index).withSession("email" -> user._1)
          else Redirect(routes.SecureZone.login).flashing("error" -> "Login/Mdp incorrects")

        }
//        futureList.map {
//          users => {
//            users match
//              case l =>  Redirect(routes.Comments.index).withSession("email" -> user._1)
//              case _ => Redirect(routes.Comments.index).withSession("email" -> user._1)
//             //Redirect(routes.Comments.index).withSession("email" -> user._1) //if users.size > 0 else Redirect(routes.SecureZone.login)
//            /*case 1 => Redirect(routes.Comments.index).withSession("email" -> user._1)
//            case _ => Redirect(routes.SecureZone.login)*/
//          }
//        }
      }
    )
  }


  def logout = Action {
    Redirect(routes.Application.index()).withNewSession
  }

  trait Secured {
    self: Controller =>

    def username(request: RequestHeader) = request.session.get("email")

    def onUnauthorized(requet: RequestHeader): Result

    def isFutureAuthenticated(f: => String => Request[AnyContent] => Future[Result]) =
    Security.Authenticated(username, onUnauthorized) { user =>
      Action.async(request => f(user)(request))
    }

    def isAuthenticated(f: => String => Request[AnyContent] => Result) =
      Security.Authenticated(username, onUnauthorized) { user =>
        Action(request => f(user)(request))
      }
  }

}
