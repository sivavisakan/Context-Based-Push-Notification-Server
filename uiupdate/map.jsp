<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link type="text/css" rel="stylesheet"
	href="/css/bootstrap/css/bootstrap.css" />
<link type="text/css" rel="stylesheet" href="./css/custom.css" />
<style type="text/css">
#map_canvas img {
	max-width: none;
}
</style>
<script src="./js/jquery.js"></script>
<script
	src="http://maps.googleapis.com/maps/api/js?key=AIzaSyB-k6sdgzpXqzTGYPqSm3qWdZFpbvwf3JY&sensor=true"></script>
</head>
<body onload="initialize()">
	<div class="navbar navbar-inverse navbar-fixed-top">
		<div class="navbar-inner">
			<div class="container">
				<a class="btn btn-navbar" data-toggle="collapse"
					data-target=".nav-collapse"> <span class="icon-bar"></span> <span
					class="icon-bar"></span> <span class="icon-bar"></span>
				</a> <a class="brand" href="#">Ericsson Disaster Management</a>
				<div class="nav-collapse collapse">
					<ul class="nav">
						<li class="active"><a href="#">Home</a></li>
						<li><a href="#about">About</a></li>
						<li><a href="#contact">Contact</a></li>
						<li><a href="map.jsp">Map</a></li>
					</ul>
				</div>
			</div>
		</div>
	</div>
	<div class="container">
		<br /> <br /> <br />
		<!-- <ol id="noStep" class="selected-step-2">
			<li id="step" class="step-1">Alert Message</li>
			<li id="step" class="step-2">Area</li>
			<li id="step" class="step-3">Information</li>
			<li id="step" class="step-4">Finish</li>
		</ol> -->
		<div class="row-fluid">

			<div class="span12">
				<div class="row-fluid">
					<div class="span4">
						<form onsubmit="showAddress(this.address.value); return false">
						
						<div class="well">
						<label for="message"><strong>Alert Message</strong>  </label>
						<textarea id="message" rows="2" cols="10" placeholder="Please enter the alert message "></textarea>
							<!-- TODO: Make the below a button tag instead  -->
						</div>
						
							<div class="well">
								<label for="address"> Address </label>
								<div class="input">
									<input class="span10" id="address" name="address"
										placeholder="Broadway ave, Manhattan" value="" type="text" />
								</div>
								<!-- TODO: Make the below an input tag instead  -->
								<button id="map-refresh" type="submit" class="btn btn-primary btn-info">
									<em class="icon-search icon-white"></em>&nbsp;Search
								</button>
							</div>
						</form>
						<form id="tools" action="./" method="post" onsubmit="return false">
							<div class="well">
								<label for="shape_type"> Area Shape </label> <select
									id="toolchoice" name="toolchoice"
									onchange="toolID=parseInt(this.options[this.selectedIndex].value)">
									<option selected="selected" value="1">Polygon</option>
									<option value="2">Circle</option>
								</select>
							<!-- </div> -->
						</form>
						<!-- <div class="well"> -->
							<!-- TODO: Make the below a button tag instead  -->
							<table>
							<tr><td>
							<input id="clear-map-btn" onclick="deleteOverlays();" type=button value="Clear Map"
								class="btn btn-primary btn-danger"><br/></td></tr>
						
						<form name="areaForm" id="areaForm" action="/proxy/alert" method="post">
							
								<tr>
									<!-- <td align="left"><button type="submit"
											class="btn btn-primary btn-small" name="backAlert">Back</button></td> -->
									<td align="right"><button type="submit"
											class="btn btn-primary" name="nextInfo">Disseminate Alert</button></td>
								</tr>
							</table>
						</form>
						</div>
						<!-- TODO: Remove test divs -->
						<div id="test"></div>
						<div id="test_1"></div>
					</div>
					<div class="span8">
						<div
							style="display: inline-block; position: relative; width: 100%">
							<div style="margin-top: 75%">
								<div id="map_canvas"
									style="position: absolute; top: 0; bottom: 0; left: 0; right: 0"></div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<script>
		var map;
		var geocoder; // = new google.maps.Geocoder();
		var markers = [];

		// Either circle or polygon
		var toolID = 1;

		// Circle variables
		var centerPoint;
		var radiusPoint;
		var circleOptions; // init here
		var circles = [];
		var currentCircle;

		// Polygon variables
		var polygonOptions; // init here
		var polyPoints = []; // Array of latLng objects defined in the google maps library.
		// It saves the vertices of a polygon that is being edited.    
		var polygons = []; // arrray of Polygon objects defined in the google maps ibrary
		var polygonPaths;
		var areaJSON;
		var curPolygon; // current // This variable holds a polygon that is being edited
		var timeout;
		var closePoly;

		// Setup the map.
		function initialize() {
			var mapOptions = {
				center : new google.maps.LatLng(37.4106, -122.0596),
				zoom : 8,
				draggable : true,
				draggingCursor : 'pointer',
				scaleControl : true,
				mapTypeControl : true,
				disableDoubleClickZoom : true,
				streetViewControl : false,

				mapTypeId : google.maps.MapTypeId.HYBRID
			// take out street view
			};
			map = new google.maps.Map(document.getElementById("map_canvas"),
					mapOptions); //keep

			// Initializethe geocoder.
			geocoder = new google.maps.Geocoder();

			// Initialize the circle options 
			circleOptions = {
				strokeColor : "#FF0000",
				strokeOpacity : 0.8,
				strokeWeight : 2,
				fillColor : "#FF0000",
				fillOpacity : 0.35,
				map : map
			};

			// Initialize the polygon options 
			polygonOptions = {
				strokeColor : "#FF0000",
				strokeOpacity : 0.8,
				strokeWeight : 2,
				fillColor : "#FF0000",
				fillOpacity : 0.35,
				map : map
			};

			// Initialize the event listeners
			google.maps.event.addListener(map, 'click', function(event) {
				if ((toolID == 2) && timeout) {
					clearTimeout(timeout);

					if (radiusPoint) {
						centerPoint = null;
						currentCircle = null;
						radiusPoint = null;
						addMarker(event);
					} else if (currentCircle) {
						addToCircle(event.latLng);
					} else {
						addMarker(event);
					}
				} else if ((toolID == 1) && (polyPoints.length >= 3)) {
					timeout = setTimeout(addMarker, 200, event);
				} else {
					addMarker(event);
				}
			}); // keep 
			google.maps.event.addListener(map, 'dblclick', function(event) {
				if (timeout) {
					clearTimeout(timeout);
					addMarker(event);
					polyPoints = [];
					curPolygon = null;
				}
			});

			google.maps.event.addListener(map, 'mousedown', function(event) {
				if ((toolID == 2) && centerPoint) {
					timeout = setTimeout(addToCircle, 200, event.latLng);
				}
			});

			restoreAreas();
		}

		// Add overlays at the cursor position when the map is clicked.
		function addMarker(event) {
			// Add a marker to the map position and push it to the array of markers.
			marker = new google.maps.Marker({
				position : event.latLng,
				map : map
			});
			markers.push(marker);

			// Add the marker to a shape
			if (toolID == 1) {
				// Polygon is selected
				addToPolygon(event.latLng);

			} else { // else if
				// Circle is selected
				currentCircle = new google.maps.Circle(circleOptions);
				circles.push(currentCircle);
				addToCircle(event.latLng);

				// Make sure to lock any previous polygon 
				polyPoints = [];
				curPolygon = null;
			}
		}

		// Adds a map position as the vertex of a polygon
		function addToPolygon(position) {
			// Add the position to the array of polygon points
			if (polyPoints.length > 0) {
				closingPoint = polyPoints.pop();
				polyPoints.push(position);
				polyPoints.push(closingPoint);
				// If the array has 3 or more vertices, draw a polygon
				if (polyPoints.length > 3)
					drawPolygon();
			}
			/* Add the first position in the array of polygon points. It is the first and the
			 last element of the array. */
			else {
				polyPoints.push(position);
				polyPoints.push(position);
			}
		}

		// Draws a polygon overlay on the map.
		function drawPolygon() {
			// Change the path of the existing current polygon
			if (curPolygon) {
				curPolygon.setPath(polyPoints);
			}
			// Create a new current polygon
			else {
				curPolygon = new google.maps.Polygon(polygonOptions);
				curPolygon.setPath(polyPoints);

				polygons.push(curPolygon);
			}
		}

		// Adds a map position as the center or the radius point of a circle
		function addToCircle(position) {
			if (centerPoint) {
				radiusPoint = position;
				drawCircle();
			} else {
				centerPoint = position;
			}
		}

		// Draws a circle overlay on the map.
		function drawCircle() {
			// Get the distance between the center and the radius point
			radius = distance(centerPoint.lat(), centerPoint.lng(), radiusPoint
					.lat(), radiusPoint.lng());

			currentCircle.setCenter(centerPoint);
			currentCircle.setRadius(radius);

			//circle = new google.maps.Circle(circleOptions);

			//circles.push(circle);

			// Reset the center and the radius
			//centerPoint = null;
			//radiusPoint = null;
		}

		// Calculates the distance between two coordinates.
		function distance(lat1, lon1, lat2, lon2) { // experiment with basic calculation
			var R = 6371000; // earth's radius in meters
			var dLat = (lat2 - lat1) * Math.PI / 180;
			var dLon = (lon2 - lon1) * Math.PI / 180;
			var a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
					+ Math.cos(lat1 * Math.PI / 180)
					* Math.cos(lat2 * Math.PI / 180) * Math.sin(dLon / 2)
					* Math.sin(dLon / 2);
			var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
			var d = R * c;
			return d;
		}

		// Deletes all overlays.
		function deleteOverlays() {
			// Delete all markers in the markers array by removing references to them.
			if (markers) {
				for (i in markers) {
					markers[i].setMap(null);
				}
				markers = [];
			}
			/* Delete all circles in the circles array by removing references to them,
			 and clear all the relevant circles vars. */
			if (circles) {
				for (i in circles) {
					circles[i].setMap(null);
				}
				circles = [];
				centerPoint = null;
			}
			// Delete all polygons in the polygons array by removing references to them.
			if (polygons) {
				for (i in polygons) {
					polygons[i].setMap(null);
				}
				polygons = [];
				polyPoints = [];
				curPolygon = null;
			}
		}

		// Centers the map to the address input.
		function showAddress(address) {
			geocoder
					.geocode(
							{
								'address' : address
							},
							function(results, status) {
								if (status == google.maps.GeocoderStatus.OK) {
									var pos = results[0].geometry.location;
									map.setCenter(pos);
									map.setZoom(12);
								} else {
									alert("Geocode was not successful for the following reason: "
											+ status);
								}
							});
		}

		// PERSIST THE AREA DATA

		$(function() {
			$('#areaForm').submit(
					function() {
						debugger
						window.localStorage.setItem("polygonAreas", JSON
								.stringify(serializePolygons()));
						window.localStorage.setItem("circleAreas", JSON
								.stringify(serializeCircles()));
						var pushMessage = $('#message').val();
						var json_sample = '{area: '+ window.localStorage.getItem("circleAreas")+',message:"'+pushMessage +'"}';
						console.log(json_sample);
						$.ajax
						    ({
						        type: "POST",
						        //the url where you want to sent the userName and password to
						        url: '/proxy/alert',
						        dataType:"json",
						        contentType: "application/json",
						        //json object to sent to the authentication url
						        data: json_sample,
						        success: function () {
						        //alert("Thanks!"); 
						        }
						    });
						  return false;
					});
			/* $.post("/proxy/alert", {json_string:JSON.stringify({area:window.localStorage.getItem("circleAreas")})},function(data){
				console.log(data);	
			}); */
			
			
			
		});

		function serializePolygons() {
			var serPolygonPaths = [];
			var onePolygonPath = [];

			if (polygons != []) {
				for ( var i = 0; i < polygons.length; i++) {
					polygonPath = polygons[i].getPath().getArray();
					onePolygonPath = [];

					for ( var j = 0; j < polygonPath.length; j++) {
						onePolygonPath.push(polygonPath[j].lat());
						onePolygonPath.push(polygonPath[j].lng());
					}

					serPolygonPaths.push(onePolygonPath);
				}
			}
			return serPolygonPaths;
		}

		function serializeCircles() {
			serCircles = [];

			debugger
			if (circles != []) {
				for ( var i = 0; i < circles.length; i++) {
					serCircles.push(circles[i].getCenter().lat());
					serCircles.push(circles[i].getCenter().lng());
					serCircles.push(circles[i].getRadius());
				}
			}
			return serCircles;
		}

		function restoreAreas() {
			var polygonAreasJSON = localStorage.getItem('polygonAreas');
			debugger;
			if (polygonAreasJSON != null) {
				polygonAreas = JSON.parse(polygonAreasJSON);
				restorePolygons(polygonAreas);
			}

			var circleAreasJSON = localStorage.getItem('circleAreas');
			if (circleAreasJSON != null) {
				circleAreas = JSON.parse(circleAreasJSON);
				restoreCircles(circleAreas);
			}
		}

		function restorePolygons(polygonPaths) {
			debugger
			for ( var i = 0; i < polygonPaths.length; i++) {
				onePolygonPathPoints = polygonPaths[i];
				onePolygonPath = [];

				for ( var j = 0; j < onePolygonPathPoints.length; j += 2) {
					onePolygonPath.push(new google.maps.LatLng(
							onePolygonPathPoints[j],
							onePolygonPathPoints[j + 1]))

					// Add a marker to the map position and push it to the array of markers.
					marker = new google.maps.Marker({
						position : new google.maps.LatLng(
								onePolygonPathPoints[j],
								onePolygonPathPoints[j + 1]),
						map : map
					});
					markers.push(marker);
				}

				polygon = new google.maps.Polygon(polygonOptions);
				polygon.setPath(onePolygonPath);
				polygons.push(polygon);
			}
		}

		function restoreCircles(circleAreas) {
			debugger
			for ( var i = 0; i < circleAreas.length; i += 3) {
				oneCircleArea = circleAreas[i];
				onePolygonPath = [];

				// Add a marker to the center position and push it to the array of markers.
				marker = new google.maps.Marker({
					position : new google.maps.LatLng(circleAreas[i], circleAreas[i + 1]),
					map : map
				});
				markers.push(marker);

				circle = new google.maps.Circle(circleOptions);
				circle.setCenter(new google.maps.LatLng(circleAreas[i], circleAreas[i + 1]));
				circle.setRadius(circleAreas[i + 2]);
				circles.push(circle);
			}
		}

		// UTILITY FUNCTIONS
		// Get a JS object or a DOM element object
		function getObject(e) {
			if (typeof (e) == 'object')
				return (e);
			if (document.getElementById)
				return (document.getElementById(e));
			return (eval(e))
		}
	</script>
</body>
</html>