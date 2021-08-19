package example

@main
def exampleApp() =
  new ExampleProdModule :
    val results = transferService.transfer("acc2", "acc1", 50)
    println(s"Transfer results: $results")