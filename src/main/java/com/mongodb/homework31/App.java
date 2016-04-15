package com.mongodb.homework31;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.*;
import static com.mongodb.client.model.Projections.elemMatch;
import static com.mongodb.client.model.Sorts.*;
import static com.mongodb.client.model.Updates.*;
import static com.mongodb.util.Helper.printJson;

/**
 * Created by rod on 12/04/16.
 */
public class App {


    public static void main(String[] args) {

        MongoClient client = new MongoClient("localhost", 27017);
        MongoDatabase db = client.getDatabase("school");
        MongoCollection<Document> collection = db.getCollection("students", Document.class);

        Bson filter = eq("scores.type", "homework");
        Bson projection = fields(include("scores.score", "scores.type"), elemMatch("type", filter));
        Bson sort = ascending("scores.score");

        for (Document curr : collection.find().into(new ArrayList<Document>())) {

            Double lowestHomeworkScore = Double.MAX_VALUE;
            Document lowestScoreDocument = null;

            for (Document score : (List<Document>) curr.get("scores")) {
                if (score.get("type").equals("homework") && score.getDouble("score").compareTo(lowestHomeworkScore) < 0) {
                    lowestHomeworkScore = score.getDouble("score");
                    lowestScoreDocument = score;
                }
            }

            List<Document> scores = (List<Document>) curr.get("scores");
            scores.remove(lowestScoreDocument);
            collection.updateOne(eq("_id", curr.get("_id")), new Document("$set", new Document("scores", scores)));

            printJson(lowestScoreDocument);
        }

        client.close();
    }
}
