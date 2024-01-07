# Dragons of Mugloar

```                   _                     
                  | |                    
 ____  _   _  ____| | ___  _____  ____   
|    \| | | |/ _  | |/ _ \(____ |/ ___)  
| | | | |_| ( (_| | | |_| / ___ | |      
|_|_|_|____/ \___ |\_)___/\_____|_|      
            (_____|


```

## Requirements
Link to the tech exercise - [https://www.dragonsofmugloar.com/](https://www.dragonsofmugloar.com/ "https://www.dragonsofmugloar.com/") . Complete the ‘back-end scripting adventure’ in the bottom of the page.

- Java application which plays the game and reliably (9 out of 10 times) can reach 1 000 points.
- Code should be on a level which you would put to production (README.md file, tests, clean code etc.)
- Regarding design patterns - only use them if you see a need, you don’t need to introduce a pattern just to show you know it, readability is key here.


## How to start it

### Prerequisites
You will need on your machine:

Git:
https://git-scm.com/downloads

JDK 17
https://docs.aws.amazon.com/corretto/latest/corretto-17-ug/downloads-list.html

Docker and Docker compose
https://docs.docker.com/get-docker/
https://docs.docker.com/compose/install/

### Steps
1. Clone repository
```bash
git clone https://github.com/KNITS-OS/BB-Dragons-of-Mugloar.git
cd Mugloar 
cd Mugloar # not a typo you need it twice to enter nested Mugloar dirs
```
2. Build Docker images from docker compose file
```bash
docker-compose build
```
3. Run Docker images with docker compose
```bash
docker-compose up
```
**Please note** that because of health checks **mugloar-api** will take some time to start, only after database is confirmed to be running. Please be patient..

### When system is running:
Configuration is provided with env vars from files.
Env vars example files are provided under /docker dir.
Different values can be set in different host systems overriding them accordingly.
See vars section below for more details.


1. Test api using swagger page:
   `http://localhost:8099/swagger-ui/index.html`

2. Verify from logs at /temp/mugloar/logs/mugloar.log execution

3. Access DB event table to monitor execution using following query:
   Change `E.game_external_id` according to given `gameId`
```sql
select E.game_external_id, E.event_external_id,
E.type,E.outcome,
E.operation_amount, E.game_gold, E.response_gold, E.game_score, E.response_score,
E.game_lives, E.response_lives, E.game_level, E.response_level,
E.operation_counter, E.game_turn, E.response_turn

from event E
where E.game_external_id='1sCkOLBS'
order by E.operation_counter;
```
4. Is possible to get the current status of the game
   GET[/api/game-executions/{gameId}](http://localhost:8099/swagger-ui/index.html#/game-execution-controller/findByExternalId)


## Tech stack

- [x] Spring Boot
- [x] Postgres DB
- [x] Maven
- [x] Docker & Docker Compose


## Technical Features

- [x] Spring Mvc for Rest api
- [x] Spring Data JPA for DB access layer
- [x] Swagger Open Api for documentation
- [x] Spring Actuator for diagnostics
- [x] Resilient http client with Feign an retry policies
- [x] Cache game status using Caffeine Cache
- [x] Entity/Dto mapping with Mapstruct
- [x] Asynchronous execution with Thread pool and CompleteableFuture
- [x] Unit tests with Surefire plugin (exclude *IT. tests)
- [x]  Integration tests with fail safe plugin (exclude unit tests)

## Design explanation

**Packaging**: Mugloar-Api is packaged by layer, and divided between **core** and **game** features at every level.
**CQRS**: Command Query Responsibility Segregation pattern has been used in dto used to represent data in memory and requests to external api.
**Feign Http client:** External api calls are performed with Feign http client, with a retry implementation and a client side dealy according to throttling configuration.


**core packages**:
Represent a client view of data fetched by the dragons of mugloar external api.
Here is possible to find features to store game execution details.
Main use is to provide data for analysis and diagnostics about game execution and give some possible hints about how to improve strategy choices.
Game summary, Mission, MissionResult and ItemPurchased are saved if flag (see configuration details below) is set to true.

**game packages**:
Here the implementation of features in requirements are implemented.
Design is relatively easy, but open for high availability implementations.

Here are key classes with relevant responsibilities:

| Service             | Responsibilities                           | Description                                     |
|---------------------|--------------------------------------------|-------------------------------------------------|
| ExampleService      | - Handle data processing<br> - Manage state | This service is responsible for processing data and managing application state. |
| UserService         | - User authentication<br> - User management | Manages user authentication and provides functionality for user management. |
| PaymentService      | - Process payment transactions<br> - Billing | Handles payment processing and billing-related tasks. |
| NotificationService | - Send notifications<br> - Manage templates  | Responsible for sending notifications and managing notification templates. |
| AnalyticsService     | - Collect and analyze data<br> - Reporting  | Collects and analyzes application data, generates reports for analysis. |



## Configuration with env vars:

| Variable Name                                | Default Value                           | Description                                                 |
|----------------------------------------------|-----------------------------------------|-------------------------------------------------------------|
| `mugloar.save-execution`                     | `true`                                  | Controls whether execution results should be saved.         |
| `mugloar.mugloar-api-host`                   | `https://dragonsofmugloar.com`         | Host URL for the Mugloar API.                                |
| `mugloar.auto-start-game`                   | `false`                                 | If set to true, will start one test game automatically after 3 seconds from application startup.          |
| `mugloar.cache.initial-capacity`            | `10`                                    | Initial capacity of the cache.                               |
| `mugloar.cache.maximum-size`                 | `1000`                                  | Maximum size of the cache.                                   |
| `mugloar.cache.expire-after-write-in-seconds`| `1200`                                 | Cache entry expiration time after write in seconds.         |
| `mugloar.cache.expire-after-last-access-in-seconds`| `600`                              | Cache entry expiration time after last access in seconds.   |
| `mugloar.async.async-execution`             | `false`                                 | Enable asynchronous execution. Instead of leaving a long http request pending until last game requested has been completed, async execution returns immediately and send feedback to a configurable url for every game completion. Please note that currently feedback is only designed with logs, not implemented as it was not a requirement.                             |
| `mugloar.async.async-callback-url`          | `www.somehost.com`                     | Callback URL for asynchronous execution.                    |
| `mugloar.async.async-executor-pool-size`    | `10`                                    | Size of the asynchronous executor pool.                     |
| `mugloar.strategy.gold-reserve-hot-pot`     | `200`                                   | Gold reserve threshold for the "hot pot" strategy. This is minimum budget reserved for hot pot purchase.           |
| `mugloar.strategy.lives-accept-safe-limit`  | `3`                                     | Lives limit for accepting risks in missions. If current lives are lower than this value only **safe missions** will be accepted. Highest weighted reward will have priority.             |
| `mugloar.strategy.lives-accept-easy-limit`  | `4`                                     | Lives limit for accepting risks in missions. If current lives are between than this value and lives-accept-safe-limit value, then **safe** and **easy** missions will be allowed. Highest weighted reward will have priority.                |
| `mugloar.strategy.lives-accept-risky-limit` | `5`                                     | Lives limit for accepting risks in missions.  If current lives are higher than this value all missions from safe, easy and risky queues are accepted. Highest weighted reward will have priority.          |
| `mugloar.strategy.lives-min-safe-level`     | `5`                                     | If lives are equal or below this limit, every chance to buy hot pot will be used, according to available budget.                |
| `mugloar.strategy.mission-not-found-threshold` | `2`                                 | Threshold for the number of missions not found triggering a batch reload.|
| `mugloar.throttling.api-throttling-delay`   | `100`                                   | Delay time for mugloar API client side throttling in milliseconds.              |
| `mugloar.throttling.api-throttling-on-exception-delay` | `300`                         | Delay time for mugloar API client side throttling on exception in milliseconds. |
