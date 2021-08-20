package example.service

import di.Beans
import example.model.{Money, TransferResult}

class TransferService(accountService: AccountService):
  def transfer(fromAccountId: String, toAccountId: String, amount: Money): TransferResult =
    val f = accountService.get(fromAccountId)
    val t = accountService.get(toAccountId)
    TransferResult(f.take(amount), t.give(amount))

trait TransferServiceBeans extends Beans :
  def accountService: AccountService

  lazy val transferService = registerShutDown(new TransferService(accountService)) {
    service =>
      println(s"Shutting down $service")
  }