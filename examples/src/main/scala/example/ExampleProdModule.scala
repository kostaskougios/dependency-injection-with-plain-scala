package example

import example.service.{AccountServiceBeans, TransferServiceBeans}

/**
 * This injects dependencies for prod applications
 */
trait ExampleProdModule
  extends AccountServiceBeans
    with TransferServiceBeans
