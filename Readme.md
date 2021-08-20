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

Before we start declaring some service layer beans, we need to see the code for those.

## The example components: AccountService

The components are about bank accounts and transferring money from one account to another (very simplified).

There is the [Account](examples/src/main/scala/example/model/Account.scala) class:

```scala

type Money = Int // <-- keep it simple

case class Account(
  id: String,
  money: Money
) 
```

And the [AccountService](examples/src/main/scala/example/service/AccountService.scala) which just
uses in-memory values for simplicity:

```scala
class AccountService:
  def get(id: String): Account =
  // just hardcode the values for the sake of simplicity
    id match
      case "acc1" => Account("acc1", 50)
      case "acc2" => Account("acc2", 150)
```

Ok now we would need the bean declarations for the `AccountService`. I tend to put those in the same file but it 
could also be declared in a separate scala file.

```scala
trait AccountServiceBeans extends Beans :
  lazy val accountService = new AccountService
```

As we can see, this just declares an instance of the account service. We will use that later on in prod code when
we need an instance of it.

So the full `AccountService.scala` file looks like:

```scala
class AccountService:
  def get(id: String): Account =
  // just hardcode the values for the sake of simplicity
    id match
      case "acc1" => Account("acc1", 50)
      case "acc2" => Account("acc2", 150)

trait AccountServiceBeans extends Beans :
  lazy val accountService = new AccountService
```

Ok good now we have the account service and a useful instance of it for when we need it. But how do we declare
a dependency to the `AccountService` and how do we inject the above instance?

## The TransferService

The [TransferService](examples/src/main/scala/example/service/TransferService.scala) is the service that
transfers money between 2 accounts.

```scala
class TransferService(accountService: AccountService):
  def transfer(fromAccountId: String, toAccountId: String, amount: Money): TransferResult =
    val f = accountService.get(fromAccountId)
    val t = accountService.get(toAccountId)
    TransferResult(f.take(amount), t.give(amount))
```

As we can see, it depends on the `accountService` to get the accounts from the account id. For simplicity
this just returns the transfer results via a [TransferResult](examples/src/main/scala/example/model/TransferResult.scala)
case class.

Now time to declare the transfer service beans:

```scala
trait TransferServiceBeans extends Beans :
  def accountService: AccountService

  lazy val transferService = new TransferService(accountService)
```

This provides an instance `transferService`. The injected `accountService` is a def which means we can either
mix it in with `AccountServiceBeans` or mock it in tests.

Let's see how would we mix in the beans for a production application context.
[ExampleProdModule](examples/src/main/scala/example/ExampleProdModule.scala) does that:

```scala
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
```

As you can see from the comments, we do get the singleton `accountService` which is injected in the singleton
`transferService`. Now we have our prod app context and we can use it in 
[ExampleApp](examples/src/main/scala/example/ExampleApp.scala):

```scala
@main
def exampleApp() =
  new ExampleProdModule :
      val results = transferService.transfer("acc2", "acc1", 50)
      println(s"Transfer results: $results")
```

## Testing the TransferService

If you noticed, `TransferServiceBeans` requires a `def accountService: AccountService` in order for it
to compile and to wire an `accountService`. So we can wire what we need in a test, either a mock or our
own instance of it.

In [TransferServiceTest](examples/src/test/scala/example/service/TransferServiceTest.scala) we wire
our own instance:

```scala
  /**
   * Note how we create a di context for testing by using TransferServiceBeans.
   * Normally here we would use a mocking framework for accountService but
   * for simplicity we just create an instance. Potentially we could just
   * extend AccountServiceBeans if we wanted the prod instance of AccountService
   */
  class App extends TransferServiceBeans :
    lazy val accountService = new AccountService // we could mock it here if needed
```

Then we can use our testing App context to test:

```scala
  test("transfers") {
    new App :
      transferService.transfer("acc2", "acc1", 20) should be(TransferResult(
        Account("acc2", 130),
        Account("acc1", 70)
      ))
  }
```

The full [TransferServiceTest](examples/src/test/scala/example/service/TransferServiceTest.scala) code:

```scala
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
```