# BookWatch
An app for cataloging and searching for books the user owns

You can catalog book references accross different "shelves" which are given by default. You can look up book references from 
the web or by using your devices barcode scanner

You can update bookmarks (the page number at which the you left off) from the book you are reading allowing you to track your reading progress.

There's a special shelf known as "Reading", where if you store books the app will assume you are reading them, and therefore those books will have their own bookmark updating interface.

When you make the update, a homescreen widget associated with the app will be updated as well. The homescreen widget will show the title,book cover, last bookmarked page number, and the date & time of the last bookmark of the book you are currently reading.

# Installation

This app uses the Gradle build system.

First download the code by cloning this repository or downloading an archived snapshot. (See the options at the top of the page.)

In Android Studio, use the "Import non-Android Studio project" or "Import Project" option. Next select the directory in which you donwloaded this repository. If prompted for a gradle configuration accept the default settings.

Make sure the Android version in your device/emulator is at least 4.0.3 (API level 15).

You will need to obtain your Google Books API key and add the following line to gradle.properties:
MyGoogleBooksApiKey = "api_key"

Get your api key: https://developers.google.com/books/docs/v1/using#APIKey
