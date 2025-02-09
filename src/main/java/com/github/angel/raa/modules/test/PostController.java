package com.github.angel.raa.modules.test;

import com.github.angel.raa.modules.annotations.Get;
import com.github.angel.raa.modules.annotations.Post;
import com.github.angel.raa.modules.core.Request;
import com.github.angel.raa.modules.core.Response;
import com.github.angel.raa.modules.core.router.Controller;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostController extends Controller {
    static List<PostEntity> posts = new ArrayList<>();

    public PostController() {
        super("/post");
        posts.add(new PostEntity(1, "title1", "body1"));
        posts.add(new PostEntity(2, "title2", "body2"));
        posts.add(new PostEntity(3, "title3", "body3"));
        posts.add(new PostEntity(4, "title4", "body4"));
        posts.add(new PostEntity(5, "title5", "body5"));
    }

    @Get("/home")
    public  Response getHome(Request request){
        Map<String, Object> model = new HashMap<>();
        model.put("title", "Bienvenido a Alba");
        model.put("message", "¡Hola desde Thymeleaf!");
        return new Response().addTemplate("index.html", model);
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


    //@Get("/:id")
    public static Response getPost(Request request) {
        if (posts.isEmpty()) {
            return new Response(401, "No posts found");
        }
        int id = Integer.parseInt(request.getParams().get("id"));
        JSONObject resp = new JSONObject();

         posts.stream().filter(p -> p.getPostId() == id).findFirst().ifPresent(p -> {
            resp.put("post", p);
        });

        return new Response(200,new JSONObject().put("Message", "Data").put("posts", resp));
    }

    @Post("/create")
    public Response create(Request request){
        var body = request.getBodyAs(PostEntity.class);
        System.out.println(body.getTitle());
        JSONObject  resp = request.getBodyAs(JSONObject.class);
        return Response.Created(resp);
    }

    @Get("/list")
    public Response listado(Request request){
        Response response = new Response();
        Map<String, Object> model = new HashMap<>();
        model.put("title", "Bienvenido a Alba");
        model.put("message", "¡Hola desde Thymeleaf!");
        model.put("posts", posts);
        return response.addTemplate("list", model);
    }

}
