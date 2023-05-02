package controllers

import javax.inject.Inject
import play.api.mvc.{ControllerComponents, BaseController, Request, AnyContent}
import play.api.db.Database
import java.sql.Connection

import anorm.as

import models.Vaccination
import models.VaccinationPopulationReport
import models.VaccinationDao

class VaccinationController @Inject() (
    vaccinationDao: VaccinationDao,
    val controllerComponents: ControllerComponents,
) extends BaseController:
  def listRaw() = Action { implicit request: Request[AnyContent] =>
    val result: List[Vaccination] = vaccinationDao.getAll()
    Ok(views.html.vaccination.index(result))
  }

  def list() = Action { implicit request: Request[AnyContent] =>
    val result: List[VaccinationPopulationReport] = vaccinationDao.getVaccinationsPopulationReport()
    Ok(views.html.vaccination.report(result))
  }
