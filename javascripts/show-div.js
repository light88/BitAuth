function showDiv(id) {
	if (id == "about") {
		document.getElementById("about-div").style.display = "inline";
		document.getElementById("passwords-div").style.display = "none";
		document.getElementById("features-div").style.display = "none";
		document.getElementById("changes-div").style.display = "none";
	} else if (id == "passwords") {
		document.getElementById("about-div").style.display = "none";
		document.getElementById("passwords-div").style.display = "inline";
		document.getElementById("features-div").style.display = "none";
		document.getElementById("changes-div").style.display = "none";
	} else if (id == "features") {
		document.getElementById("about-div").style.display = "none";
		document.getElementById("passwords-div").style.display = "none";
		document.getElementById("features-div").style.display = "inline";
		document.getElementById("changes-div").style.display = "none";
	} else if (id == "changes") {
		document.getElementById("about-div").style.display = "none";
		document.getElementById("passwords-div").style.display = "none";
		document.getElementById("features-div").style.display = "none";
		document.getElementById("changes-div").style.display = "inline";
	}
}