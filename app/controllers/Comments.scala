package controllers

import controllers.SecureZone.Secured
import org.joda.time.DateTime
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc._
import play.modules.reactivemongo.{MongoController, ReactiveMongoPlugin}
import models.Comment
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.BSONDocument
import play.filters.csrf._

import scala.concurrent.Future


/**
 * Created by DELL on 14/02/2015.
 */

object Comments extends Controller with MongoController with Secured {

  val collection = db[BSONCollection]("comments")

  def index = isFutureAuthenticated { username =>
    implicit request =>

        // get a sort document (see getSort method for more information)
        // build a selection document with an empty query and a sort subdocument ('$orderby')
        val query = BSONDocument(
          "$query" -> BSONDocument())
        // the cursor of documents
        val found = collection.find(query).cursor[Comment]
        // build (asynchronously) a list containing all the articles
        found.collect[List]().map { comments =>
          Ok(views.html.admin.contacts(comments))
        }.recover {
          case e =>
            e.printStackTrace()
            BadRequest(e.getMessage())
        }

  }

  def showCreationForm() = CSRFAddToken {
    Action {
      implicit request =>
        Ok(views.html.contact(Comment.form))
    }
  }

  def create = CSRFCheck {
    Action.async { implicit request =>
      Comment.form.bindFromRequest.fold(
        errors => Future.successful(Ok(views.html.contact(errors))),
        // if no error, then insert the article into the 'articles' collection
        comment =>
          collection.insert(comment.copy(creationDate = Some(new DateTime()))).map(_ =>
            Redirect(routes.Comments.showCreationForm()).flashing("success" -> "Message sent"))
      )
    }
  }

  def contentPage = isAuthenticated { username =>
    implicit request =>
      Ok(views.html.admin.contentMgt())
  }

  override def onUnauthorized(requet: RequestHeader) =
    Redirect(routes.Application.index())
}
