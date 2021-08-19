package example

import example.service.{AccountServiceBeans, TransferServiceBeans}

/**
 * This injects dependencies for prod applications
 */
trait ExampleProdModule
  extends AccountServiceBeans
    with TransferServiceBeans
// this effectively is the following:
// lazy val accountService = new AccountService
// lazy val transferService = new TransferService(accountService)
// This way, accountService is injected in transferService
