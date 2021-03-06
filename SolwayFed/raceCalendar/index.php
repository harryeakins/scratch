<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"><!-- InstanceBegin template="/Templates/general.dwt" codeOutsideHTMLIsLocked="false" -->
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<!-- InstanceBeginEditable name="doctitle" -->
<title>The Solway Federation of Racing Pigeon Societies - Race Calendar</title>
<!-- InstanceEndEditable -->
<link rel="stylesheet" type="text/css" href="/css/solwayFed.css" />
<!--[if IE]>
<link rel="stylesheet" type="text/css" href="/css/ieFixes.css" />
<!-[end if]-->
<link rel="stylesheet" type="text/css" href="/css/solwayFed_print.css" media="print" />
<script type="text/javascript" src="/scripts/solwayFed.js"></script>
<script src="/Scripts/AC_RunActiveContent.js" type="text/javascript"></script>
<!-- InstanceBeginEditable name="head" --><!-- InstanceEndEditable -->
</head>

<body onload="heights();" onresize="heights();" id="body">
	<div id="topBanner">
		<div id="search">
			 <? include("c:\sites_web\sarah_solwayFed\includes\search.php"); ?>
		</div>
		<div id="logo">
			<? include("c:\sites_web\sarah_solwayFed\includes\logo.php"); ?>
		</div>
		<div id="banner">
			  <? include("c:\sites_web\sarah_solwayFed\includes\banner.php"); ?>
		</div>
	</div>
	<div id="nav">
		<? include("c:\sites_web\sarah_solwayFed\includes\\navigation.php"); ?>
	</div>
	<div id="belowNav">
		<div id="contentArea">
			<div id="leftCol"><!-- InstanceBeginEditable name="EditRegion3" -->
			  <h1>Race Calendar</h1>
                <?php
                require_once ("c:\sites_web\sarah_solwayFed\admin\utils.php");

                $dbh = csConnect();
                $result = csExecuteQuery($dbh,
                    "SELECT * FROM csEvents WHERE date >= CURRENT_DATE() ORDER BY date");



                while ($row = mysql_fetch_assoc($result)) {
                    $friendlyDate = date('d/m/Y', strtotime($row["date"]));
                ?>
                    <div class="race">
                    <a href="details.php?id=<?php echo $row["id"]; ?>"><img src="/includes/thumbnail.php?s=181&path=../<?php echo htmlspecialchars($row["imageFilename"]); ?>" alt="raceImage"/></a>
                    <p><span><strong>Location:</strong></span><?php echo htmlspecialchars($row["racepoint"]); ?><br/>
                    <span><strong>Date:</strong></span><?php echo htmlspecialchars($friendlyDate); ?></p>
                    <p><?php echo nl2br(htmlspecialchars(csTruncateString($row["details"], 200))); ?><br/>
                    <a href="details.php?id=<?php echo $row["id"]; ?>">read more...</a></p>
                    </div>
                    <hr/>

                <?php
                }
                ?>
			<!-- InstanceEndEditable --></div>
			<div id="rightCol">
			   <? include("c:\sites_web\sarah_solwayFed\includes\\next.php"); ?>
			</div>
		</div>
	</div>
	<div id="footer">
		  <? include("c:\sites_web\sarah_solwayFed\includes\footer.php"); ?>
	</div>
	<script src="http://www.google-analytics.com/urchin.js" type="text/javascript">
</script>
<script type="text/javascript">
_uacct = "UA-3945837-1";
urchinTracker();
</script>
</body>
<!-- InstanceEnd --></html>
