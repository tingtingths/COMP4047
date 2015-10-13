<%--
  Created by IntelliJ IDEA.
  User: Ting
  Date: 12/10/2015
  Time: 20:20
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <link rel="stylesheet" href="./res/css/style.css" type="text/css">
    <script src="./res/js/AjaxUiProcessing.js"></script>
    <script src="./res/js/randomBG.js"></script>
    <title></title>
</head>
<script type="text/javascript">
    function inputOnFocus() {
        document.getElementById("searchWrapper").style.border = "2px solid #77b7a1";
    }

    function inputOnBlur() {
        document.getElementById("searchWrapper").style.border = "2px solid rgba(255,255,255,0)";
    }
</script>
<body>
<span id="bkgBottom"></span>
<span id="bkgTop"></span>
<div id="searchContainer">
    <div id="searchWrapper">
        <input type="text" onfocus="inputOnFocus()" onblur="inputOnBlur()" onkeydown="search(this)" maxlength="1024" id="textField">
        <img id="goBtn" onclick="search(this)" src="./res/img/goBtn.png"/>
    </div>
</div>
</body>
</html>