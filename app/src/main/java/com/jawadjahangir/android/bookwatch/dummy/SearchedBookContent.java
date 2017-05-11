package com.jawadjahangir.android.bookwatch.dummy;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class SearchedBookContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<Book> ITEMS = new ArrayList<Book>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, Book> ITEM_MAP = new HashMap<String, Book>();

    private static final int COUNT = 25;

    static {
        // Add some sample items.
//        for (int i = 1; i <= COUNT; i++) {
//            addItem(createDummyItem(i));
//        }
    }

    private static void addItem(Book item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

//    private static Book createDummyItem(int position) {
//        return new Book(String.valueOf(position), "Item " + position, makeDetails(position));
//    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class Book implements Parcelable{
        public String id;
        public String title;
        public String author;
        public String thumbnail;
        public double avg_rating;

        @Override
        public void writeToParcel(Parcel out, int flags) {
            out.writeString(id);
            out.writeString(title);
            out.writeString(author);
            out.writeString(thumbnail);
            out.writeDouble(avg_rating);
            //out.writeParcelable(mInfo, flags);
        }

        private Book(Parcel in) {
            id = in.readString();
            title = in.readString();
            author = in.readString();
            thumbnail = in.readString();
            avg_rating = in.readDouble();
            //mInfo = in.readParcelable(MySubParcelable.class.getClassLoader());
        }


        public Book(String id, String title, String author, String thumbnail, double avg_rating ) {
            this.id = id;
            this.title = title;
            this.author = author;
            this.thumbnail = thumbnail;
            this.avg_rating = avg_rating;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Parcelable.Creator<Book> CREATOR
                = new Parcelable.Creator<Book>() {

            // This simply calls our new constructor (typically private) and
            // passes along the unmarshalled `Parcel`, and then returns the new object!
            @Override
            public Book createFromParcel(Parcel in) {
                return new Book(in);
            }

            // We just need to copy this and change the type to match our class.
            @Override
            public Book[] newArray(int size) {
                return new Book[size];
            }
        };

        @Override
        public String toString() {
            return title;
        }
    }

    public static class BookDetails {
        public String book_id;
        public String title;
        public String[] authors;
        public String publisher;
        public String publishedDate;
        public String description;
        public String[] isbn;
        public int pageCount;
        public String[] categories;
        public double avgRatings;
        public String smallThumbnail;
        public String thumbnail;

        public BookDetails(String id, String title, String[] author, String thumbnail, double avg_rating,
                           String publisher, String publishedDate, String description, String[] isbn, int pageCount,
                           String[] categories, String smallThumbnail) {
            this.book_id = id;
            this.title = title;
            this.authors = author;
            this.publisher = publisher;
            this.publishedDate = publishedDate;
            this.description = description;
            this.isbn = isbn;
            this.pageCount = pageCount;
            this.categories = categories;
            this.avgRatings = avg_rating;
            this.smallThumbnail = smallThumbnail;
            this.thumbnail = thumbnail;
        }

        @Override
        public String toString() {
            return title + " page count: " + pageCount;
        }
    }

    public static class NonParcelBook {
        public int _id;
        public String title;
        public String author;
        public String thumbnail;
        public String shelfName;


        public NonParcelBook(int _id, String title, String author, String thumbnail, String shelfName) {
            this._id = _id;
            this.title = title;
            this.author = author;
            this.thumbnail = thumbnail;
            this.shelfName = shelfName;
        }


    }
}
