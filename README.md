# Reddit Android Mobile app Take-home Assessment

Submission : Praneeth Jataprolu
Github handle : JVSSPraneethGithub

### System Requirements:

* Android Studio Meerkat | 2024.3.2
* Android SDK - Max API 36, Min API 28

### Configuration:

* Gradle: 8.14
* AGP: 8.10.0
* Kotlin: 2.1.20
* Java-version: 11

**In-case of Build-failures** : Please delete ```` $HOME/.gradle ```` folder, ```` $HOME/.m2 ```` folder, as well as all folders listed in ```` .gitignore ```` file.

### Design Considerations:

* Clean architecture (Jetpack Compose, Jetpack ViewModel, Dagger-Hilt, Domain Use-case) with
  Unidirectional Data-flow.
* SOLID, GRASP, DRY and KISS, no over-engineering, no over-complications.
* Android Paging-3, Pull-to-Refresh.

### Acceptance Criteria:

* Rick and Morty characters-list, fetched using Paging-3 from API with Infinite scrolling / Pagination.
* Progress-Indicator displayed without blocking UI ( API fetching is lightning-fast )
* No Crashes or ANRs.

### Extra credits included:

* Device Theme ( Color-scheme ) support.
* Gradle Kotlin-script build files.
* Gradle Version-Catalog.
* Accessibility Talk-back support.
* 2 Baseline Unit-tests included - Paging Source, and View-Model.

  ````./gradlew testDebugUnitTest````

**Please note**: UI-Test explicitly ignored due to
issue - https://issuetracker.google.com/issues/372932107