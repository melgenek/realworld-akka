package realworld.model

trait RelationModel extends UserModel {

  import profile.api._

  class Relations(t: Tag) extends Table[(Long, Long)](t, "relations") {
    def followerId = column[Long]("follower_id", O.PrimaryKey)

    def followeeId = column[Long]("followee_id", O.PrimaryKey)

    def follower = foreignKey("FOLLOWER_FK", followerId, users)(_.id)

    def followee = foreignKey("FOLLOWEE_FK", followeeId, users)(_.id)

    def * = (followerId, followeeId)
  }

  val relations = TableQuery[Relations]

}
