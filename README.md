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
curl -X 'POST' 'http://localhost:8080/performtransaction' -H "Content-Type: application/json" -d '{ "accountId": 1, "type": "DEPOSIT", "amount": 200}'

# how to withdraw money
curl -X 'POST' 'http://localhost:8080/performtransaction' -H "Content-Type: application/json" -d '{ "accountId": 1, "type": "WITHDRAW", "amount": 50}'

# how to get balance
curl -X 'GET' 'http://localhost:8080/balance/1' -H "Content-Type: application/json"

# h2 console
available at http://localhost:8080/h2-console

# Sonarcloud report
https://sonarcloud.io/project/issues?resolved=false&sinceLeakPeriod=true&id=ruggeromontesi_exercise
