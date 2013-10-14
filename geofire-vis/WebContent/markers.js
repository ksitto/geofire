$(document).ready(function() {
  $("#map").css({
		height: 500,
		width: 600
	});
	var myLatLng = new google.maps.LatLng(17.74033553, 83.25067267);
  MYMAP.init('#map', myLatLng, 11);
  
  $("#showmarkers").click(function(e){
		MYMAP.getLocations();
  });
});

var MYMAP = {
  map: null,
	bounds: null
}

MYMAP.init = function(selector, latLng, zoom) {
  var myOptions = {
    zoom:zoom,
    center: latLng,
    mapTypeId: google.maps.MapTypeId.ROADMAP
  }
  this.map = new google.maps.Map($(selector)[0], myOptions);
	this.bounds = new google.maps.LatLngBounds();
}

MYMAP.getLocations = function() {
      	$.getJSON("http://localhost:9000/", function(data) {
        	console.log("success");
            $.each(data["geolocation"], function(i, entry) {
            	alert(entry.latitude, entry.longitude);
            	PlotMarker(entry.latitude, entry.longitutde);
            });           
        })
        	.done(function() {
            	console.log("second success");
            })
            .fail(function() {
            	console.log("error");
            })
            .always(function() {
            	console.log("complete");
            })
        ;        
      }
      
function PlotMarker(lat, lon) {    
  var marker =
      new google.maps.Marker({
        position: new google.maps.LatLng(lat, lon),
        map: MYMAP.map,                
        draggable: false,
        animation: google.maps.Animation.DROP,                  
      });
}            