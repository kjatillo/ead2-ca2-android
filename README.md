# Meal Planner Application

A full-stack meal planning application consisting of a .NET Web API backend and an Android client application.

## Table of Contents
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Setup Instructions](#setup-instructions)
- [Key Features](#key-features)
- [Testing](#testing)
- [Contributors](#contributors)

## Project Structure

The solution consists of two main projects:

### 1. EAD2_CA2.Api (.NET Web API)
- Backend API built with .NET 8.0
- Entity Framework Core for data access
- SQL Server database
- Swagger UI for API documentation
- Repository pattern implementation

### 2. EAD2_CA2.AndroidClient (Android App)
- Modern Android application
- Minimum SDK: 24 (Android 7.0)
- Target SDK: 35
- Written in Java

## Prerequisites

### Backend Requirements
- .NET 8.0 SDK
- SQL Server
- Visual Studio 2022 or VS Code with C# extensions

### Android Requirements
- Android Studio
- JDK 11
- Android SDK with API level 35
- Gradle 8.x

## Setup Instructions

### Backend Setup
1. Clone the repository
2. Navigate to the `EAD2_CA2.Api` directory
3. Update the connection string in `appsettings.json`
4. Run database migrations:
   ```bash
   dotnet ef database update
   ```
5. Start the API:
   ```bash
   dotnet run
   ```
6. Access the API at `https://localhost:{port}`
7. API documentation is available at `https://localhost:{port}/swagger/index.html`

### Android Client Setup
1. Open the `EAD2_CA2.AndroidClient` directory in Android Studio
2. Sync the project with Gradle files
3. Ensure the API base URL in `ApiServiceGenerator.java` points to your running backend
4. Build and run the application on an emulator or physical device

## Key Features

- Browse, search, and filter recipes by various criteria
- Create and manage meal plans
- View detailed recipe information
- Responsive and intuitive user interface
- RESTful API with complete CRUD operations

## Testing

The Android application includes Espresso framework for automated end-to-end testing of critical user flows

## Contributors

**Jorune Sveikauskaite**  
Student ID: X00187267  
Email: x00187267@mytudublin.ie  

**Keneith Atillo**  
Student ID: X00190944  
Email: x00190944@mytudublin.ie
