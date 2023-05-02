package controllers

import javax.inject.*
import play.api.*
import play.api.mvc.*

import models.{Case, CaseCountryReport, CaseDao}

@Singleton
class QueryController @Inject() (
    caseDao: CaseDao,
    val controllerComponents: ControllerComponents
) extends BaseController:

  def reportByCountryUpUntilDate(country: String, date: Option[String]) =
    Action { implicit request: Request[AnyContent] =>
      val format = new java.text.SimpleDateFormat("yyyy-MM-dd")

      val newValue = date match
        case Some(date) => format.parse(date)
        case None       => format.parse("2023-01-01")

      val result: List[CaseCountryReport] =
        caseDao.getByCountryUpUntilDate(country, newValue)
      Ok(views.html.infection_case.test(result))
    }
