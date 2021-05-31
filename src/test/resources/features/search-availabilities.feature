Feature: as a patient I can search for availabilities of a specific provider

  Background:
    Given we are in 'Princeton Plainsboro Clinic'
    And following providers exist:
      | id | firstname | lastname | occupation    |
      | 1  | Gregory   | House    | diagnostician |
    And following patients exist
      | id | firstname | lastname |
      | 1  | Michael   | Scott    |
      | 2  | Dwight    | Schrute  |
    And following timeslots exist:
      | start | end   |
      | 8:00  | 08:15 |
      | 8:15  | 08:30 |
      | 8:30  | 08:45 |


  Scenario Outline: validate request parameters
    When I call GET method for /v1/providers/1/availabilities?<requestParams>
    Then the status code should be 400
    And the status message should contain '<statusMessage>'

    Examples:
      | requestParams                 | statusMessage                                               |
      |                               | \'from\' for method parameter type LocalDate is not present |
      | from=2021-05-29               | \'to\' for method parameter type LocalDate is not present   |
      | to=2021-05-29                 | \'from\' for method parameter type LocalDate is not present |
      | from=2021-05-10&to=2021-05-03 | from date is after to date                                  |


  Scenario: get availabilities of provider who has no appointments
    Given no appointments exist
    When I call GET method for /v1/providers/1/availabilities?from=2021-05-10&to=2021-05-11
    Then the status code should be 200
    And the response should have following time slots:
      | date       | start | end   |
      | 2021-05-10 | 08:00 | 08:15 |
      | 2021-05-10 | 08:15 | 08:30 |
      | 2021-05-10 | 08:30 | 08:45 |


  Scenario: get availabilities of provider who has one timeslot available
    Given following appointments exist:
      | date       | patient | provider | timeSlot  |
      | 2021-05-10 | Scott   | House    | 8:00-8:15 |
      | 2021-05-10 | Schrute | House    | 8:15-8:30 |

    When I call GET method for /v1/providers/1/availabilities?from=2021-05-10&to=2021-05-11
    Then the status code should be 200
    And the response should have following time slots:
      | date       | start | end   |
      | 2021-05-10 | 08:30 | 08:45 |
