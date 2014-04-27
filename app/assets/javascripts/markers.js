$(document).ready(function() {
	$("#map").css({
		height : 500,
		width : 600
	});
	var myLatLng = new google.maps.LatLng(39.1981, -76.80224);
	MYMAP.init('#map', myLatLng, 1);

	$("#showmarkers").click(function(e) {
		MYMAP.getLocations();
	});
});

var MYMAP = {
	map : null,
	bounds : null
}

var MAX_IMAGES = 10;

MYMAP.init = function(selector, latLng, zoom) {
	var myOptions = {
		zoom : zoom,
		center : latLng,
		mapTypeId : google.maps.MapTypeId.HYBRID
	}
	this.map = new google.maps.Map($(selector)[0], myOptions);
	this.bounds = new google.maps.LatLngBounds();

	google.maps.event.addListener(this.map, 'idle', function() {
		currentBounds = MYMAP.map.getBounds();

		MYMAP.getLocations(currentBounds.getNorthEast().lat(), currentBounds
				.getNorthEast().lng(), currentBounds.getSouthWest().lat(),
				currentBounds.getSouthWest().lng());
	})

}

MYMAP.getLocations = function(neLat, neLng, swLat, swLng) {
	$
			.getJSON(
					application_root + "/window",
					{
						neLat : neLat,
						neLng : neLng,
						swLat : swLat,
						swLng : swLng
					},
					function(data) {
						image_count = 0;
						document.getElementById("gallery").innerHTML = "";
						var markers = [];
						$.each(data, function(i, entry) {
							var marker = new google.maps.Marker({
								position : new google.maps.LatLng(
										entry.latitude, entry.longitude),
								draggable : false,
								title : entry.imgKey
							});

							markers.push(marker);

							image_count++;
							if (image_count <= MAX_IMAGES) {
								var img = document.createElement("img");
								img.setAttribute("src", entry.path);
								img.setAttribute("height", "76");
								img.setAttribute("width", "134");
								document.getElementById("gallery").appendChild(
										img);
							}

						});
						var markerCluster = new MarkerClusterer(MYMAP.map,
								markers);
						console.log("loaded <" + data.length + "> objects");
					})
			.done(
					function() {
						document.getElementById("image_count").innerText = "Displaying "
								+ MAX_IMAGES
								+ " of "
								+ image_count
								+ " total images";
					}).fail(function() {
				console.log("error");
			});
}

function PlotMarker(lat, lon) {

	var marker = new google.maps.Marker({
		position : new google.maps.LatLng(lat, lon),
		map : MYMAP.map,
		draggable : false
	});

}
