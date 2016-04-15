package com.mongodb;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.bson.Document;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

import static spark.Spark.*;

/**
 * Created by rod on 02/04/16.
 */
public class HelloWorldMongoDBSparkFreemarkerStyle {

    public static void main(String[] args) throws IOException {
        final Configuration cfg = new Configuration();

        cfg.setClassForTemplateLoading(HelloWorldMongoDBSparkFreemarkerStyle.class, "/freemarker");

//        cfg.setDirectoryForTemplateLoading(new File("/home/rod/mongo-projects/M101J/src/main/java/freemarker"));

        MongoClient client = new MongoClient();
        MongoDatabase db = client.getDatabase("course");

        final MongoCollection<Document> coll = db.getCollection("hello");

        coll.drop();
        coll.insertOne(new Document().append("name", "MongoDB"));

        get("/", new Route() {
            public Object handle(Request request, Response response) throws Exception {
                StringWriter writer = new StringWriter();

                try {
                    Template template = cfg.getTemplate("hello.html");
                    Document doc = coll.find().first();
                    template.process(doc, writer);

                } catch (Exception e) {
                    halt(500);
                    e.printStackTrace();
                }

                return writer;
            }
        });
    }
}
