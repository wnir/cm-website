package models

import org.joda.time.DateTime
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._
import play.api.data.validation.Constraints._
import reactivemongo.bson._


/**
 * Created by DELL on 14/02/2015.
 */
case class Comment(
                    id: Option[BSONObjectID],
                    title: String,
                    email: String,
                    subject: String,
                    creationDate: Option[DateTime]
                    )

object Comment {

  implicit object CommentBSONReader extends BSONDocumentReader[Comment] {
    def read(doc: BSONDocument): Comment =
      Comment(
        doc.getAs[BSONObjectID]("_id"),
        doc.getAs[String]("title").getOrElse(""),
        doc.getAs[String]("email").getOrElse(""),
        doc.getAs[String]("subject").getOrElse(""),
        doc.getAs[BSONDateTime]("creationDate").map(dt => new DateTime(dt.value))
      )
  }

  implicit object CommentBSONWriter extends BSONDocumentWriter[Comment] {
    def write(comment: Comment): BSONDocument =
      BSONDocument(
        "_id" -> comment.id.getOrElse(BSONObjectID.generate),
        "title" -> comment.title,
        "email" -> comment.email,
        "subject" -> comment.subject,
        "creationDate" -> comment.creationDate.map(date => BSONDateTime(date.getMillis))
      )
  }

  val form = Form(
    mapping(
      "id" -> optional(of[String] verifying pattern(
        """[a-fA-F0-9]{24}""".r,
        "constraint.objectId",
        "error.objectId")),
      "title" -> nonEmptyText,
      "email" -> nonEmptyText,
      "subject" -> nonEmptyText,
      "creationDate" -> optional(of[Long])) { (id, title, email, subject, creationDate) =>
      Comment(
        id.map(BSONObjectID(_)),
        title,
        email,
        subject,
        creationDate.map(new DateTime(_))
      )
    } { comment =>
      Some(
        (comment.id.map(_.stringify),
          comment.title,
          comment.email,
          comment.subject,
          comment.creationDate.map(_.getMillis))
      )
    }
  )
}

