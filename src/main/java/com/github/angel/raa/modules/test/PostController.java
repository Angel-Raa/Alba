package com.github.angel.raa.modules.test;

import com.github.angel.raa.modules.annotations.Get;
import com.github.angel.raa.modules.core.Request;
import com.github.angel.raa.modules.core.Response;
import com.github.angel.raa.modules.core.router.Controller;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PostController extends Controller {


    static List<Post> posts = new ArrayList<>();

    public PostController() {
        super("/post");
        posts.add(new Post(1, "title1", "body1"));
        posts.add(new Post(2, "title2", "body2"));
        posts.add(new Post(3, "title3", "body3"));
        posts.add(new Post(4, "title4", "body4"));
        posts.add(new Post(5, "title5", "body5"));
        posts.add(new Post(6, "title6", "body6"));
        posts.add(new Post(7, "title7", "body7"));
        posts.add(new Post(8, "title8", "body8"));
        posts.add(new Post(9, "title9", "body9"));
        posts.add(new Post(10, "title10", "body10"));
    }


    @Get("/posts")
    public static Response getPosts(Request request) {
        if (posts.isEmpty()) {
            return new Response(401, "No posts found");
        }
        request.getHeaders().forEach((key, value) -> {
            System.out.println(key + ": " + value);
        });
        JSONObject resp = new JSONObject();
        resp.put("posts", posts);

        return new Response(200, resp);
    }


    @Get("/:id")
    public static Response getPost(Request request) {
        if (posts.isEmpty()) {
            return new Response(401, "No posts found");
        }
        int id = Integer.parseInt(request.getParams().get("id"));
        JSONObject resp = new JSONObject();

         posts.stream().filter(p -> p.getPostId() ==id).findFirst().ifPresent(p -> {
            resp.put("post", p);
        });

        return new Response(200,new JSONObject().put("Message", "Data").put("posts", resp));
    }
}
