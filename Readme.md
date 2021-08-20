# Dependency injection via plain scala for Scala 2 or 3

This is an example repo demonstrating how DI can be performed without any framework or external lib in Scala. The
approach is not the cake pattern though there are a few similarities. It has the benefits that wiring issues will be
compilation errors (instead of runtime for most DI frameworks), there is no reflection involved, lifecycle for beans can
be implemented as we want, and it is a lot easier to trace what beans are injected. A draw back is that there is a bit
more coding to declare the beans.

This DI pattern can be applied to spark projects too where I am mainly using it since 2020.

Note this scala project can be checked out and compiled with sbt. It contains the components described in this readme
file. This is a scala 3 project.

## The Beans trait

First we will have a marker [Beans](lib/src/main/scala/di/Beans.scala) trait which should be inherited by all bean
declarations.

```scala
trait Beans
```

This can be empty initially (later on we will add lifecycle methods if we need them, but for now it can be empty).

## The example components

The components are about bank accounts and transferring money from one account to another (very simplified).

There is the [Account](examples/src/main/scala/example/model/Account.scala) class:

```scala

type Money = Int // <-- keep it simple

case class Account(
  id: String,
  money: Money
) 
```

