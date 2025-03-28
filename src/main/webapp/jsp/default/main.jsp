<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<div align="center" class="main_div">
    <!-- 이미지 표시 -->
    <img src="${pageContext.request.contextPath}/piv.png" 
         style="max-width: 90%; height: auto; display: block; margin-bottom: 20px;">
    
    <!-- 클릭하면 예매 페이지로 이동하는 버튼 -->
    <h2>
        <a href="${pageContext.request.contextPath}/ticketing" 
           style="text-decoration: none; color: white; background-color: #ff4500; padding: 10px 20px; 
                  border-radius: 10px; display: inline-block; font-weight: bold;">
            지금 바로 예매하러가기
        </a>
    </h2>
</div>


