package models;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import play.Play;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;
import argo.jdom.JdomParser;
import argo.jdom.JsonNode;
import argo.jdom.JsonRootNode;
import argo.saj.InvalidSyntaxException;

import com.drew.imaging.ImageProcessingException;
import com.google.gson.Gson;

public class RedisEnrichedImageRepository implements EnrichedImageRepository {

	JedisPool pool;
	EnrichedImageCache cache;

	public RedisEnrichedImageRepository() {
		try {
			String vcap_services = System.getenv("VCAP_SERVICES");

			String hostname;
			int port;
			String password;

			if (vcap_services != null && vcap_services.length() > 0) {
				// parsing rediscloud credentials
				JsonRootNode root = new JdomParser().parse(vcap_services);
				JsonNode rediscloudNode = root.getNode("rediscloud");
				JsonNode credentials = rediscloudNode.getNode(0).getNode(
						"credentials");

				hostname = credentials.getStringValue("hostname");
				port = Integer.parseInt(credentials.getStringValue("port"));
				password = credentials.getStringValue("password");

			} else {
				hostname = Play.application().configuration()
						.getString("redis.hostname");
				port = Integer.parseInt(Play.application().configuration()
						.getString("redis.port"));
				password = Play.application().configuration()
						.getString("redis.password");
			}

			pool = new JedisPool(new JedisPoolConfig(), hostname, port,
					Protocol.DEFAULT_TIMEOUT, password);

		} catch (InvalidSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void loadFile(File aPath, String persistentPath) {
		try {
			EnrichedImage enrichedImage = new EnrichedImage(aPath);
			enrichedImage.path = persistentPath;
			Gson gson = new Gson();
			String serializedImage = gson.toJson(enrichedImage);

			Jedis jedis = pool.getResource();
			jedis.set(serializedImage, "");

			pool.returnResource(jedis);

		} catch (ImageProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public List<EnrichedImage> getAllImages() {
		return getImagesInBounds(Float.POSITIVE_INFINITY,
				Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY,
				Float.NEGATIVE_INFINITY);
	}

	@Override
	public List<EnrichedImage> getImagesInBounds(Float neLat, Float neLng,
			Float swLat, Float swLng) {
		EnrichedImageCache cache = EnrichedImageCache.getInstance();
		if (!cache.contains(neLat, neLng, swLat, swLng)) {

			List<EnrichedImage> images = new ArrayList<EnrichedImage>();

			Gson gson = new Gson();
			Jedis jedis = pool.getResource();
			for (String key : jedis.keys("*")) {
				EnrichedImage img = gson.fromJson(key, EnrichedImage.class);
				double imgLat = img.latitude;
				double imgLng = img.longitude;
				images.add(img);
			}
			pool.returnResource(jedis);

			EnrichedImageCache.update(images, Float.POSITIVE_INFINITY,
					Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY,
					Float.NEGATIVE_INFINITY);
		}

		cache = EnrichedImageCache.getInstance();
		List<EnrichedImage> images = new ArrayList<EnrichedImage>();
		for (EnrichedImage img : cache.cache) {
			if (img.latitude <= neLat && img.latitude >= swLat
					&& img.longitude <= neLng && img.longitude >= swLng) {
				images.add(img);
			}
		}

		return images;
	}

	@Override
	public void deleteAll() {
		Jedis jedis = pool.getResource();
		for (String key : jedis.keys("*")) {
			jedis.del(key);
		}
		pool.returnResource(jedis);
	}
}
