#noinspectionLocalDateTime start=LocalDateTime.parse(nextClass.getStartTime()); CucumberPlusUndefinedStep
Feature: Meal plan

  as a User
  I want to see the meal plans for the current week

  Scenario: User has no internet
    Given The app is opened
    When The user opens the meal plan page
    And The user has no connection to the internet
    Then The meal plan page will show an error message

  Scenario: The meal plan server does not respond
    Given The app is opened
    When The user opens the meal plan page
    And The meal plan server does not respond
    Then The meal plan page will show an error message

  Scenario: Meal plans for each day of current week is displayed to the user
    Given The app is opened
    When The user opens the meal plan page
    And The meal plan server responds
    Then The meal plans for for each day of current week will be displayed