package controllers

import play.api.mvc._

object Application extends Controller {

  def index = Action {
    Ok(views.html.index("REALLY??"))
  }

  def about = TODO

  def login = TODO

  def signup = TODO

}