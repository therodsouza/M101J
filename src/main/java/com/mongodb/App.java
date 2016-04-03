package com.mongodb;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.*;
import com.mongodb.client.result.UpdateResult;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.*;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.*;
import static com.mongodb.client.model.Sorts.descending;
import static com.mongodb.client.model.Updates.*;
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

        MongoCollection<Document> anotherColl = db.getCollection("people", Document.class);
        coll.drop();

        for (int i = 0; i < 10; i++) {
            anotherColl.insertOne(new Document()
                    .append("x", new Random().nextInt(2))
                    .append("y", new Random().nextInt(100)));
        }

        Document first = anotherColl.find().first();

        printJson(first);

        List<Document> all = anotherColl.find().into(new ArrayList<Document>());

        for (Document cur : all) {
//            printJson(cur);
        }

        Bson filter = new Document("x", 0)
                .append("y", new Document("$gt", 20))
                .append("y", new Document("$lt", 50));

        Bson coolFilter = and(eq("x", 0), gt("y", 50), lt("y", 90));

        MongoCursor cursor = anotherColl.find(coolFilter).iterator();

        try {
            while (cursor.hasNext()) {
                Document cur = (Document) cursor.next();
                printJson(cur);
            }

        } finally {
            cursor.close();
        }

        // insert 100 documents with two random integers
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                anotherColl.insertOne(new Document().append("i", i).append("j", j));
            }
        }

        Bson projection = fields(include("i","j"), excludeId());
        Bson sort = descending("j", "i");

        all = anotherColl.find()
                .projection(projection)
                .sort(sort)
                .skip(20)
                .limit(50)
                .into(new ArrayList<Document>());

        for (Document curr : all) {
            printJson(curr);
        }

        anotherColl.drop();

        for (int i = 0; i < 8; i++) {
            anotherColl.insertOne(new Document().append("_id", i).append("x", i).append("y", true));
        }

        anotherColl.replaceOne(eq("x", 5), new Document().append("x", 20).append("updated", true));
        anotherColl.updateOne(eq("x", 6), new Document("$set", new Document().append("x", 20).append("updated", true)));

        UpdateResult updateResult = anotherColl.updateOne(eq("x", 7), combine(set("x", 30), set("updated", true)));

        anotherColl.updateOne(eq("_id", 9), combine(set("x", 30), set("updated", true)), new UpdateOptions().upsert(true));

        anotherColl.updateMany(gte("x", 5), inc("x", 1));

        for (Document curr : anotherColl.find().into(new ArrayList<Document>())) {
            printJson(curr);
        }

        anotherColl.deleteMany(gt("_id", 4));
        anotherColl.deleteOne(eq("_id", 4));

        for (Document curr : anotherColl.find().into(new ArrayList<Document>())) {
//            printJson(curr);
        }
    }
}
