# Backend Template for SEPM Group Phase

## Setup

### Checkstyle
If you use IntelliJ IDEA for backend development, install `Checkstyle-IDEA` from `Settings > Plugins`, then restart your IDE.
Afterwards, go to `Settings > Tools > Checkstyle` and under `Configuration file` press `+`. A new window will open.
Enter `checkstyle` as description, select `Use a local checkstyle file` and select the `checkstyle.xml` in the project root. Finally, check `Store relative to project location`.
Now press `Next` twice, then `Finish`. Now, enable the checkstyle by checking the checkbox to the left of its entry. Then press `OK`.

If you do not adhere to the rules, the IDE will make you aware immediately by showing some warning.

## How to run it

### Start the backed
`mvn spring-boot:run`

### Start the backed with test data
If the database is not clean, the test data won't be inserted

`mvn spring-boot:run -Dspring-boot.run.profiles=generateData`

## Development Guidelines

### DTOs
Use records if possible.
