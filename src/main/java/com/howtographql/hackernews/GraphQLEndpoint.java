package com.howtographql.hackernews;

import com.coxautodev.graphql.tools.SchemaParser;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import graphql.schema.GraphQLSchema;
import graphql.servlet.GraphQLContext;
import graphql.servlet.SimpleGraphQLServlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@WebServlet(urlPatterns = "/graphql")
public class GraphQLEndpoint extends SimpleGraphQLServlet {
    private static final LinkRepository linkRepository;
    private static final UserRepository userReporsitory;

    static {
        MongoDatabase mongodb = new MongoClient().getDatabase("hackernews");
        linkRepository = new LinkRepository(mongodb.getCollection("links"));
        userReporsitory = new UserRepository(mongodb.getCollection("users"));
    }

    public GraphQLEndpoint() {
        super(buildSchema());
    }

    private static GraphQLSchema buildSchema() {
        return SchemaParser.newParser()
                .file("schema.graphqls")
                .resolvers(
                        new Query(linkRepository, userReporsitory),
                        new Mutation(linkRepository, userReporsitory),
                        new SigninResolver(),
                        new LinkResolver(userReporsitory)
                        )
                .build()
                .makeExecutableSchema();
    }

    protected GraphQLContext createContext(Optional<HttpServletRequest> request, Optional<HttpServletResponse> response) {
        User user = request
                .map(req -> req.getHeader("Authorization"))
                .filter(id -> !id.isEmpty())
                .map(id -> id.replace("Bearer ",""))
                .map(userReporsitory::getUserById)
                .orElse(null);

        return new AuthContext(user, request, response);
    }
}
