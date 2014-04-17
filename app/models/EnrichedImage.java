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

	public double latitude = 0;
	public double longitude = 0;
	public String imgKey;
	public String path;

	private static final ALogger LOG = play.Logger.of(EnrichedImage.class);

	public EnrichedImage() {
	}

	public EnrichedImage(File aFile) throws ImageProcessingException,
			IOException {
		imgKey = aFile.getName();
		refreshMetadata(aFile);
	}

	private void refreshMetadata(File aFile) throws ImageProcessingException,
			IOException {
		Metadata metadata = ImageMetadataReader.readMetadata(aFile);
		GpsDirectory gpsDirectory = metadata.getDirectory(GpsDirectory.class);
		if (gpsDirectory != null) {
			GeoLocation geoLocation = gpsDirectory.getGeoLocation();
			if (geoLocation != null) {
				this.latitude = geoLocation.getLatitude();
				this.longitude = geoLocation.getLongitude();
			}
		}
	}
}