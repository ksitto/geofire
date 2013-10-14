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
        ALogger log = play.Logger.of(Application.class);        
        initializeRepo();
    }
    
    private void initializeRepo() {
        repo = new EnrichedImageRepositoryImpl();
        File rootDirectory = Play.application().getFile("public/data/ec1m_landmark_images-sample");                                
        repo.loadDirectory(rootDirectory);        
    }
    
    public Result index() {                 
        response().setHeader("Access-Control-Allow-Origin", "*");                                                
        return ok("READY");
    }
    
    public Result getAll() {
        response().setHeader("Access-Control-Allow-Origin", "*");                                                
        List<EnrichedImage> images = repo.getAllImages();         
        return ok(Json.toJson(images));
    }
    
    public Result getLocations(Float neLat, Float neLng, Float swLat, Float swLng) {
        response().setHeader("Access-Control-Allow-Origin", "*");        
        List<EnrichedImage> images = repo.getImagesInBounds(neLat, neLng, swLat, swLng);         
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
