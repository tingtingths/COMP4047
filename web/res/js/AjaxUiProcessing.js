/**
 * Created by Ting on 12/10/2015.
 */

apiURL = window.location.protocol + "//" + window.location.host + "/spider/search"
var firstProcess = true;
var result;
var ptr = 0;
var appendStep = 5;

function search(ele) {
	// debug calulate processing time
	var start_time;
	var end_time;

	var doRequest = false;

	if (ele == document.getElementById("textField")) {
		if (event.keyCode == 13) {
			doRequest = true;
		}
	} else {
		doRequest = true;
	}
	if (doRequest) {
		console.log("init ajax request...");
		// change icon
		document.getElementById("goBtn").src = "./res/img/loader.gif";

		var searchPattern = document.getElementById("textField").value;
		console.log("value : " + searchPattern);
		//console.log(window.location.protocol);
		//console.log(window.location.host);

		// Http request
		var req = new XMLHttpRequest();

		req.onreadystatechange = function() {
			if (req.readyState == 4 && req.status == 200) { //XMLHttpRequest 'DONE' and 'SUCCESS'
				end_time = Date.now();
				document.getElementById("timeTaken").innerHTML = ((end_time - start_time)/1000) + " seconds";
				console.log("Time taken : " + ((end_time - start_time)/1000) + " seconds.");

				var json = req.responseText;
				// arrange the result
				startUiProcess(json);

			}
		}

		req.open("GET", apiURL + "?q=" + searchPattern, true);
		req.send();
		start_time = Date.now();
		
		// UI debug
		//startUiProcess();
	}
}

function startUiProcess(json) {
	// reset the icon
	document.getElementById("goBtn").src = "./res/img/goBtn.png";
	console.log("Processing json...");
	//console.log("json: " + json);
	var results = JSON.parse(json);
	var sortList = [];

	//console.log(jsonArr);
	for (var i in results) {
		var result = results[i];
		sortList.push(result);
	}
	// descending
	sortList.sort(function(a, b) {
		return b["weight"] - a["weight"];
	});

	if (firstProcess) {
		firstProcess = false;
		document.getElementById("searchContainer").style.top = "0%";
		document.getElementById("textField").style.width = "800px";
		document.getElementById("textField").blur();
		document.getElementById("textField").style.background = "none";
		document.getElementById("searchContainer").style.background = "white";
		document.getElementById("bkgTop").className = "backgroundBlur";
		document.getElementById("bkgBottom").className = "backgroundBlur";
		document.getElementById("resultContainer").style.background = "rgba(255, 255, 255, 0.8)";
		document.getElementById("resultContainer").style.display = "block";
		document.getElementsByClassName("myFooter")[0].style.display = "none";
	}
	// process json
	//....

	appendResult(sortList);
}

function appendResult(sorted) {
	// create result element and append into resultContainer
	for (var i in sorted) {
		var result = sorted[i];
		console.log(result["url"] + " w: " + result["weight"]);
	}
}

function inputOnFocus() {
	var wrapper = document.getElementById("searchWrapper");
	if (firstProcess) {
		wrapper.style.border = "2px solid #77b7a1";
	} else {
		wrapper.style.border = "3px solid #77b7a1";
		wrapper.style.borderStyle = "none none solid none";
	}
}

function inputOnBlur() {
	var wrapper = document.getElementById("searchWrapper");
	if (firstProcess) {
		wrapper.style.border = "2px solid rgba(255,255,255,0)";
	} else {
		wrapper.style.border = "3px solid rgba(255,255,255,0)";
		wrapper.style.borderStyle = "none none solid none";
	}
	
}