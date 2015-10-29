/**
 * Created by Ting on 12/10/2015.
 */

apiURL = window.location.protocol + "//" + window.location.host + "/spider/search"
var firstProcess = true;
var result;
var ptr = 0;
var appendStep = 5;
var sortedResult = [];
var appendLimit = 15; // 15 items per page

function search(ele) {
	// debug calulate processing time
	var doRequest = false;

	if (ele == document.getElementById("textField")) {
		if (event.keyCode == 13) {
			doRequest = true;
		}
	} else {
		doRequest = true;
	}
	if (doRequest) {
		var searchPattern = document.getElementById("textField").value;
		if (searchPattern.trim() != "") {
			console.log("init ajax request...");
			// change icon
			document.getElementById("goBtn").src = "./res/img/loader.gif";

			console.log("value : " + searchPattern);

			// Http request
			var req = new XMLHttpRequest();

			req.onreadystatechange = function () {
				if (req.readyState == 4 && req.status == 200) { //XMLHttpRequest 'DONE' and 'SUCCESS'
					var json = req.responseText;
					// arrange the result
					startUiProcess(json);

				}
			}

			req.open("GET", apiURL + "?q=" + searchPattern, true);
			req.send();
		}

		// UI debug
		//startUiProcess();
	}
}

function startUiProcess(json) {
	// reset stuffs
	document.getElementById("goBtn").src = "./res/img/goBtn.png"; // icon
	document.getElementById("results").innerHTML = ""; // container
	sortedResult = [];

	console.log("Processing json...");
	//console.log("json: " + json);
	var results = JSON.parse(json);

	//console.log(jsonArr);
	for (var i in results) {
		if (i != results.length - 1) {
			var result = results[i];
			sortedResult.push(result);
		} else {
			var count = results.length - 1;
			document.getElementById("timeTaken").innerHTML = count + " results, " + results[i]["ms"] + " ms";
		}
	}
	// descending
	sortedResult.sort(function(a, b) {
		return b["weight"] - a["weight"];
	});

	if (firstProcess) {
		firstProcess = false;
		document.getElementById("searchContainer").style.top = "0%";
		//document.getElementById("textField").style.width = "800px";
		document.getElementById("textField").blur();
		document.getElementById("textField").style.background = "none";
		document.getElementById("searchContainer").style.background = "white";
		document.getElementById("bkgTop").className = "backgroundBlur";
		document.getElementById("bkgBottom").className = "backgroundBlur";
		document.getElementById("whiteCover").style.opacity = "1";
		document.getElementById("resultContainer").style.opacity = "1";
		document.getElementsByClassName("myFooter")[0].style.display = "none";
	}
	// process json
	//....

	appendResult();
}

function appendResult() {
	var count = 0;
	var container = document.getElementById("results");
	// create result element and append into resultContainer
	for (var i in sortedResult) {
		if (count < appendLimit) {
			var result = sortedResult[i];
			var domain = result["domain"];
			var url = result["url"];
			var title = result["title"];
			var weight = result["weight"];
			var eleResult = document.createElement("div");
			eleResult.className = "result";

			// set title
			var eleTitle = document.createElement("a");
			eleTitle.href = url;
			eleTitle.target = "_blank";
			eleTitle.className = "resultTitle";
			if (title == "null") {
				eleTitle.innerHTML = domain;
			} else {
				eleTitle.innerHTML = title;
			}

			// set weight
			var eleWeight = document.createElement("span");
			eleWeight.className = "resultWeight";
			eleWeight.innerHTML = "[" + weight + "]";

			// set cite
			var eleUrl = document.createElement("cite");
			eleUrl.className = "resultCite";
			eleUrl.innerHTML = url;

			// append elements
			eleResult.appendChild(eleTitle);
			eleResult.appendChild(eleWeight);
			eleResult.appendChild(document.createElement("br")); // new line
			eleResult.appendChild(eleUrl);
			eleResult.appendChild(document.createElement("br")); // new line
			container.appendChild(eleResult);

			count += 1;
		}
	}
	// remove processed object
	sortedResult.splice(0, count); // delete 1 item from i

	if ((sortedResult.length > 0) && (count = appendLimit)) {
		var moreBtn = document.createElement("button");
		moreBtn.className = "moreBtn";
		moreBtn.innerHTML = "More";
		moreBtn.setAttribute("onclick", "appendResult()");
		document.getElementById("moreBtn").style.display = "block";
	} else {
		document.getElementById("moreBtn").style.display = "none";
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