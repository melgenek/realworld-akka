package realworld.util

import realworld.model.User

object TestData {

  val UserName = "username"
  val Email = "email"
  val Password = "password"
  val PasswordHash = "passwordHash"
  val Token = "token"
  val Bio = "bio"
  val Image = "image"

  val user = User(email = Email, username = UserName, password = PasswordHash)

}
