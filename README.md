# Lock Screen Permission App

This repository contains a sample Android application designed to illustrate the management of various permissions essential for implementing an app lock feature. The application is built using Jetpack Compose, a modern toolkit for building native UI in Android, enabling a responsive and visually appealing user interface. The primary goal of this project is to demonstrate best practices in permission handling while providing a secure mechanism to lock specific applications, ensuring that sensitive data remains protected.

The app serves as a practical example for developers interested in integrating app lock features into their Android applications. It provides a clear and accessible way for users to manage permissions, understand their importance, and utilize them effectively to safeguard their applications from unauthorized access.

## Classes Overview

### 1. `MainActivity : ComponentActivity()`

`MainActivity` serves as the primary entry point of the application, orchestrating the initial setup and navigation. This class is responsible for managing user interactions regarding permission requests, ensuring that users have all necessary permissions to access the app's functionality fully.

Upon launching the app, `MainActivity` conducts an immediate check for critical permissions, including overlay permissions, usage access, notification access, and accessibility services. It prompts the user to grant these permissions if they have not already been granted. Each permission is visually represented through a `PermissionRow`, providing users with clear feedback on the current status of their permissions.

The `MainActivity` also incorporates the `NavHost`, which facilitates smooth navigation between different screens in the app, including the `WelcomeScreen`, `MainScreen`, and `ShowAppList`. This allows users to navigate seamlessly while also providing a responsive UI that adapts to user interactions.

In addition to managing permissions, `MainActivity` includes helper functions that validate the overall state of permissions, ensuring that the app's core features can be accessed without interruption. By implementing a user-friendly design and clear prompts, the app aims to enhance the user experience while effectively guiding them through the necessary steps to grant permissions.

### 2. `LockScreenActivity : AppCompatActivity()`

`LockScreenActivity` is dedicated to displaying a custom lock screen designed to protect sensitive applications. This activity is crucial in implementing a secure passcode input system, allowing users to enter a passcode to unlock specific applications they wish to protect. 

The lock screen UI is crafted to be both visually appealing and highly functional, featuring numeric buttons representing each digit of the passcode. Users can easily input their passcode, with a dedicated submit button (`tick`) to validate their entry. The activity interacts with a local database (`AppDatabaseHelper`) to retrieve the correct passcode associated with the locked application.

When users submit their passcode, `LockScreenActivity` compares the entered value with the stored passcode. If the passcode is correct, the lock screen is dismissed, granting access to the application. In cases where the passcode is incorrect, users receive immediate feedback through an error message, encouraging them to retry. This mechanism ensures that only authorized users can access locked applications, providing a layer of security and control over personal data.

### 3. `PermissionManager`

The `PermissionManager` class is a utility class responsible for handling all permission-related tasks. It encapsulates the logic for checking whether specific permissions are granted, requesting permissions from the user, and providing callbacks to inform the calling class of permission results.

By centralizing permission management within this class, the app maintains a clean and organized code structure. The `PermissionManager` helps ensure that permissions are handled consistently throughout the app, reducing the risk of errors and enhancing maintainability.

### 4. `LockService : Service()`

`LockService` is a background service that plays a critical role in monitoring and enforcing the app lock functionality. This service continuously runs in the background, allowing it to detect when a user attempts to access a locked application. By utilizing system events, `LockService` identifies any interaction with apps that are designated as protected.

When a locked application is opened, `LockService` triggers the display of the `LockScreenActivity`, effectively covering the screen and prompting the user to enter their passcode. This seamless transition ensures that unauthorized access is prevented, providing users with peace of mind regarding their data security.

In addition to detecting app launches, `LockService` manages the lifecycle of the lock screen, ensuring it is appropriately displayed and dismissed based on user interactions. It acts as a gatekeeper, providing an added layer of protection by enforcing the passcode input system without requiring constant user intervention.

### 5. `AccessibilityService : AccessibilityService()`

The `AccessibilityService` class is utilized to monitor user interactions with the device, allowing the app to track when users switch between different applications. By leveraging accessibility features, this service enhances the app's capability to detect app launches and switch events.

The primary function of the `AccessibilityService` is to listen for events that indicate a user has navigated to a new application. When the user switches to a locked app, the `AccessibilityService` communicates with `LockService` to activate the lock screen, prompting the user to enter their passcode.

This integration ensures that users are consistently protected as they navigate their device. The `AccessibilityService` is essential for creating a smooth and secure experience, enabling the app to respond dynamically to user actions and maintain the integrity of the app lock feature.

### 6. `DatabaseHelper : SQLiteOpenHelper`

`DatabaseHelper` is a critical class responsible for managing the local SQLite database used to store passcodes and related application data securely. By extending `SQLiteOpenHelper`, this class facilitates the creation, updating, and querying of the database efficiently.

The `DatabaseHelper` class includes methods to initialize the database schema, insert new passcodes, update existing ones, and retrieve passcodes associated with specific applications. This functionality ensures that users can easily set and manage their passcodes for various locked applications.

Moreover, `DatabaseHelper` handles the database's lifecycle events, including creation and version management. This ensures that any changes to the database structure are seamlessly integrated without disrupting the user experience. By encapsulating all database interactions, `DatabaseHelper` simplifies data management and enhances code readability.

### 7. `ContentProvider : ContentProvider()`

`ContentProvider` is a component that serves as an interface for managing access to the application's data, allowing for data sharing between different apps while enforcing security measures. This class provides methods for querying, inserting, updating, and deleting passcode data stored in the local database.

By implementing the `ContentProvider` interface, this class facilitates interaction with the SQLite database in a standardized way. It abstracts the underlying database operations, allowing other components within the app to access passcode information without directly interacting with the database itself.

The `ContentProvider` plays a vital role in maintaining data integrity and security. It ensures that only authorized components can access sensitive information, such as passcodes, providing an additional layer of protection against unauthorized data access. This design pattern promotes separation of concerns, making the app more modular and easier to maintain.

## Features

- **Comprehensive Permission Management**: The app systematically requests and manages essential permissions upon startup, ensuring users are informed of any missing permissions that are crucial for the app's functionality. It covers various permission types, including overlay, usage access, notification, and accessibility services, creating a holistic permission management system.

- **Custom Lock Screen UI**: The app features a visually appealing and intuitive lock screen interface that allows users to securely input their passcodes. The design emphasizes usability and aesthetics, providing a modern look that enhances user engagement.

- **Local Database Integration**: The app employs a lightweight local database to store and manage passcodes securely. This functionality allows users to customize their experience by locking or unlocking applications based on their preferences. The database ensures that passcodes are securely stored and easily retrievable.

- **Real-time Permission Feedback**: Users receive instant visual feedback regarding the status of each permission, enhancing their awareness and facilitating prompt action where necessary. This feedback mechanism ensures that users are always informed about the permissions required for the app's full functionality.

- **Seamless Navigation**: The use of Jetpack Compose and Navigation Components ensures a smooth and responsive user experience as users navigate through different screens in the app. Transitions between screens are designed to be fluid, contributing to an overall polished user experience.

## Usage

To utilize the app effectively, users must grant all necessary permissions when prompted. Upon launching the app for the first time, users will encounter a series of permission requests. It is essential to grant these permissions to enable the full functionality of the app. After successfully granting the required permissions, users can lock specific applications by entering a unique passcode, which is stored and managed in the app's local database.

