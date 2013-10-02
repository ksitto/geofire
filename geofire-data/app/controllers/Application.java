package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import play.*;
import play.api.Logger;
import play.Logger.ALogger;
import play.libs.Json;

import models.EnrichedImage;
import models.EnrichedImageRepository;
import models.EnrichedImageRepositoryImpl;

import java.io.File;
import java.util.List;

public class Application extends Controller {
    
    private EnrichedImageRepository repo;
    
    public Application() {
        repo = new EnrichedImageRepositoryImpl();
    }
    
    public Result index() {                 
        response().setHeader("Access-Control-Allow-Origin", "*");
        
        ALogger log = play.Logger.of(Application.class);
        
        File rootDirectory = Play.application().getFile("public/data/ec1m_landmark_images-sample");        
                
        log.warn("doing work...");
        repo.loadDirectory(rootDirectory);
        
        List<EnrichedImage> images = repo.getAllImages();         
        return ok(Json.toJson(images));
    }
    
    public static Result checkPreFlight() {
        response().setHeader("Access-Control-Allow-Origin", "*");       // Need to add the correct domain in here!!
        response().setHeader("Access-Control-Allow-Methods", "*");   // Only allow POST
        response().setHeader("Access-Control-Max-Age", "300");          // Cache response for 5 minutes
        response().setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");         // Ensure this header is also allowed!  
        return ok();
    }
    
}
