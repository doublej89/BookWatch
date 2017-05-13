# BookWatch
An app for cataloging and searching for books the user owns

The user can update bookmarks from the book he/she is reading, and the user can also scan barcodes to retrieve info
about the book from Google Books

Obtain your Google Books API key and add the following line to gradle.properties:
MyGoogleBooksApiKey = "api_key"

Using the Home Screen Widget:

If the user has stored book references in the "Reading" shelf, that means the user is reading the books corresponding 
to the references. Clicking on that shelf in the main page would open an interface that would allow the user to regularly
update the bookmark (the page number at which the user last left off) as the user makes regular progress with the book.
When the user makes the update, the homescreen widget will be updated as well. The homescreen widget will show the title,
book cover, last bookmarked, and the date & time of the last bookmark of the book the user is currently reading.
