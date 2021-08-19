package example.model

type Money = Int // <-- keep it simple

case class Account(
  id: String,
  money: Money
):
  def take(amount: Money) =
    if (amount > money) throw new IllegalArgumentException("Can't transfer that much")
    copy(money = money - amount)

  def give(amount: Money) = copy(money = money + amount)
