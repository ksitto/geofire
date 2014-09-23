package models;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import play.Logger.ALogger;
import play.Play;

import com.drew.imaging.ImageProcessingException;

public class LocalMemoryEnrichedImageRepository implements
		EnrichedImageRepository {

	private List<EnrichedImage> images;
	private static final ALogger LOG = play.Logger.of(EnrichedImage.class);

	public LocalMemoryEnrichedImageRepository() {
		images = new ArrayList<EnrichedImage>();
		initializeRepo();
	}

	@Override
	public List<EnrichedImage> getAllImages() {
		return images;
	}

	@Override
	public List<EnrichedImage> getImagesInBounds(Float neLat, Float neLng,
			Float swLat, Float swLng) {
		List<EnrichedImage> myImages = new ArrayList<EnrichedImage>();
		for (EnrichedImage img : images) {
			double imgLat = img.latitude;
			double imgLng = img.longitude;
			if (imgLat <= neLat && imgLat >= swLat && imgLng <= neLng
					&& imgLng >= swLng) {
				myImages.add(img);
			}
		}
		return myImages;
	}

	@Override
	public void loadFile(File aPath, String persistentPath) {
		try {
			EnrichedImage enrichedImage = new EnrichedImage(aPath);
			enrichedImage.path = persistentPath;
			images.add(enrichedImage);
		} catch (ImageProcessingException e) {
			LOG.error("", e);
		} catch (IOException e) {
			LOG.error("", e);
		}

	}

	@Override
	public void deleteAll() {
		images.clear();
	}

	private void initializeRepo() {
		File rootDirectory = Play.application().getFile("public/images");

		for (File imgFile : rootDirectory.listFiles()) {
			loadFile(imgFile, new String("assets/data/" + imgFile.getName()));
		}
	}
}