@Users
Feature: Users
  As a user
  I want to interact with users
  So that I might be able to work with people

  Scenario Outline: Create a new user
    Given I am an authenticated user
    And I have a user
    And The user has username with <number> characters
    And The user has password with <number> characters
    When I submit the user
    Then I should receive a success response

    Examples:
      | number |
      | 255    |
      | 1      |
      | 25     |
      | 52     |
      | 250    |
      | 205    |

  Scenario Outline: Create a user without required data
    Given I am an authenticated user
    And I have a user
    And The user has username with <number> characters
    When I submit the user
    Then I should receive an InvalidPayloadException

    Examples:
      | number |
      | 0      |
      | 256    |

  Scenario: Get a user
    Given I am an authenticated user
    And A user exists
    When I get the user
    Then I should receive a success response
    And A user should be received

  Scenario: Update an existing user
    Given I am an authenticated user
    And A user exists
    And The user's username is modified to be 50 characters
    And The user's password is modified to be 55 characters
    When I submit the user for update
    Then I should receive a success response

  Scenario: Delete a user
    Given I am an authenticated user
    And A user exists
    When I delete the user
    Then I should receive a success response
    And The user should be removed

  Scenario Outline: Create a user as an unauthenticated user
    Given I have a user
    And The user has username with <number> characters
    And The user has password with <number> characters
    When I submit the user
    Then I should receive an unauthenticated response

    Examples:
      | number | componentType |
      | 15     | SLACK         |

  Scenario: Get a user as an unauthenticated user
    Given A user exists
    When I get the user
    Then I should receive an unauthenticated response

  Scenario: Update an existing user as an unauthenticated user
    Given A user exists
    And The user's username is modified to be 50 characters
    And The user's password is modified to be 55 characters
    When I submit the user for update
    Then I should receive an unauthenticated response

  Scenario: Delete a user as an unauthenticated user
    Given A user exists
    When I delete the user
    Then I should receive an unauthenticated response

  Scenario: Check password to see if it has been compromised
    Given I have a password
    When I check a password to see if it has been compromised
    Then I should receive a success response
    And A count of the number of times the password has been seen is received

  Scenario: Check to see if a username is available
    Given I have a username
    When I check a username to see if it has been used
    Then I should receive a success response
    And A flag indicating whether or not the username has been used is received

  Scenario: Get all users
    When I get all users
    Then I should receive a success response
    And A list of users is received

  Scenario: Verifies a user's notification settings for a given NotificationType
    When I verify a user's notification settings for a given NotificationType
    Then I should receive a success response

  Scenario: Updates a user's password
    When I update a user's password
    Then I should receive a success response

  Scenario: Start the user password reset process
    When I start the user password reset process
    Then I should receive a success response

  Scenario: Logout
    When I logout
    Then I should receive a success response
