package app;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Range;

public class WordTemplate extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		String base_path = getServletContext().getRealPath("");
		Map<String, String> map = new HashMap<String, String>();
		String studentId= request.getParameter("studentId");
		// 数据库读取数据
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String url = "jdbc:mysql://127.0.0.1:3306/electivesystem?user=root&password=123456&useUnicode=true&characterEncoding=UTF8";
			Connection connection = DriverManager.getConnection(url);
			PreparedStatement pstamt = connection
					.prepareStatement("select studentId,name from student where studentId=?");
			pstamt.setString(1, studentId);
			ResultSet rs = pstamt.executeQuery();
			if (rs.next()) {
				map.put("${id}", rs.getString(1));
				map.put("${name}", rs.getString(2));
				File file = readwriteWord(base_path + "download/学生信息模版.doc",new File(base_path + "download/" + map.get("${name}") + "信息.doc"), map);
				Download.downloadFile(request, response, file);
			}

		} catch (Exception e) {
			System.out.println(e.getClass());
			e.printStackTrace();
		}

	}

	/*
	 * word模版替换
	 * 
	 * @param file_path word模板路径和名称
	 * 
	 * @param outFile 输入文件
	 * 
	 * @param map 待填充的数据，如${name}->零号芯片
	 * 
	 * @throws IOException
	 */
	public File readwriteWord(String file_path, File out_file, Map<String, String> map) throws IOException {
		// 读取word模板
		File file = new File(file_path);
		FileInputStream in = new FileInputStream(file);
		HWPFDocument hdt = new HWPFDocument(in);

		// 读取word文本内容
		Range range = hdt.getRange();
		// 替换文本内容
		for (Map.Entry<String, String> entry : map.entrySet()) {
			range.replaceText(entry.getKey(), entry.getValue());
		}
		ByteArrayOutputStream ostream = new ByteArrayOutputStream();
		FileOutputStream out = new FileOutputStream(out_file, true);
		hdt.write(ostream);
		out.write(ostream.toByteArray());
		out.close();
		ostream.close();
		hdt.close();
		return out_file;
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	}

}
