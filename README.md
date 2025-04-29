# Meal Planner Application

A full-stack meal planning application consisting of a .NET Web API backend and an Android client application.

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

## Getting Started

### Backend Setup
1. Clone the repository
2. Navigate to the `EAD2_CA2.Api` directory
3. Update the connection string in `appsettings.json`
4. Run the following commands:
   ```bash
   dotnet restore
   dotnet run
   ```
5. The API will be available at `https://localhost:{port}`
6. Swagger UI will be available at `https://localhost:{port}/swagger/index.html`

### Android Client Setup
1. Open the `EAD2_CA2.Api` directory in Android Studio
2. Sync the project with Gradle files
3. Update the API base URL in the app configuration
4. Build and run the application

## Features
- Meal planning
- RESTful API endpoints
- Modern Android UI 

## Developers
**Name**: Jorune Sveikauskaite <br />
**Student ID**: X00187267 <br />
**Student Email**: x00187267@mytudublin.ie <br />

<br />

**Name**: Keneith Atillo <br />
**Student ID**: X00190944 <br />
**Student Email**: x00190944@mytudublin.ie <br />
