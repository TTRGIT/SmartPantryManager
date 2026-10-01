Smart Pantry Manager

Smart Pantry Manager is a Java Android application that stores a user's pantry locally and suggests only recipes that can be cooked with the ingredients and quantities already available.

Practical lesson guide

See 402312357_Tlale_MobileAppDev700_Assignment.zip under APPLICATION’S SCREENSHOTS	section, a step-by-step Android Studio lesson covering project creation, every source/resource file, code-commenting practices, Room CRUD, testing, running, and APK generation.

See Smart Pantry Manager App Demo video for a timed live-demo and code-explanation narrative, including CRUD, strict matching, persistence, code-line pointers, and SQLite justification.

Features

- Add, view, edit and delete pantry ingredients.
- Persistent on-device SQLite storage through Room.
- 20 built-in recipes seeded into the database on first launch.
- Strict recipe suggestions: every required ingredient and sufficient quantity must be present.
- A separate Almost There section for recipes missing exactly one ingredient.
- Name normalization for common plural forms and aliases.
- Unit conversion across compatible mass (mg/g/kg), volume (ml/L/tsp/tbsp/cup) and count units.
- Expired ingredients are excluded from recipe matching.
- Recipe details with quantities and step-by-step preparation.
- Persistent expiry-alert and unit-preference settings.
- No maps, location, GPS, network, account or shopping functionality.

Project structure

- model: Room entities, relations and matching results.
- data: DAOs, Room database, repository and recipe seed data.
- util: ingredient normalization, unit conversion and strict matching.
- ui: activities, fragments, RecyclerView adapters and view model.

Run

Open this folder in Android Studio, allow Gradle to sync, select an emulator/device running Android 7.0 (API 24) or later, and run the app configuration.

The first launch creates smart_pantry.db and inserts the recipe collection. Pantry changes remain after the app is closed and reopened.

Matching rules

For every recipe ingredient, the matcher combines pantry rows with the same normalized name and converts compatible units before comparing the total quantity. A recipe appears under Ready to cook only when the missing list is empty. Recipes with exactly one missing or insufficient ingredient appear only under Almost There. Recipes missing two or more ingredients are not shown.
