
A Spring Boot REST API that calculates customer reward points based on purchases.  
Customers earn points according to these rules:
- **2 points per $1** spent over $100 in a transaction.
- **1 point per $1** spent between $50 and $100.
- Purchases below $50 earn **0 points**.

#Sample Data 

URL:----http://localhost:8087/api/rewards/calculate


Response---[
{
        "customerId": "Test1",
        "monthlyPoints": {
            "2026-03": 50.00
        },
        "totalPoints": 50.00
    },
    {
        "customerId": "Test",
        "monthlyPoints": {
            "2026-04": 250.00,
            "2026-03": 50.00
        },
        "totalPoints": 300.00
    }
]

URL:--http://localhost:8087/api/rewards/calculate?startDate=2026-04-01&endDate=2026-04-10

Response---
[
    {
        "customerId": "Test",
        "monthlyPoints": {
            "2026-04": 250.00
        },
        "totalPoints": 250.00
    }
]


URL:--http://localhost:8087/api/rewards/calculate?months=1

[
    {
        "customerId": "Test",
        "monthlyPoints": {
            "2026-04": 250.00
        },
        "totalPoints": 250.00
    }
]

##Prerequisites
- Java 8
- Maven 3+
- Spring Boot 3.2.5
- MySQL 









