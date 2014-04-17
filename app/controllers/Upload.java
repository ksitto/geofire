package controllers;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import models.EnrichedImageRepository;
import models.RedisEnrichedImageRepository;
import models.S3File;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

public class Upload extends Controller {

	EnrichedImageRepository repository = new RedisEnrichedImageRepository();

	public Result index() {
		List<S3File> uploads = new ArrayList<S3File>();
		// new Model.Finder(UUID.class, S3File.class).all();

		return ok(views.html.upload.render());
	}

	public Result upload() {
		Http.MultipartFormData body = request().body().asMultipartFormData();
		List<Http.MultipartFormData.FilePart> uploadFileParts = body.getFiles();

		if (uploadFileParts != null) {
			for (Http.MultipartFormData.FilePart uploadFilePart : uploadFileParts) {

				S3File s3File = new S3File();
				s3File.name = uploadFilePart.getFilename();
				s3File.file = uploadFilePart.getFile();
				s3File.save();

				try {
					repository.loadFile(uploadFilePart.getFile(), s3File
							.getUrl().toString());
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return redirect(routes.Upload.index());
		} else {
			return badRequest("File upload error");
		}

	}

	public static Result uploadDirectory() {

		return null;
	}
}