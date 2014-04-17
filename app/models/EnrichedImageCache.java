package models;

import java.util.ArrayList;
import java.util.List;

import play.Logger.ALogger;

public class EnrichedImageCache {
	private static final ALogger LOG = play.Logger.of("application");

	private static EnrichedImageCache instance;

	public static EnrichedImageCache getInstance() {
		if (instance == null) {
			instance = new EnrichedImageCache(new ArrayList<EnrichedImage>(),
					Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY,
					Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
		}

		return instance;
	}

	public static void update(List<EnrichedImage> aList, float anNeLat,
			float anNeLng, float aSwLat, float aSwLng) {

		LOG.info("Updating the enriched image cache");

		instance = new EnrichedImageCache(aList, anNeLat, anNeLng, aSwLat,
				aSwLng);
	}

	List<EnrichedImage> cache;
	float neLat;
	float neLng;
	float swLat;
	float swLng;

	public EnrichedImageCache(List<EnrichedImage> aList, float anNeLat,
			float anNeLng, float aSwLat, float aSwLng) {
		cache = aList;
		neLat = anNeLat;
		neLng = anNeLng;
		swLat = aSwLat;
		swLng = aSwLng;
	}

	public boolean contains(float anNeLat, float anNeLng, float aSwLat,
			float aSwLng) {
		if (anNeLat <= neLat && aSwLat >= swLat && anNeLng <= neLng
				&& aSwLng >= swLng) {
			return true;
		} else {
			return false;
		}
	}

}
