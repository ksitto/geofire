package models;

import java.io.File;
import java.io.IOException;

import com.drew.metadata.Metadata;
import com.drew.lang.GeoLocation;
import com.drew.metadata.exif.GpsDirectory;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;

import play.Logger.ALogger;

public class EnrichedImage {
    private File file;
	private Metadata metadata;
	public GeoLocation geoLocation;
    
    private static final ALogger LOG = play.Logger.of(EnrichedImage.class);
    
    public EnrichedImage() {        
    }
    
    public EnrichedImage(File aFile) throws ImageProcessingException, IOException {
        file = aFile;
        refreshMetadata();
    }
    
    private void refreshMetadata() throws ImageProcessingException, IOException {
        metadata = ImageMetadataReader.readMetadata(file);
        GpsDirectory gpsDirectory = metadata.getDirectory(GpsDirectory.class);
    	geoLocation = gpsDirectory.getGeoLocation();
    }
}