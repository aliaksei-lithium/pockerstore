## Description

*Source:* `/api/`

*Solution:* `/solution/`

The task is to implement a simple online store application called “Pokerstore”. 
In the application a customer can purchase items using his credit. The inventory of available items, 
the prices and the credit for each customer is managed by a store employee through the same application.

## Notes

* Have not changed any already exited interface or package(except solving Task 2). And try to follow "convention" of naming and package structure
* `double` type not suitable for accounting operations, so move in implementation to BigDecimal, remain interface the same. 
* First attempt to solve tasks - w/o DB and use only in memory storage for fast develop and create universal solution and 
interfaces for different repositories implementation.
* To support concurrency, have used pessimistic lock on service layer. This lock was used to solve concurrent correct access for sensitive operations.
But it's preferable to move to db level lock if possible, something like `select ... for update` row level lock implementations, and application/db level transactions (like Spring `@Transactional` for correct and predictable database usage)
* Implementation description: 
    - Domain entities stay immutable and without specific database annotation and fields.
    - For in-memory storage Entities used as is, and for OrientDB used Document storage with *by hand* scheme generation. 
    Btw, it's ok to use mutable entities with JPA/Spring Data and use concrete data storage annotations and special fields in Entities.
    For example: with OrientDB ObjectDatabase need to add getters/setters and special fields for id and indexes and document versions, and 
    this will add more concise and handy API with repository.
* Business logic assumptions: 
    - Order from supplier will stock product's quantity that not enough right after purchase. In real world it will be just request then.
    - Customer can buy only available products if ask more that in stock right now - supplier will add new products after purchase. 
    But Customer should be notified about this in some ways.

## Tasks

### Task 1
_All methods in the StoreFrontService and BackOfficeService interfaces must be
implemented to support employee functions and customer purchases. The following basic
business rules apply:
It must be possible to order products that are not currently in stock
A purchase is not allowed if the customer has insufficient credit_
#### Solution
Implement methods and add tests for business logic.

### Task 2

_It must be possible for an employee to ask for a report of products out of stock._

#### Solution
Implement service method `BackOfficeService#findOutOfStockProducts` with generic repository method `StockRepository#findWhere` that takes predicate function. 
For best performance it's possible to implement  methods like `StockRepository#findByInventory` with concrete optimal storage implementation.

### Task 3
1. _The store must be able to handle concurrent user requests._
2. _The store must use a NOSQL data store to persistent data._
3. _The store may either run standalone in-process (with the JUnit test) or in a container
managed environment._

#### Solution

1. Striped lock not best solution for concurrent requests and can lead to deadlocks in case of not accurate programming. 
But solve concurrent issues and have more or less universal solution with not bad performance. Btw, repositories not 'thread-safe' and without external
service layer synchronization may lead to incorrect business behavior and something like 'OptimisticLockException'.
2. Used OrientDB as a NOSQL solution in this case just for simple reasons: It's NOSQL db, can be embedded to application, work a little with it before.
3. Have not used any containers or frameworks and third-party libraries. But when developing not sandbox application, prefer 
use suitable libs/frameworks.