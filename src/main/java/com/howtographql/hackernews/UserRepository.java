package com.howtographql.hackernews;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.types.ObjectId;

import static com.mongodb.client.model.Filters.eq;

public class UserRepository {
    private final MongoCollection<Document> users;

    public UserRepository(MongoCollection<Document> users) {
        this.users = users;
    }

    private User user(Document doc) {
        if(doc == null) {
            return null;
        }

        return new User(
          doc.get("_id").toString(),
          doc.getString("name"),
          doc.getString("email"),
          doc.getString("password")
        );
    }

    public User getUserById(String id) {
        Document doc = users.find(eq("_id", new ObjectId(id))).first();

        return user(doc);
    }

    public User getUserByEmail(String email) {
        Document doc = users.find(eq("email", email)).first();

        return user(doc);
    }

    public User saveUser(User user) {
        Document doc = new Document();
        doc.append("name", user.getName());
        doc.append("email", user.getEmail());
        doc.append("password", user.getPassword());
        users.insertOne(doc);
        return new User(
                doc.get("_id").toString(),
                user.getName(),
                user.getEmail(),
                user.getPassword());
    }
}
