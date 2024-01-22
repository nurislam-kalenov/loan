# Inbank Loan Service

# Local infrastructure for development

Code and scripts required to get required applications running in your machine.

## Requirements

* Docker
* Docker Compose
* Gradle

## Running a service
    ./gradlew build
    docker image build -t loan-service .
    docker-compose up -d
    
    Frontend (Swagger): http://localhost:8080/swagger-ui/index.html

## Task description
Please design a decision engine which takes in personal code, loan amount, loan period in months and returns a decision (negative or positive) and the amount. <br />
The idea of the decision engine is to determine what would be the maximum sum, regardless of the person requested loan amount. <br /> 
For example if a person applies for 4000 €, but we determine that we would approve a larger sum then the result should be the maximum sum which we would approve. <br />
Also in reverse, if a person applies for 4000 € and we would not approve it then we want to return the largest sum which we would approve, for example 2500 €. <br />
If a suitable loan amount is not found within the selected period, the decision engine should also try to find a new suitable period. In real life the solution should connect to external registries and compose a comprehensive user profile, but for the sake of simplicity this part can be mocked as a hard coded result for certain personal codes. 
As the scope of this solution you only need to support 4 different scenarios - a person has debt or a person falls under a different segmentation.<br />

    For example :
    49002010965 - debt
    49002010976 - segment 1 (credit_modifier = 100)
    49002010987 - segment 2 (credit_modifier = 300)
    49002010998 - segment 3 (credit_modifier = 1000)

If a person has debt then we do not approve any amount. If a person has no debt then we take the identifier and use it for calculating person's credit score taking into account the requested input. <br />
 Constraints:
Minimum input and output sum can be 2000 € Maximum input and output sum can be 10000 € Minimum loan period can be 12 months Maximum loan period can be 60 months <br />
Scoring algorithm. For calculating credit score a really primitive algorithm should be implemented. You need to divide the credit modifier with the loan amount and multiply the result with the loan period in months. If the result is less than 1 then we would not approve such sum, if the result is larger or equal than 1 then we would approve this sum. <br />

    credit score = (credit modifier / loan amount) * loan period

As a result please provide working code with a single api endpoint and front-end application which uses the functionality. Also whenever possible share your thought process.

## Explanation of important choices in your solution

1. 'Command' or 'UseCase' design. Instead of traditional 3 layered architecture (View-Service-DAO) design I decided to use approach from Clean
   Architecture. Each operation is independent and isolated and do only what it promises. Predictable and easy to test.
   Plus there are 2 independent domains that easy to split and move to another module/microservice.
2. Custom response model. Custom error messages (GlobalExceptionHandler). It needs to be flexible and independent from
   Spring Web responses and error handlers.
3. API can be internal and external. There is versioning of API (/v1)

• Estimate on how many transactions can your account application can handle per second on your development machine Used
tool: *JMeter*. API: POST `v1/loan/decision`.

| Thread count            |  Loop count   | Error Rate | Throughput(sec) |
| ----------------------  |:-------------:|-----------:|----------------:|
| 1                       | 10000         |      0.00% |              21 |
| 10                      | 10000         |      0.00% |              29 |
| 50                      | 100000        |      0.00% |              34 |
| 70                      | 1000000       |      0.00% |              35 |

# Scale applications horizontally and ideas.

1. We could consider JexlEngine for formulas and store formulas in some external storage.
2. High Load. Let's say 2-3 million request per day. And the system cache profile data, save every request into Postgres DB for history. Could be need for analysis or AI team. Also, we have complicated formula that takes some ms for calculations. The system needs to process 2 million
   request per day, which is 2,000,000 requests / 20^5 seconds = 20 request per second (TPS). 15-20 TPS is
   not a big number for a Postgres database. Anyway it is obvious that the 'loan' domain will be loaded more than
   the 'profile module hundreds or even a thousand times. The solution would be to take out the loan module
   separately and set auto-scale. We can name it as 'loan-decision' service.
3. Postgres database replication. There will be a lot of insert probably we should think about sharding.


