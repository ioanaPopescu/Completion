<%--
  User: Ioana Popescu
  Date: 5/5/14
  Time: 10:28 PM
--%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<html>
<head>
    <title>Procesarea Imaginilor</title>
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resources/css/default.css">
</head>
<body>
<div class="page-title">PROCESAREA IMAGINILOR</div>
<div class="subtitle">SELECTATI IMAGINEA PE CARE DORITI SA O PROCESATI</div>
<form:form method="post" enctype="multipart/form-data" modelAttribute="uploadedFile" action="fileUpload.htm">
    <input type="file" name="file" class="custom-file-input" style="width:122px"/>
    <input type="submit" value="Upload"/>
    <form:errors path="file"/>
</form:form>
<div class="footer">
    DANA IOANA POPESCU<br/>
    Prelucrarea Complexa a Semnalelor in Aplicatii Multimedia
</div>
</body>
</html>
