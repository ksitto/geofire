package models;

import java.util.List;
import java.util.ArrayList;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import play.Logger.ALogger;

import models.EnrichedImage;
import models.EnrichedImageRepository;

import com.drew.imaging.ImageProcessingException;

public class EnrichedImageRepositoryImpl implements EnrichedImageRepository {
    
    private List<EnrichedImage> images;            
    private static final ALogger LOG = play.Logger.of(EnrichedImage.class);
    
    public EnrichedImageRepositoryImpl () {
        images = new ArrayList<EnrichedImage>();
    }
    
    
    @Override
    public void loadDirectory(File aPath) {
        for (File imgFile : aPath.listFiles(new ImageFilter())) {
            try {
    		    EnrichedImage enrichedImage = new EnrichedImage(imgFile);
                images.add(enrichedImage);    		
            } catch (ImageProcessingException e) {
                LOG.error("", e);                
            } catch (IOException e) {
                LOG.error("", e);                                
            }             
		}
    }
    
    private static final class ImageFilter implements FileFilter {
    	private final String[] acceptedExtensions = new String[] { ".jpg",
				".jpeg" };

		@Override
		public boolean accept(File pathname) {
			if (pathname.isDirectory()) {
				return false;
			} else {
				for (String acceptedExtension : acceptedExtensions) {
					if (pathname.getName().toLowerCase()
							.endsWith(acceptedExtension)) {
						return true;
					}
				}
			}
			return false;
		}
	}
    
    
    @Override
    public List<EnrichedImage> getAllImages() {
        return images;
    }
    
    
}