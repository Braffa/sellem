package forms

/**
 * Created by david.a.brayfield on 12/05/2015.
 */
case class RegisterUserForm (
           authorityLevel:String,
           email:String,
           firstName:String,
           lastName:String,
           password: String,
           telephone: String,
           userId: String) {
  override def toString: String = {"\nauthorityLevel - " + authorityLevel +
                                   "\nemail          - " + email +
                                   "\nfirstName      - " + firstName +
                                   "\nlastName       - " + lastName +
                                   "\npassword       - " + password +
                                   "\ntelephone       - " + telephone +
                                   "\nuserId         - " + userId
  }

}
