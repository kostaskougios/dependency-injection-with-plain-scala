package example.service

import example.model.{Account, TransferResult}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers.*

class TransferServiceTest extends AnyFunSuite :
  test("transfers") {
    new App :
      transferService.transfer("acc2", "acc1", 20) should be(TransferResult(
        Account("acc2", 130),
        Account("acc1", 70)
      ))
  }

  /**
   * Note how we create a di context for testing by using TransferServiceBeans.
   * Normally here we would use a mocking framework for accountService but
   * for simplicity we just create an instance. Potentially we could just
   * extend AccountServiceBeans if we wanted the prod instance of AccountService
   */
  class App extends TransferServiceBeans :
    lazy val accountService = new AccountService // we could mock it here if needed
