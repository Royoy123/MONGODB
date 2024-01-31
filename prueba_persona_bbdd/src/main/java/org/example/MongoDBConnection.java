package org.example;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class MongoDBConnection {
    private static final String DATABASE_NAME = "MONGODB";
    private static final String COLLECTION_NAME = "usearios";

    private static MongoDBConnection instance;
    private final MongoClient mongoClient;
    private final MongoDatabase database;
    private final MongoCollection<Document> userCollection;

    private MongoDBConnection() {
        // Configura la conexión a MongoDB
        this.mongoClient = MongoClients.create("mongodb://localhost:27017");
        this.database = mongoClient.getDatabase(DATABASE_NAME);
        this.userCollection = database.getCollection(COLLECTION_NAME);
    }

    public static synchronized MongoDBConnection getInstance() {
        if (instance == null) {
            instance = new MongoDBConnection();
        }
        return instance;
    }

    public MongoCollection<Document> getUserCollection() {
        return userCollection;
    }

    public void closeConnection() {
        // Cierra la conexión al finalizar
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}
