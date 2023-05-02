package controllers

import javax.inject.*
import play.api.*
import play.api.mvc.*
import play.api.db.Database

import anorm.as

import models.Country
import models.CountryDao

class CountryController @Inject() (
    countryDao: CountryDao,
    val controllerComponents: ControllerComponents,
    database: Database
) extends BaseController:
  def list() = Action { implicit request: Request[AnyContent] =>
    val resultCountries: List[Country] = countryDao.getAll()
    Ok(views.html.country.index(resultCountries))
  }

  def show(name: String) = Action { implicit request: Request[AnyContent] =>
    countryDao.getByName(name) match
      case Some(country) => Ok(views.html.country.show(country))
      case None          => NotFound
  }

  def searchContinent(continent: String) = Action {
    implicit request: Request[AnyContent] =>
      val resultCountries: List[Country] = countryDao.getByContinent(continent)
      Ok(views.html.country.index(resultCountries))
  }
