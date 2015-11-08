<%--
  Created by IntelliJ IDEA.
  User: Ting
  Date: 12/10/2015
  Time: 20:20
  To change this template use File | SearchEngine.Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <link rel="stylesheet" href="./res/css/style.css" type="text/css">
    <script src="./res/js/AjaxUiProcessing.js"></script>
    <script src="./res/js/randomBG.js"></script>
    <title></title>
</head>
<body>
<span id="timeTaken"></span>
<span id="bkgBottom"></span>
<span id="bkgTop"></span>
<span id="whiteCover"></span>
<div id="searchContainer">
    <div id="logo"><span id="firstLetter">m</span>ultiverse</div>
    <div id="searchWrapper">
        <input type="text" autofocus onfocus="inputOnFocus()" onblur="inputOnBlur()" onkeydown="search(this)" maxlength="1024" id="textField">
        <img id="goBtn" onclick="search(this)" src="./res/img/goBtn.png"/>
    </div>
    <br class="spacer">
    <img id="helpBtn" onclick="popHelp()" src="./res/img/helpBtn.png"/>
    <br class="spacer">
    <div id="helpBox">
        Basic search:<br>
        <span class="innerText">Type in keyword(s) and click the arrow on the right.<br></span>
        Advance search with AND/OR operators:<br>
        <span class="innerText">The AND/OR condition must be in the following pattern;<br></span>
        <span class="innerText">(? AND/OR ?) , where ? should be a <span class="highlight">keyword</span> or <span class="highlight">another search pattern</span>.</span>
    </div>
</div>
<div id="lower">
    <div id="pagesContainer"></div>
    <div id="resultContainer">
        <div id="results">
            <div class="result">
            </div>
        </div>
        <button id="moreBtn" onclick="appendResult()">More</button>
        <br>
    </div>
</div>
<div class="myFooter"><span onclick="changeBkg()">COMP4047 Project | Best viewed in Chrome</span></div>
</body>
</html>