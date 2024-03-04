# exercise
# how to set it up
* git clone https://github.com/ruggeromontesi/exercise.git
* cd ./exercise
* mvn clean package
* mvn spring-boot:run
* @pause

# how to create bank account
curl -X 'POST' 'http://localhost:8080/account/management/create' -H "Content-Type: application/json" -d '{ "userId": 1, "currency" : "EUR", "accountType": "SAVING"}'

# how to deposit money
curl -X 'POST' 'http://localhost:8080/account/management/performtransaction' -H "Content-Type: application/json" -d '{ "accountId": 1, "type": "DEPOSIT", "amount": 200}'

# how to withdraw money
curl -X 'POST' 'http://localhost:8080/account/management/performtransaction' -H "Content-Type: application/json" -d '{ "accountId": 1, "type": "WITHDRAW", "amount": 50}'

# how to get balance
curl -X 'GET' 'http://localhost:8080/account/management/balance/1' -H "Content-Type: application/json"

# h2 console
available at http://localhost:8080/h2-console

# Sonarcloud report
https://sonarcloud.io/summary/new_code?id=ruggeromontesi_exercise&branch=master

# How will you design/organize the micro services for your API product?

# How will you break down the business requirements into user stories?
* 1 As a user, I would like to create a saving account for the customer, knowing his id, specifying account type (SAVING, CURRENT) and the currency (EUR).
* 2 As a user, I would like to perform deposit and withdrawal to/from a certain account, knowing the id of the account and  specifying the type of operation and the relevant amount. For the saving account 
   is not foreseen overdraft, i.e.: if the amount of withdrawal is bigger than the available  balance the transaction should fail, but still should be kept track of it.
* 3 As a user, I would like to read the available balance for a certain account, given the account id.
* 4 As a user, I would like to have the list of last 10 transactions given a the id of the account.
