package com.howtographql.hackernews;

import com.coxautodev.graphql.tools.GraphQLRootResolver;
import graphql.GraphQLException;
import graphql.schema.DataFetchingEnvironment;

public class Mutation implements GraphQLRootResolver {
    private final LinkRepository linkRepository;
    private final UserRepository userRepository;

    public Mutation(LinkRepository linkRepository, UserRepository userRepository) {
        this.linkRepository = linkRepository;
        this.userRepository = userRepository;
    }

    public Link createLink(String url, String description, DataFetchingEnvironment env) {
        AuthContext authContext = env.getContext();
        Link link = new Link(url, description, authContext.getUser().getId());

        this.linkRepository.saveLink(link);
        return link;
    }

    public User createUser(String name, AuthData auth) {
        User newUser = new User(name, auth.getEmail(), auth.getPassword());

        return this.userRepository.saveUser(newUser);
    }

    public SigninPayload signinUser(AuthData authData) {
        User user = userRepository.getUserByEmail(authData.getEmail());

        if(user.getPassword().equals(authData.getPassword())) {
            return new SigninPayload(user.getId(), user);
        }

        throw new GraphQLException("Invalid Credentials");
    }
}
