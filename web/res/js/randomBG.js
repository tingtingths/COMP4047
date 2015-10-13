//background
var bkg_min = 1;
var bkg_max = 5; //how many pictures
var current = Math.floor((Math.random() * bkg_max) + bkg_min);
var bkg_refresh_rate = 360 * 1000; //360 seconds
var isBkgTop = false;
var pool = [];
var img = new Image();

	changeBkg();
	setInterval(function() {
		changeBkg();
	}, bkg_refresh_rate); //change bkg for every bkg_refresh_rate

function changeBkg() {
	if (pool.length == 0) {
		for (var i = bkg_min; i <= bkg_max; i++) {
			pool.push(i);
		}
		pool = shuffleArray(pool);
	}
	if (pool.length == 1) { //add four elements into the pool. To avoid having the same bkg.
		var tmp = [];
		for (var i = bkg_min; i <= bkg_max; i++) {
			tmp.push(i);
		}
		tmp.splice(pool[0]-1, 1);
		tmp = shuffleArray(tmp);
		pool = pool.concat(tmp);
	}
	current = pool[0];
	pool.splice(0, 1);

	img.src = "./res/img/bkg_" + current + ".jpg";
	img.onload = function() { //wait untill the image is loaded
		if (isBkgTop) {
			document.getElementById("bkgTop").style.backgroundImage = "url(" + img.src + ")";
			document.getElementById("bkgTop").style.opacity = "1";

			isBkgTop = false;
		} else {
			document.getElementById("bkgBottom").style.backgroundImage = "url(" + img.src + ")";;
			document.getElementById("bkgTop").style.opacity = "0";
			isBkgTop = true;
		}
	};
}

function shuffleArray(arr) {
	var outArr = [];
	var len = arr.length;

	for (var i = 0; i < len; i++) {
		var pick = Math.floor(Math.random() * arr.length);
		outArr.push(arr[pick]);
		arr.splice(pick, 1);
	};

	return outArr;
}