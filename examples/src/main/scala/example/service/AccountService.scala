package example.service

import di.Beans
import example.model.Account

class AccountService:
  def get(id: String): Account =
  // just hardcode the values for the sake of simplicity
    id match
      case "acc1" => Account("acc1", 50)
      case "acc2" => Account("acc2", 150)

trait AccountServiceBeans extends Beans :
  lazy val accountService = new AccountService
