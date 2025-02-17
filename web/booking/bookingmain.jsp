<%@ page import="java.util.List" %>
<%@ page import="model.booking.BookingBean" %>
<%@ page import="model.booking.BookingDAO" %><%--
  Created by IntelliJ IDEA.
  User: jy
  Date: 2019/11/18
  Time: 11:26 오전
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <link rel="stylesheet" href="../assets/style/bookingmain.css"/>
</head>
<body>
<%
    String email = (String)session.getAttribute("email");
    if (email ==null){
        %>
<script>
    alert('로그인이 필요합니다!');
    history.go(-1);
</script>

<%
    }
    BookingDAO dao = new BookingDAO();
    List<BookingBean> bean = dao.getBooking(email);
    if (bean.isEmpty()){
%>
<div class="container">
    <h2>예약확인</h2>
    <p style="color: darkred">예약내역이 없습니다.</p>
</div>
<%
    }else{
%>
<div class="container">
<h1>예약확인</h1>
<%
        for (BookingBean b : bean){
%>

    <div class="booking-card" data-aos="fade-left">
        <div class="booked-motel-image">
            <img src="<%=b.getImg()%>" alt="" onclick="location.href='index.jsp?main=/booking/bookingdetail.jsp?no=<%=b.getNo()%>'">
        </div>
        <div class="booked-motel-contents">
            <h3 class="booked-motel-name"><%=b.getName()%></h3>
            <ul>
                <li class="booked-motel-info"><%=b.getInfo()%></li>
                <li class="booked-date"><span><%=b.getCheckin()%></span> ~ <span><%=b.getCheckout()%></span></li>
                <li class="booked-price"><%=b.getPrice()%></li>
            </ul>
        </div>
    </div>
        <%}%>
</div>
    <%}%>
</body>
</html>
