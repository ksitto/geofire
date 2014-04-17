package controllers;

import java.util.List;

import models.EnrichedImage;
import models.EnrichedImageRepository;
import models.RedisEnrichedImageRepository;
import play.Logger.ALogger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

public class Application extends Controller {

	private EnrichedImageRepository repo;
	ALogger log = play.Logger.of("application");

	public Application() {
		repo = new RedisEnrichedImageRepository();
	}

	public Result index() {
		response().setHeader("Access-Control-Allow-Origin", "*");
		return ok(views.html.testmap.render("GeoFire prototype v0.000001"));
	}

	public Result getAll() {
		response().setHeader("Access-Control-Allow-Origin", "*");
		List<EnrichedImage> images = repo.getAllImages();
		return ok(Json.toJson(images));
	}

	public Result getLocations(Float neLat, Float neLng, Float swLat,
			Float swLng) {
		response().setHeader("Access-Control-Allow-Origin", "*");
		List<EnrichedImage> images = repo.getImagesInBounds(neLat, neLng,
				swLat, swLng);
		return ok(Json.toJson(images));
	}

	public static Result checkPreFlight() {
		response().setHeader("Access-Control-Allow-Origin", "*"); // Need to add
																	// the
																	// correct
																	// domain in
																	// here!!
		response().setHeader("Access-Control-Allow-Methods", "*"); // Only allow
																	// POST
		response().setHeader("Access-Control-Max-Age", "300"); // Cache response
																// for 5 minutes
		response().setHeader("Access-Control-Allow-Headers",
				"Origin, X-Requested-With, Content-Type, Accept"); // Ensure
																	// this
																	// header is
																	// also
																	// allowed!
		return ok();
	}

	public Result deleteAll() {
		repo.deleteAll();
		return ok();
	}
}
