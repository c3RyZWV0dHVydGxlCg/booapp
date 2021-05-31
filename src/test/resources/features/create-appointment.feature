Feature: as a patient I can create appointments

  Background:
    Given we are in 'Princeton Plainsboro Clinic'
    And following providers exist:
      | firstname | lastname | occupation    |
      | Gregory   | House    | diagnostician |
    And following patients exist
      | firstname | lastname | occupation    |
      | Homer     | Simpson  | diagnostician |
    And following timeslots exist:
      | start | end   |
      | 8:00  | 08:15 |
      | 8:15  | 08:30 |
      | 8:30  | 08:45 |


  Scenario: book an appointment with a provider who does not exist
    Given following providers exist:
      | id | firstname | lastname | occupation    |
      | 1  | Gregory   | House    | diagnostician |
    When I call POST method for /v1/providers/100/appointments with body
    """
    {
      "date": "2021-05-30",
      "timeSlotId": 1,
      "patientId": 1
    }
    """
    Then the status code should be 404
    And the status message should contain 'Provider with id 100 does not exist'


  Scenario Outline: validate request
    Given following providers exist:
      | id  | firstname | lastname | occupation    |
      | 200 | James     | Wilson   | diagnostician |
    When I call POST method for /v1/providers/200/appointments to create a following appointment
      | date   | timeSlot     | patientId   |
      | <date> | <timeSlotId> | <patientId> |
    Then the status code should be 400
    And the status message should contain '<message>'
    Examples:
      | date       | timeSlotId  | patientId | message                               |
      | 2021-05-30 | 21:00-21:15 | Simpson   | Timeslot with given id does not exist |
      | 2021-05-30 | 8:00-8:15   | Flanders  | Patient with given id does not exist  |


  Scenario: create appointment which already exists
    Given following provider exist:
      | id  | firstname | lastname | occupation    |
      | 300 | James     | Wilson   | diagnostician |
    And following appointment exist:
      | date       | patient | provider | timeSlot  |
      | 2021-05-30 | Simpson | Wilson    | 8:00-8:15 |
    When I call POST method for /v1/providers/300/appointments to create a following appointment
      | date       | timeSlot  | patient |
      | 2021-05-30 | 8:00-8:15 | Simpson |
    Then the status code should be 409


  Scenario: create appointment
    Given following provider exist:
      | id  | firstname | lastname | occupation    |
      | 200 | James     | Wilson   | diagnostician |
    And no appointments exist
    When I call POST method for /v1/providers/200/appointments to create a following appointment
      | date       | timeSlot  | patient |
      | 2021-06-01 | 8:00-8:15 | Simpson |
    Then the status code should be 200
    And following appointment should exist:
      | date       | patient | provider | timeSlot  |
      | 2021-06-01 | Simpson | Wilson   | 8:00-8:15 |

