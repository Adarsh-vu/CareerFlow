# CareerFlow – AI-Powered College Discovery and Career Guidance Platform

A native Android application that combines college discovery, aptitude assessment and AI-powered career guidance to help students make informed higher-education decisions.

## Overview

Choosing the right college and career path can be challenging for students. Information about colleges, courses, entrance examinations, fees and career opportunities is often spread across multiple platforms, making it difficult to evaluate available options.

**CareerFlow** addresses this problem by bringing college discovery and personalized career guidance together in a single mobile application.

Built using Kotlin and Firebase, CareerFlow provides a structured college search experience alongside AI-powered aptitude assessment and a conversational career guide named **Mentra**. The platform helps students explore educational opportunities, understand their strengths and organize their academic goals.

## Key Features

### 1. College Discovery

Explore colleges and universities through a curated college database.

- Search colleges by name.
- Filter colleges by academic stream.
- Refine results using location and budget preferences.
- Explore entrance examination requirements.
- View relevant college information.
- Compare selected colleges using key information.

### 2. AI-Powered Aptitude Assessment

CareerFlow provides an interactive aptitude assessment covering three major areas:

- Logical reasoning.
- Quantitative aptitude.
- Verbal ability.

The assessment uses Gemini-powered AI capabilities to analyze student responses and generate personalized insights.

### 3. Personalized AI Analysis Report

After completing the aptitude assessment, students receive an AI-generated analysis containing:

- Personal strengths and weaknesses.
- Recommended academic streams.
- Suitable courses.
- Skill development guidance.
- Potential career pathways.
- College suggestions based on the student's profile and performance.

The generated report is presented in a structured, readable format within the application.

### 4. Mentra – AI Career Guide

Mentra is CareerFlow's conversational AI assistant, designed to help students explore educational and career-related questions.

Students can use Mentra to:

- Ask questions about colleges and courses.
- Explore career opportunities.
- Understand entrance examinations.
- Seek guidance based on their academic preferences.
- Continue conversations using stored chat history.

The chatbot uses Gemini through the Firebase Vertex AI integration and incorporates contextual information to provide more relevant responses.

AI-generated guidance should be treated as assistance rather than a replacement for independently verifying admission requirements and educational information.

### 5. Career Planner and Wishlist

CareerFlow also includes tools that help students organize their academic journey.

- Maintain a personalized college wishlist.
- Keep track of selected educational options.
- Plan academic milestones.
- Organize progress towards career goals.

### 6. User Authentication and Profile Management

The application supports:

- Email and password authentication.
- Google Sign-In.
- User profile management.
- Persistent user information.
- Cloud-based data synchronization.

## Application Architecture

CareerFlow follows the **Model-View-ViewModel (MVVM)** architecture with the Repository Pattern.

This approach separates the user interface, application logic and data-access responsibilities.

### Architecture Layers

| Layer | Responsibility |
|---|---|
| View | Android XML layouts and UI components |
| ViewModel | Manages UI-related application logic and state |
| Repository | Abstracts data sources and backend interactions |
| Model | Represents application data and domain entities |
| Firebase | Provides authentication, cloud database and AI integration |

The Repository Pattern helps separate data-access logic from the ViewModel, making the application easier to maintain and extend.

## AI Integration

CareerFlow integrates Google's Gemini models through Firebase Vertex AI.

The AI integration supports two primary workflows:

**Aptitude Analysis**

```text
Aptitude Assessment
        ↓
Student Responses
        ↓
Gemini AI Analysis
        ↓
Personalized Report
        ↓
Display in Application
```

**Mentra Conversational Assistant**

```text
User Query
    ↓
ChatViewModel
    ↓
Gemini API with Context
    ↓
Response Processing
    ↓
Display in Chat Interface
```

These workflows enable AI-assisted assessment and conversational guidance within the Android application.

## Technology Stack

| Category | Technologies |
|---|---|
| Platform | Android |
| Programming Language | Kotlin |
| Architecture | MVVM, Repository Pattern |
| UI | Android XML, Material Design 3 |
| UI Binding | View Binding |
| Asynchronous Operations | Kotlin Coroutines |
| Authentication | Firebase Authentication |
| Database | Cloud Firestore |
| AI Integration | Firebase Vertex AI, Google Gemini |
| UI Components | RecyclerView, Navigation Component |
| Development Environment | Android Studio, Gradle |

## Data Management

CareerFlow uses Firebase Firestore as its cloud-based NoSQL database.

The application maintains information associated with:

- User profiles.
- College information.
- Student preferences.
- Aptitude assessment results.
- Wishlists.
- Career guidance interactions.

Firebase Authentication handles user identity, while Firestore supports persistent data storage and synchronization.

## User Interface

The application uses Android XML layouts and Material Design 3 components to provide a consistent mobile experience.

Its interface is organized around the main student activities:

- Home dashboard.
- College search and discovery.
- College details and comparison.
- Aptitude assessment.
- Personalized AI report.
- Mentra chatbot.
- User profile.
- Career planning.

## Testing and Results

The project report describes functional testing of the major application modules.

The reported testing covers:

- User authentication.
- College search and filtering.
- AI-powered aptitude reporting.
- Mentra conversational interactions.
- Profile and application functionality.

The report records successful operation of the core application features under typical testing conditions.

## Repository Structure

The repository follows an Android Gradle project structure.

```text
CareerFlow/
├── app/
├── gradle/
├── .gitignore
├── build.gradle.kts
├── gradle.properties
├── gradlew
├── gradlew.bat
└── settings.gradle.kts
```

## Getting Started

### Prerequisites

- Android Studio.
- Android SDK.
- JDK compatible with the project's Gradle configuration.
- Firebase project with the required services enabled.
- Gemini access through Firebase Vertex AI.

### Setup

1. Clone the repository:

```bash
git clone https://github.com/Adarsh-vu/CareerFlow.git
```

2. Open the project in Android Studio.

3. Allow Gradle to synchronize the project and download its dependencies.

4. Configure the required Firebase project settings and Android application configuration.

5. Ensure the required Firebase services are enabled and configured.

6. Configure the Gemini integration through Firebase Vertex AI.

7. Build and run the application using an Android emulator or a connected Android device.

**Note:** Local Android SDK configuration and generated build files are excluded from version control. Firebase configuration and service availability must be verified separately before running the application.

## Future Enhancements

Potential improvements identified in the project include:

- A proactive machine-learning recommendation engine that learns from student interactions and assessment outcomes.
- Community features connecting students, teachers and educational consultants.
- Multilingual support.
- AI-driven scholarship suggestions.
- Examination preparation recommendations.

These are identified as future enhancements and should not be confused with the existing application features.

## Author

**Adarsh V U**

MCA Project – Department of Computer Applications  
Rajiv Gandhi Institute of Technology, Kottayam

## License

No license has been specified for this repository. Please contact the repository owner regarding reuse or distribution.
