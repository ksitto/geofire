package controllers;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.naming.ConfigurationException;

import com.google.common.io.Resources;

import models.EnrichedImageRepository;
import models.LocalMemoryEnrichedImageRepository;
import models.RedisEnrichedImageRepository;
import models.S3File;
import play.Play;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import plugins.S3Plugin;

public class Upload extends Controller {

	EnrichedImageRepository repository;

	public Upload() {

		try {
			repository = new RedisEnrichedImageRepository();
		} catch (ConfigurationException e) {
			repository = new LocalMemoryEnrichedImageRepository();
		}

	}

	public Result index() {
		return ok(views.html.upload.render());
	}

	public Result upload() {
		Http.MultipartFormData body = request().body().asMultipartFormData();
		List<Http.MultipartFormData.FilePart> uploadFileParts = body.getFiles();

		if (uploadFileParts != null) {
			for (Http.MultipartFormData.FilePart uploadFilePart : uploadFileParts) {
				URL fileLocation;
				try {
					fileLocation = uploadFile(uploadFilePart.getFile(),
							uploadFilePart.getFilename());
				} catch (MalformedURLException e1) {
					return badRequest("File upload error");
				}

				repository.loadFile(uploadFilePart.getFile(),
						fileLocation.toString());
			}
			return redirect(routes.Upload.index());
		} else {
			return badRequest("File upload error");
		}

	}

	public static Result uploadDirectory() {

		return null;
	}

	private URL uploadFile(File aFile, String fileName)
			throws MalformedURLException {
		URL aUrl;

		if (null != S3Plugin.amazonS3) {
			S3File s3File = new S3File();
			s3File.name = fileName;
			s3File.file = aFile;
			s3File.save();

			aUrl = s3File.getUrl();
		} else {
			aFile.renameTo(new File(Play.application().getFile("public/data"),
					fileName));

			aUrl = new URL("http", "localhost", 9000, "assets/data/" + fileName);
		}

		return aUrl;
	}
}