package app;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import jxl.write.Label;

public class Clazz extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		try {
			String orderId=request.getParameter("orderId");
			String courseName=request.getParameter("courseName");
			Class.forName("com.mysql.jdbc.Driver");
			String url="jdbc:mysql://127.0.0.1:3306/electivesystem?user=root&password=123456&useUnicode=true&characterEncoding=UTF8";
			Connection connection = DriverManager.getConnection(url);
			String sql="select studentId,student.name from student inner join compete_course on student.studentId=compete_course.student_id inner join course on course.id=compete_course.course_id where course.courseId=? and course.orderId=?";			
			PreparedStatement prepareStatement = connection.prepareStatement(sql);
			prepareStatement.setString(1,courseName);
			prepareStatement.setString(2, orderId);
			ResultSet rs = prepareStatement.executeQuery();
			System.out.println(prepareStatement.toString());
			List<Label> labels=new LinkedList<Label>();
			labels.add(new Label(0,0,"学号"));
			labels.add(new Label(1,0,"姓名"));
			for(int row=1;rs.next();row++){
				for(int column=0,columns=rs.getMetaData().getColumnCount();column<columns;column++){
					labels.add(new Label(column,row,rs.getString(column+1)));
				}
			}
			File file = Download.createExcel(new File(getServletContext().getRealPath("download/"+courseName+".xls")), labels);
			Download.downloadFile(request, response, file);
		} catch (Exception e) {
			
			e.printStackTrace();
			throw new RuntimeException();
		}
	}


	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	}

}
