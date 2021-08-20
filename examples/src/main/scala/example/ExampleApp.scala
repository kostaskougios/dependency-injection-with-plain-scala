package example

@main
def exampleApp() =
  new ExampleProdModule :
    withBeansDo { // <-- makes sure beans are shutdown
      val results = transferService.transfer("acc2", "acc1", 50)
      println(s"Transfer results: $results")
    }