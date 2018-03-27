package realworld.model

case class IdRecord[K, V](id: K, record: V)

object IdRecord {
  type IdUser = IdRecord[Long, User]
}
