package models

case class Address(id: Long,
                    name: String,
                    number: Int,
                    street: String,
                    city: String,
                    postalCode: Int,
                    county:String,
                    country: String) {

  override def toString: String = {
    name + '\n' +
    number + " " + street + '\n' +
    city + ", " + county + ", " + postalCode + '\n' +
    country
  }
}