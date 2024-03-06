# Exercise
# How to set the project up
* git clone https://github.com/ruggeromontesi/exercise.git
* cd ./exercise
* mvn clean package
* mvn spring-boot:run

# API documentation
After running application API documentation is available at:
http://localhost:8080

# How to create bank account
Use http://localhost:8080/swagger-ui/index.html#/account-controller/createAccount specify 
 * userId
 * account type: [SAVING, CURRENT].

System is "pre-loaded" with two users with id 1 and 2.
Response returns the id of the account created. Note it as you will need this id to perform all operations below

# How to deposit/ withdraw money
Use POST method  http://localhost:8080/swagger-ui/index.html#/account-controller/executeTransaction. 
Specify :
* accountId,
* positive amount,
* type of transaction: [DEPOSIT, WITHDRAWAL]
  
Response returns transaction info such as transaction id, type, status, execution time
If the amount of withdrawal is bigger than the available balance the transaction will not be performed.

# How to get balance
Use http://localhost:8080/swagger-ui/index.html#/account-controller/getBalance 
specify
* accountId

# How to get transactions
Use http://localhost:8080/swagger-ui/index.html#/account-controller/getLastTenTransactions
Specify 
* accountId
# h2 console
available at http://localhost:8080/h2-console
user: ruggero
pwd : 1234

# Sonarcloud report
https://sonarcloud.io/summary/new_code?id=ruggeromontesi_exercise&branch=master

# How will you design/organize the micro services for your API product?

# How will you break down the business requirements into user stories?
* 1 As a user, I would like to create a saving account for the customer, knowing his id, specifying account type (SAVING, CURRENT) and the currency (EUR).
* 2 As a user, I would like to perform deposit and withdrawal to/from a certain account, knowing the id of the account and  specifying the type of operation and the relevant amount. For the saving account 
   is not foreseen overdraft, i.e.: if the amount of withdrawal is bigger than the available  balance the transaction should fail, but still should be kept track of it.
* 3 As a user, I would like to read the available balance for a certain account, given the account id.
* 4 As a user, I would like to have the list of last 10 transactions given a the id of the account.
