package models;

import java.util.List;
import java.io.File;
import models.EnrichedImage;

public interface EnrichedImageRepository {
    public void loadDirectory(File aPath);
    public List<EnrichedImage> getAllImages();
    public List<EnrichedImage> getImagesInBounds(Float neLat, Float neLng, Float swLat, Float swLng);    
}