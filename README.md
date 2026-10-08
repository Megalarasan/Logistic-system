# Supermarket Workforce & Logistics System

A Java console application that coordinates two connected supermarket back-office domains: employee and shift management, and transport logistics. The system models employees, roles, availability, branches, drivers, trucks, transport areas, routes, and delivery records, with local persistence through Hibernate and SQLite.

This repository is a cleaned portfolio snapshot of the employee–transport integration developed for a university software-design project. Assignment submissions, student identifiers, IDE metadata, generated databases, compiled classes, and packaged binaries are intentionally excluded.

## Main features

- Create, update, find, and remove employees.
- Model employment details including roles, contracts, bank information, and weekly availability.
- Create upcoming shifts, assign employees by role, and retain past-shift records.
- Manage branches, suppliers used by logistics, drivers, trucks, and transport areas.
- Plan transports with routes, delivery files, time windows, driver eligibility, and truck-capacity constraints.
- Coordinate workforce and logistics rules, including driver availability and required stocker assignments.
- Load deterministic mock data for an immediately usable demonstration.
- Persist application state locally in SQLite through Hibernate ORM.
- Exercise domain and persistence behaviour with JUnit 5 tests.

## Architecture

The application follows a layered design:

```text
src/
├── main/
│   ├── java/
│   │   ├── Presentation/       Console menus and input handling
│   │   ├── Domain/             Employee, shift, and transport business rules
│   │   ├── DB/                 Persistence interfaces and controller implementations
│   │   ├── Models/             Database entities and data-transfer objects
│   │   ├── Auxiliary/          Shared configuration, validation, IDs, and result types
│   │   └── Main.java           Application entry point
│   └── resources/              Hibernate configuration
└── test/
    ├── java/                   Domain and persistence tests
    └── resources/              Isolated test-database configuration
```

The presentation layer calls domain managers rather than issuing database operations directly. Domain managers work through controller interfaces, allowing the same business logic to use either mock controllers or SQLite-backed implementations. Hibernate maps the domain entities and their relationships, while a separate test mode keeps test data out of the application database.

## Technologies

- Java 21
- Maven
- Hibernate ORM 6.6
- Jakarta Persistence (JPA)
- SQLite and the Xerial JDBC driver
- JUnit 5

## Setup and usage

### Prerequisites

- JDK 21 or newer
- Maven 3.9 or newer

### Build and test

```bash
git clone https://github.com/KulmanD/supermarket-workforce-logistics-system.git
cd supermarket-workforce-logistics-system
mvn clean verify
```

### Run

```bash
mvn package
java -jar target/supermarket-workforce-logistics-system-1.0.0.jar
```

At startup, choose whether to load demonstration data. Selecting `y` clears the application's local SQLite files before inserting a fresh sample dataset. You can then enter the transport system or the employee system. Example employee IDs in the demonstration dataset include `EMP002` for shift-management features and `EMP003` for HR features.

The application creates `losPollosHermanos.db` in the current working directory. Tests use the separate `losPollosHermanosTestingOnly.db` file. Both are ignored by Git.

## Notable technical decisions

- **Layered boundaries:** presentation, domain, persistence, and model responsibilities are separated into distinct packages.
- **Interface-driven persistence:** controller interfaces support both in-memory mock implementations and SQLite-backed implementations.
- **Cross-module rules:** transport planning consults employee availability and role data rather than duplicating workforce state.
- **Polymorphic roles:** employee roles are represented as a JPA inheritance hierarchy, supporting role-specific behaviour.
- **Isolated test storage:** a JUnit extension switches the application into test mode so persistence tests use a separate database.
- **Portable build:** Maven replaces IDE-specific dependency configuration and produces a runnable JAR without committing third-party binaries.

## Skills demonstrated

- Object-oriented analysis, inheritance, interfaces, and domain modelling
- Layered application architecture and separation of concerns
- Relational persistence with Hibernate/JPA and SQLite
- Transactional CRUD operations and entity relationships
- Integration of workforce scheduling with logistics planning
- Unit and persistence integration testing with JUnit 5
- Mock-data design, input validation, and console workflow development
- Maven dependency and build management
- Collaborative Git development and module integration

## Project provenance

The original system was created as a team academic project by Denis Coleman, Tom Gluzman, Ethen Krimer, and Yael Durahly. This portfolio snapshot focuses on the employee and transport integration. Denis Coleman's contribution history includes employee persistence and scheduling, test coverage, mock-data integration, and coordination between employee availability and transport planning.

All employee identifiers, contact details, addresses, bank-account values, and vehicle identifiers used by the demo and automated tests are synthetic placeholders.

The original team copyright and MIT license are preserved in [LICENSE](LICENSE).

## Limitations

- The user interface is console-based and assumes a single local operator.
- Employee access is selected by identifier and is not production-grade authentication or authorization.
- SQLite is suitable for a local demonstration, not concurrent multi-site deployment.
- Stock and supplier behaviour is represented only where needed by the transport workflow in this snapshot.
- Database schema evolution relies on Hibernate's automatic update mode rather than versioned migrations.
- Some legacy naming and static service access remain from the original academic implementation.

## Possible future improvements

- Expose the domain through a REST API and add a web interface.
- Add password-based authentication and role-based authorization.
- Introduce dependency injection and reduce static/global state.
- Add versioned database migrations and environment-based configuration.
- Expand stock and supplier modules into full implementations.
- Add broader validation, negative-path tests, coverage reporting, and continuous delivery.

## License

Licensed under the MIT License. See [LICENSE](LICENSE).
