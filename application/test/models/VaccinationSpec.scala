import org.scalatestplus.play.*
import org.scalatestplus.play.guice.GuiceOneAppPerSuite

import models.Vaccination
import models.VaccinationPopulationReport
import models.VaccinationDao

class VaccinationSpec extends PlaySpec with GuiceOneAppPerSuite {
  def vaccinationRepository: VaccinationDao =
    app.injector.instanceOf[VaccinationDao]

  "Vaccination Model".should {
    "return a list of vaccination".in {
      val actualVaccinations: List[Vaccination] = vaccinationRepository.getAll()

      actualVaccinations.length must equal(28512)
    }

    "return a list of vaccination report".in {
      val actualVaccinations: List[VaccinationPopulationReport] = vaccinationRepository.getVaccinationsPopulationReport()

      actualVaccinations.length must equal(173)
    }
  }
}
