Feature: as a patient I can search for providers

  Background:
    Given we are in 'Princeton Plainsboro Clinic'

  Scenario: list all providers, but none exist
    Given no providers exist
    When I call GET method for /v1/providers
    Then the status code should be 200
    And the response should be
    """
    []
    """


  Scenario: list all providers
    Given following providers exist:
      | id | firstname | lastname | occupation    |
      | 1  | Gregory   | House    | diagnostician |
      | 2  | James     | Wilson   | oncologist    |
    When I call GET method for /v1/providers
    Then the status code should be 200
    And the response should contain providers with last names
    """
    Wilson,House
    """


  Scenario Outline: search providers
    Given following providers exist:
      | id | firstname | lastname | occupation    |
      | 1  | Gregory   | House    | diagnostician |
      | 2  | Lisa      | Cuddy    | neurologist   |
      | 3  | James     | Wilson   | oncologist    |
      | 4  | Eric      | Foreman  | neurologist   |
      | 5  | Robert    | Chase    | surgeon       |
    When I call GET method for /v1/providers?occupation=<occupation>
    Then the status code should be 200
    And the response should contain providers with last names
    """
    <names>
    """

    Examples:
      | occupation  | names         |
      | surgeon     | Chase         |
      | SURGEON     | Chase         |
      | neurologist | Cuddy,Foreman |
      | dentist     |               |
