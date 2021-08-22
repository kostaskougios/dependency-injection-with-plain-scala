package di

import scala.collection.mutable.ArrayBuffer

/**
 * Beans trait, for this example also shutdown (destroy) lifecycle is implemented.
 */
trait Beans:
  // keep track of beans that we need to shutdown
  private val toShutdown = ArrayBuffer.empty[ShutdownBeans[_]]

  // register a bean so that it is shutdown by withBeansDo()
  def registerShutDown[B](bean: B)(shutdown: B => Unit): B =
    toShutdown.synchronized(toShutdown += ShutdownBeans(bean, shutdown))
    bean

  // run f with the app context and shutdown the beans when done
  def withBeansDo[R](f: => R): R =
    try f finally shutdown()

  // shuts down all beans
  private def shutdown() =
    for (sb <- toShutdown)
      try
        sb.shutdown(sb.bean)
      catch
        case e: Throwable => e.printStackTrace()

// keeps track of beans and the code to execute to shutdown those
private class ShutdownBeans[B](val bean: B, val shutdown: B => Unit)