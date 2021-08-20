package di

import scala.collection.mutable.ArrayBuffer

trait Beans:
  private val toShutdown = ArrayBuffer.empty[ShutdownBeans[_]]

  def registerShutDown[B](bean: B)(shutdown: B => Unit): B =
    toShutdown.synchronized(toShutdown += ShutdownBeans(bean, shutdown))
    bean

  def withBeansDo[R](f: => R): R =
    try f finally shutdown()

  private def shutdown() =
    for (sb <- toShutdown)
      try
        sb.shutdown(sb.bean)
      catch
        case e: Throwable => e.printStackTrace()

private class ShutdownBeans[B](val bean: B, val shutdown: B => Unit)