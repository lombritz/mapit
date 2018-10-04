package controllers

import play.api.mvc._
import play.api.libs.json._

import models.RealEstates
import scala.collection.mutable.ListBuffer

object Application extends Controller {

  def index = Action {
    Ok(views.html.index("REALLY??"))
  }

  def fetch(area: String): Action[AnyContent] = Action {
    Ok()
  }

  def about = TODO

  def login = TODO

  def signup = TODO

}