package app;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.artofsolving.jodconverter.OfficeDocumentConverter;
import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration;
import org.artofsolving.jodconverter.office.OfficeManager;

public class Preview extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String file_path=getServletContext().getRealPath("download/")+request.getParameter("filename");
		File file=new File(file_path);
		if(!file.exists()){
			//如果文件不存在，则先生成该文件
			request.getRequestDispatcher("download?filename=选课指南.xls").forward(request, response);
		}
		preview(request, response, file);
	}
	
	/**
	 * 预览文件
	 * @param request
	 * @param response
	 * @param file	要预览的文件
	 * @throws IOException
	 */
	private void preview(HttpServletRequest request,HttpServletResponse response,File file) throws IOException{
		//转化文件为pdf
		File file_pdf = officeToPdf(file);
		
		//预览
        BufferedInputStream br = new BufferedInputStream(new FileInputStream(file_pdf));
        byte[] buf = new byte[1024];
        int len = 0;
      //  response.reset(); // 非常重要
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition","inline; filename=" + URLEncoder.encode(file_pdf.getName(), "UTF-8"));

        OutputStream out = response.getOutputStream();
        while ((len = br.read(buf)) != -1)
            out.write(buf, 0, len);
        br.close();
        out.close();
	}
	
	public  File officeToPdf(File file){
		//获取文件完整路径
		String path=file.getAbsolutePath();
		DefaultOfficeManagerConfiguration configuration=new DefaultOfficeManagerConfiguration();
		//设置openoffice的安装路径
		configuration.setOfficeHome(new File("X:/Program Files (x86)/OpenOffice 4"));
		//启动OpenOffice的服务
		OfficeManager officeManager = configuration.buildOfficeManager();
		officeManager.start();
		//连接OpenOffice
		OfficeDocumentConverter documentConverter=new OfficeDocumentConverter(officeManager);
		
		//-------转化文件-----------
		//设置输出文件路径
		String output_path=path.replace(path.substring(path.lastIndexOf(".")),".pdf");	//这里直接采用了源文件目录
		File output_file=new File(output_path);
		if(!output_file.getParentFile().exists()){
			//如果文件夹不存在，则创建该文件夹
			output_file.getParentFile().mkdir();
		}
		documentConverter.convert(new File(path), output_file);
		System.out.println("文件转换完毕");
		officeManager.stop();
		System.out.println("OpenOffice服务已关闭");
		return output_file;
		
	}
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().write("请用GET方法访问，谢谢合作");
	}

}
