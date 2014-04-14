package models;

import java.io.File;
import java.io.IOException;

import play.Logger.ALogger;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.lang.GeoLocation;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.GpsDirectory;

public class EnrichedImage {
    private File file;
	private Metadata metadata;
	public GeoLocation geoLocation;
    public String imgKey;
    
    private static final ALogger LOG = play.Logger.of(EnrichedImage.class);
    
    public EnrichedImage() {        
    }
    
    public EnrichedImage(File aFile) throws ImageProcessingException, IOException {
        file = aFile;
        imgKey = aFile.getName();
        refreshMetadata();
    }
    
    private void refreshMetadata() throws ImageProcessingException, IOException {
        metadata = ImageMetadataReader.readMetadata(file);
        GpsDirectory gpsDirectory = metadata.getDirectory(GpsDirectory.class);
    	geoLocation = gpsDirectory.getGeoLocation();
    }
}