package models;

import java.util.List;
import java.io.File;
import models.EnrichedImage;

public interface EnrichedImageRepository {
	public void loadFile(File aPath, String persistentPath);

	public List<EnrichedImage> getAllImages();

	public List<EnrichedImage> getImagesInBounds(Float neLat, Float neLng,
			Float swLat, Float swLng);

	public void deleteAll();	
}