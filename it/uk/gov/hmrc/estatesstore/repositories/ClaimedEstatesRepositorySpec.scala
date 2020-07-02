package uk.gov.hmrc.estatesstore.repositories

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import play.api.Application
import play.api.test.Helpers._
import uk.gov.hmrc.estatesstore.MongoSuite
import uk.gov.hmrc.estatesstore.models.claim_an_estate.EstateClaim

import scala.concurrent.Future
import scala.language.implicitConversions

class ClaimedEstatesRepositorySpec extends AsyncFreeSpec with MustMatchers
  with ScalaFutures with OptionValues with Inside with MongoSuite with EitherValues {

  "a claimed estates repository" - {

    val internalId = "Int-328969d0-557e-4559-96ba-074d0597107e"

    def assertMongoTest(application: Application)(block: Application => Assertion): Future[Assertion] =
      running(application) {
        for {
          connection <- Future.fromTry(getConnection(application))
          _ <- dropTheDatabase(connection)
        } yield block(application)
      }

    "must be able to store, retrieve and remove estates claims" in assertMongoTest(application) { app =>

      val repository = app.injector.instanceOf[ClaimedEstatesRepository]

      val lastUpdated = LocalDateTime.parse("2000-01-01 12:30", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))

      val estateClaim = EstateClaim(internalId, "1234567890", managedByAgent = true, lastUpdated = lastUpdated)

      val storedClaim = repository.store(estateClaim).futureValue.right.value

      inside(storedClaim) {
        case EstateClaim(id, utr, mba, tl, ldt) =>
          id mustEqual internalId
          utr mustEqual storedClaim.utr
          mba mustEqual storedClaim.managedByAgent
          tl mustEqual storedClaim.estateLocked
          ldt mustEqual storedClaim.lastUpdated
      }

      repository.get(internalId).futureValue.value mustBe estateClaim

      repository.remove(internalId).futureValue

      repository.get(internalId).futureValue mustNot be(defined)
    }

    "must be able to update a estate claim with the same auth id" in assertMongoTest(application) { app =>

      val repository = app.injector.instanceOf[ClaimedEstatesRepository]

      val lastUpdated = LocalDateTime.parse("2000-01-01 12:30", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))

      val estateClaim = EstateClaim(internalId, "1234567890", managedByAgent = true, lastUpdated = lastUpdated)

      repository.store(estateClaim).futureValue.right.value

      val updatedClaim = repository.store(estateClaim.copy(utr = "0987654321")).futureValue

      updatedClaim must be('right)
    }
  }
}
