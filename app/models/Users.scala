package models

import reactivemongo.bson.{BSONDocumentWriter, BSONDocument, BSONDocumentReader, BSONObjectID}

/**
 * Created by DELL on 23/02/2015.
 */
case class User(
                  id: Option[BSONObjectID],
                  email: String,
                  password: String
                  )

object User{

  implicit object UserBSONReader extends BSONDocumentReader[User] {
    override def read(doc: BSONDocument): User =
      User(
        doc.getAs[BSONObjectID]("_id"),
        doc.getAs[String]("email").get,
        doc.getAs[String]("password").get
      )
  }

  implicit object UserBSONWriter extends BSONDocumentWriter[User] {
    override def write(user: User): BSONDocument =
      BSONDocument(
        "_id" -> user.id.getOrElse(BSONObjectID.generate),
        "email" -> user.email,
        "password" -> user.password
      )
  }
}
