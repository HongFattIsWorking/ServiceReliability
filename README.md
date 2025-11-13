# ServiceReliability ATS Submission 

this program is intended for ATS take home assignment submission 

## Quick Start Instructions 
Upon cloning the project please run the following command to run the application
``` 
docker compose up --build
``` 
Once the application started up, it will do a polling every 20 Seconds. 

to check the latest please do a GET request to the following path 
``` 
localhost:8080/service_status
``` 
## Design Overall 
-  Implemented as a scheduled background service to constantly poll for the url status to and store in the DB 
- Created an API to pull for the latest status based on the link and environment so the users can see the status for each environment 

Trade off 
- Requires networked access and healthy DB container. 
- Relational Database may not be the best choice in saving all the records 


## Infrastructure/deployment notes.
Deployment Architecture:

he monitoring application should be deployed within the application layer, behind the API Gateway.

The API Gateway handles request routing, authentication, and rate limiting, while the monitoring service continuously checks service health and availability without being affected by those limits.

This separation ensures that operational probes and internal health checks can still run even when external clients are rate-limited.
For deployment, the service can run as a container (e.g., in Kubernetes or Docker Compose) with appropriate environment configurations for database connectivity and metrics collection. It should also expose health endpoints (e.g., /actuator/health


