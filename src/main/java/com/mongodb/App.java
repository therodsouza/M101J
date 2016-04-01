package com.mongodb;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Arrays;
import java.util.Date;

import static com.mongodb.util.Helper.printJson;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        MongoClient client = new MongoClient("localhost", 27017);
        MongoDatabase db = client.getDatabase("test");

        MongoCollection<BsonDocument> coll = db.getCollection("people", BsonDocument.class);

        Document doc;
        doc = new Document()
                .append("str", "MongoDB, Hello")
                .append("int", 42)
                .append("l", 1L)
                .append("double", 2.2D)
                .append("boolean", true)
                .append("date", new Date())
                .append("objectId", new ObjectId())
                .append("null", null)
                .append("embeded", new Document("x", 0))
                .append("list", Arrays.asList(1, 2, 3));

        printJson(doc);
    }
}
