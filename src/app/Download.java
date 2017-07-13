package app;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class Download extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 从数据库拿数据
		try {
				Class.forName("com.mysql.jdbc.Driver");
				String url="jdbc:mysql://127.0.0.1:3306/electivesystem?user=root&password=123456&useUnicode=true&characterEncoding=UTF8";
				Connection connection=DriverManager.getConnection(url);
				PreparedStatement prepareStatement = connection.prepareStatement("select college.name,course.courseId,course.orderId,course.name,teacher.name,course.credit, course.showTime,course.place,course.allWeek,clazz.name,course.properties,course.note from college inner join course on course.college_id=college.id	inner join course_teacher_between on course_teacher_between.course_id=course.id inner join teacher on course_teacher_between.teacher_id=teacher.id left join course_clazz_between on course_clazz_between.course_id=course.id left join clazz on clazz.id=course_clazz_between.clazz_id where course.isOpen=1;");							
				ResultSet rs = prepareStatement.executeQuery();
				List<Label> list=new LinkedList<Label>();
				list.add(new Label(0,0,"开课院系"));
				list.add(new Label(1,0,"课程号"));
				list.add(new Label(2,0,"课序号"));
				list.add(new Label(3,0,"课程名"));
				list.add(new Label(4,0,"上课教师"));
				list.add(new Label(5,0,"学分"));
				list.add(new Label(6,0,"时间"));
				list.add(new Label(7,0,"地点"));
				list.add(new Label(8,0,"周次"));
				list.add(new Label(9,0,"班级"));
				list.add(new Label(10,0,"课程属性"));
				list.add(new Label(11,0,"备注"));
				for(int row=1;rs.next();row++){
					for(int column=0,columns=rs.getMetaData().getColumnCount();column<columns;column++){
						list.add(new Label(column,row,"  "+(rs.getString(column+1)==null?"":rs.getString(column+1))+"	"));
					}
					
				}
			// 生成Excel文件
			File file=createExcel(new File(getServletContext().getRealPath("download/选课指南.xls")), list);		

			// 下载
			Download.downloadFile(request, response, file);
		} catch (Exception e) {
			System.out.println(e.getClass());
			e.printStackTrace();
		}
	}

	/**
	 * 以 简单字符串的内容填充Excel(此方法只有一个sheet)
	 * @param file_name	保存的文件路径,包含文件名
	 * @param labels	表格内容数组
	 * @return
	 * @throws IOException
	 * @throws RowsExceededException
	 * @throws WriteException
	 */
	protected static File createExcel(File file,List<Label> labels) throws IOException, RowsExceededException, WriteException {				
		WritableWorkbook workbook = Workbook.createWorkbook(file);
		WritableSheet sheet = workbook.createSheet(file.getName(), 0);
		for (Label label : labels) {
			sheet.addCell(label);
		}
		workbook.write();
		workbook.close();
		return file;
		
	}

	/**
	 * 下载指定的文件
	 * 
	 * @param request
	 * @param response
	 * @param file_name
	 *            文件名如 test.xls,不包含路径
	 * @throws IOException
	 */
	public static void  downloadFile(HttpServletRequest request, HttpServletResponse response, File file)
			throws IOException {

		FileInputStream in = new FileInputStream(file);

		// 设置响应头
		response.setHeader("content-disposition", "attachment;fileName=" + URLEncoder.encode(file.getName(), "UTF-8"));

		// 写入响应
		ServletOutputStream outputStream = response.getOutputStream();
		byte[] b = new byte[1024];
		while (in.read(b) != -1) {
			outputStream.write(b);
		}

		in.close();
		outputStream.close();
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().write("请用GET方式访问！！");
	}



}
