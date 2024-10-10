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

## Features

- **Comprehensive Permission Management**: The app systematically requests and manages essential permissions upon startup, ensuring users are informed of any missing permissions that are crucial for the app's functionality. It covers various permission types, including overlay, usage access, notification, and accessibility services, creating a holistic permission management system.

- **Custom Lock Screen UI**: The app features a visually appealing and intuitive lock screen interface that allows users to securely input their passcodes. The design emphasizes usability and aesthetics, providing a modern look that enhances user engagement.

- **Local Database Integration**: The app employs a lightweight local database to store and manage passcodes securely. This functionality allows users to customize their experience by locking or unlocking applications based on their preferences. The database ensures that passcodes are securely stored and easily retrievable.

- **Real-time Permission Feedback**: Users receive instant visual feedback regarding the status of each permission, enhancing their awareness and facilitating prompt action where necessary. This feedback mechanism ensures that users are always informed about the permissions required for the app's full functionality.

- **Seamless Navigation**: The use of Jetpack Compose and Navigation Components ensures a smooth and responsive user experience as users navigate through different screens in the app. Transitions between screens are designed to be fluid, contributing to an overall polished user experience.

- **User Guidance**: The app provides clear instructions and prompts to guide users through the permission-granting process, ensuring they understand the importance of each permission and how it contributes to the app's functionality. This educational approach enhances user confidence and promotes better understanding of app security features.

## Usage

To utilize the app effectively, users must grant all necessary permissions when prompted. Upon launching the app for the first time, users will encounter a series of permission requests. It is essential to grant these permissions to enable the full functionality of the app. After successfully granting the required permissions, users can lock specific applications by entering a unique passcode, which is stored and managed in the app's local database.

The app's interface is designed to be intuitive, allowing users to navigate easily between different screens and manage their locked applications effectively. Users are encouraged to explore the app's features by locking various applications and testing the passcode functionality. The app provides a straightforward interface for managing these settings, ensuring a user-friendly experience.


