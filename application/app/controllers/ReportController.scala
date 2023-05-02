package controllers

import javax.inject.*
import play.api.*
import play.api.mvc.*
import play.api.db.Database

import anorm.as

import models.{Case, CasePopulationReport, CaseDao}
import models.{VaccinationDao, VaccinationPopulationReport}

@Singleton
class ReportController @Inject() (
    caseDao: CaseDao,
    vaccinationDao: VaccinationDao,
    val controllerComponents: ControllerComponents
) extends BaseController:

  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

  def listInfectionsByCountries(limit: Int, order_by: String) = Action {
    implicit request: Request[AnyContent] =>
      val result: List[CasePopulationReport] =
        matchOrderByCase(order_by, limit)
      Ok(views.html.infection_case.report(result))
  }

  private def matchOrderByCase(
      order_by: String,
      limit: Int
  ): List[CasePopulationReport] =
    order_by match
      case s: String if s.toUpperCase() == "DESC" =>
        caseDao.getCasesPopulationReportOrderByDescLimit(limit)
      case s: String if s.toUpperCase() == "ASC" =>
        caseDao.getCasesPopulationReportOrderByAscLimit(limit)
      case _: String => List.empty

  def listVaccinationsByCountries(limit: Int, order_by: String) = Action {
    implicit request: Request[AnyContent] =>
      val result: List[VaccinationPopulationReport] =
        matchOrderByVaccination(order_by, limit)
      Ok(views.html.vaccination.report(result))
  }

  private def matchOrderByVaccination(
      order_by: String,
      limit: Int
  ): List[VaccinationPopulationReport] =
    order_by match
      case s: String if s.toUpperCase() == "DESC" =>
        vaccinationDao.getVaccinationsPopulationReportOrderByDescLimit(limit)
      case s: String if s.toUpperCase() == "ASC" =>
        vaccinationDao.getVaccinationsPopulationReportOrderByAscLimit(limit)
      case _: String => List.empty

  def listCountryWithUnvaccinatedPopulation(
      limit: Int,
      order_by: String,
      threshold: Int
  ) = Action { implicit request: Request[AnyContent] =>
    val result: List[VaccinationPopulationReport] =
      vaccinationDao.getCountryWithUnvaccinatedPopulation(threshold, order_by, limit)
    Ok(views.html.vaccination.report(result))
  }
