Room ORM Migration Example Android App

This Android application serves as an exploration of how migrations work with the Room ORM library, specifically in the context of adding a new column to an existing database. The app has been enhanced from the workshop version to facilitate the addition of a new attribute to the existing entity (e.g., Title).
Key Features:

    Navigation Between Fragments: The app demonstrates the implementation of fragment navigation, allowing users to seamlessly move between different sections of the application.

    Gallery Integration and Local Image Storage: Users can connect with the device's image gallery, selecting images, and locally storing them within the application.

    RecyclerView and CardView for Entity List: The app utilizes RecyclerView and CardView to present a visually appealing list of entities with the newly added attributes. This enhances the user experience in browsing and interacting with the data.

    Picasso Library for Image Handling: The Picasso library is employed to efficiently load and display images. It includes transformations to enhance the visual presentation of images within the app.

    Room ORM Architecture: The app follows the Room ORM architecture, showcasing how to handle database operations and migrations seamlessly, ensuring data integrity while evolving the data model.

Migration Context:

The main focus of the enhancements is on the migration process, especially when introducing a new attribute (Title) to an existing entity. During the defense, we'll provide a detailed explanation of the following:

    Step-by-step migration process: Understanding the process of migrating the database schema using Room ORM.

    Demonstration of database evolution: How the app accommodates the addition of the new attribute without losing existing data.

How to Use:

To explore the migration example, clone this repository and open the project in Android Studio. Run the app on an emulator or physical device to interact with the features mentioned above.
