- Start DiscoveryServiceApplication
- Start ServiceApplication
- Start Second instance of ServiceApplication on different port
- Start GatewayApplication


- Hit localhost:8080/service-demo/service-instances endpoint multiple times
    - Should see Zuul forwarded requests logged on both Ribbon load balanced ServiceApplications
- Shut down one instance of ServiceApplication
- Refresh endpoint multiple times
    - Should see fallback response triggered by Hystrix momentarily and then resolve as Eureka deregisters offline instance
- Bring ServiceApplication instance back up
- Refresh endpoint multiple times
    - Should start seeing load balanced requests on both instances again momentarily
