# Medical Appointment Booking Application

Booking system for clinics. Provides following functionality:

 - A patient can search for the different providers of the clinic
   
    ```bash
   $ curl --location --request GET 'http://localhost:8080/v1/providers?occupation=diagnostician'
    ```
   
 - A patient can look for the availabilities of a specific provider within a defined time interval (for
instance, the availabilities of Dr. A between May 8th, 2019 and May 12th, 2019)
   
    ```bash
   $ curl --location --request GET 'http://localhost:8080/v1/providers/1/availabilities?from=2021-06-01&to=2021-06-02'
   ```
   
 - A patient can book an appointment with a provider by selecting one of their availabilities

    ```bash
    $ curl --location --request POST 'http://localhost:8080/v1/providers/1/appointments' \
    --header 'Content-Type: application/json' \
    --data-raw '{
    "date": "2021-05-30",
    "timeSlotId": 3,
    "patientId": 1
    }'
    ```

## Features

 - Spring Boot
 - H2 / MySQL
 - Docker 
 - Cucumber / MockMVC

## How to run locally

Clone the repo and then execute:

```bash
$ cd ./booapp
$ ./mvnw package
$ java -jar ./target/booapp-0.0.1-SNAPSHOT.jar
```

## How to run in a container

Prerequisite:

 - `docker` v.20.10.6
 - `docker-compose` v.1.29

Clone the repo and then run:

```bash
$ docker-compose up
```

When application starts it is seeded with following data:

### clinic

|DESCRIPTION|NAME|
|---| --- |
|Just a clinic | Princeton Plainsboro Clinic|

### timeslot

|start | end|
|---| --- |
| 08:00:00 | 08:15:00 |
| 08:15:00 | 08:30:00 |
| 08:30:00 | 08:45:00 |
| 08:45:00 | 09:00:00 |
| 09:00:00 | 09:15:00 |

### patient

| first_name | last_name |
|---|---|
| Dwight | Schrute |
| Michael | Michael Scott |
| Oscar | Martinez |
| Kevin | Malone |

### provider

| FIRST_NAME | LAST_NAME | OCCUPATION | CLINIC |
|---|---|---|---|
| Gregory | House | diagnostician | 1 |
| Lisa | Cuddy | neurologist | 1 |
| James | Wilson | oncologist | 1 |
| Eric | Foreman | neurologist | 1 |
| Robert | Chase | surgeon | 1 |


### appointment

| DATE | PATIENT | PROVIDER | TIME_SLOT |
|---|---|---|---|
| 2021-06-01 | 1 | 1 | 1 | 
| 2021-06-01 | 2 | 2 | 2 | 
| 2021-06-01 | 3 | 1 | 3 | 
| 2021-06-01 | 4 | 3 | 3 | 



# Things to improve / not finished

 - Swagger docs
 - As timeslots won't change often, they could be stored in a static map instead of a table. Another way could be to cache `TimeSlotRepository#findAll` method using `@Cacheble` annotation.

# Notes

Time spent 5-6 hours.   
Implemented using TDD with Cucumber.   
I decided not to add swagger documentation, as the app exposes just 3 endpoints, but I attached a Postman collection.  
No unit tests as the service layer does not contain a lot of logic.
