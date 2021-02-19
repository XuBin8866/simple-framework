package com.xxbb.framework.simplespring.mvc.render.impl;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import com.google.gson.Gson;
import com.xxbb.framework.simplemybatis.session.SqlSession;
import com.xxbb.framework.simplespring.mvc.RequestProcessorChain;
import com.xxbb.framework.simplespring.mvc.render.ResultRender;
import org.apache.poi.ss.usermodel.Workbook;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author xubin
 * @date 2021/1/11 11:58
 */
public class ExcelResultRender implements ResultRender {
	private Object excelData;
	
	public ExcelResultRender(Object excelData) {
		this.excelData = excelData;
	}
	
	@Override
	public void render(RequestProcessorChain requestProcessorChain) throws Exception {
		HttpServletResponse response = requestProcessorChain.getResp();
		List<?> list;
		if (excelData instanceof List<?>) {
			list = (List<?>) excelData;
		} else {
			list = Collections.singletonList(excelData);
		}
		Object object = list.get(0);
		String clazzName = object.getClass().getSimpleName();
		//设置响应头
		response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
		response.setHeader("content-Type", "application/vnd.ms-excel");
		response.setHeader("Content-Disposition", "attachment;filename=" +
				new String((clazzName+System.currentTimeMillis()).getBytes("gb2312"), "iso8859-1") + ".xls");
		Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams("数据表头", "data"),
				list.get(0).getClass(), list);
		workbook.write(response.getOutputStream());
	}
}
