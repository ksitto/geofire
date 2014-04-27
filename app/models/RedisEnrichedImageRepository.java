package models;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.ConfigurationException;

import play.Logger.ALogger;
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

	public RedisEnrichedImageRepository() throws ConfigurationException {
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
				if (Play.application().configuration().keys()
						.contains("redis.hostname")
						&& Play.application().configuration().keys()
								.contains("redis.port")
						&& Play.application().configuration().keys()
								.contains("redis.password")) {
					hostname = Play.application().configuration()
							.getString("redis.hostname");
					port = Integer.parseInt(Play.application().configuration()
							.getString("redis.port"));
					password = Play.application().configuration()
							.getString("redis.password");
				} else {
					throw new ConfigurationException();
				}
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

		RedisEnrichedImageCache cache = RedisEnrichedImageCache
				.getInstance(pool);

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

	private static class RedisEnrichedImageCache {
		private static final ALogger LOG = play.Logger.of("application");
		private static RedisEnrichedImageCache instance;
		public List<EnrichedImage> cache;
		protected long lastUpdate = 0L;
		private static final long AGE_OFF = 10000;

		private RedisEnrichedImageCache() {
		}

		public static synchronized RedisEnrichedImageCache getInstance(
				JedisPool aPool) {

			if (instance == null) {
				instance = new RedisEnrichedImageCache();
			}

			if (instance.isExpired()) {
				instance.update(aPool);
			}

			return instance;
		}

		protected synchronized void update(JedisPool aPool) {

			LOG.info("updating the cache");

			cache = new ArrayList<EnrichedImage>();

			Gson gson = new Gson();
			Jedis jedis = aPool.getResource();
			for (String key : jedis.keys("*")) {
				EnrichedImage img = gson.fromJson(key, EnrichedImage.class);
				double imgLat = img.latitude;
				double imgLng = img.longitude;
				cache.add(img);
			}
			aPool.returnResource(jedis);

			lastUpdate = System.currentTimeMillis();
		}

		protected boolean isExpired() {
			return (System.currentTimeMillis() >= (lastUpdate + AGE_OFF));
		}
	}
}
