package repositories

import models.FeatureFlag.Enabled
import models.FeatureFlagName.MLD5
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{EitherValues, FreeSpec, Inside, MustMatchers, OptionValues}
import play.api.test.Helpers._
import uk.gov.hmrc.estatesstore.MongoSuite

import scala.concurrent.ExecutionContext.Implicits.global

class FeaturesRepositorySpec
  extends FreeSpec with MustMatchers
    with ScalaFutures with OptionValues with Inside with MongoSuite with EitherValues {

  "Features Repository" - {

    "must round trip feature flags correctly" in {

      running(application) {
        getConnection(application).map { connection =>

          val repo = application.injector.instanceOf[FeaturesRepository]

          dropTheDatabase(connection)

          val data = Seq(Enabled(MLD5))

          whenReady(repo.setFeatureFlags(data).flatMap(_ => repo.getFeatureFlags)) { result =>
            result mustBe data
          }
        }
      }
    }
  }
}
