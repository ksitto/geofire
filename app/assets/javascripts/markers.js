$(document).ready(function() {
	$("#map").css({
		height : 500,
		width : 600
	});
	var myLatLng = new google.maps.LatLng(39.1981, -76.80224);
	MYMAP.init('#map', myLatLng, 15);

	$("#showmarkers").click(function(e) {
		MYMAP.getLocations();
	});
});

var MYMAP = {
	map : null,
	bounds : null
}

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
	$.getJSON(
			"http://localhost:9000/application/window",
			{
				neLat : neLat,
				neLng : neLng,
				swLat : swLat,
				swLng : swLng
			},
			function(data) {
				document.getElementById("gallery").innerHTML = "";
				var markers = [];
				$.each(data, function(i, entry) {
					console.log(entry.imgKey);

					var marker = new google.maps.Marker({
						position : new google.maps.LatLng(
								entry.geoLocation.latitude,
								entry.geoLocation.longitude),
						draggable : false,
						title : entry.imgKey
					});

					markers.push(marker);

					var img = document.createElement("img");
					img.setAttribute("src", "/assets/data/" + entry.imgKey);
					img.setAttribute("height", "76");
					img.setAttribute("width", "134");
					document.getElementById("gallery").appendChild(img);
				});
				var markerCluster = new MarkerClusterer(MYMAP.map, markers);
				console.log("loaded <" + data.length + "> objects");
			}).done(function() {
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
