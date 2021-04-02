package com.xxbb.framework.simplespring.mvc.render.impl;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import com.xxbb.framework.simplespring.mvc.RequestProcessorChain;
import com.xxbb.framework.simplespring.mvc.render.ResultRender;
import com.xxbb.framework.simplespring.util.LogUtil;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;

import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

/**
 * @author xubin
 * @date 2021/1/11 11:58
 */
public class ExcelResultRender implements ResultRender {
	/**
	 * 传入数据
	 */
	private Object excelData;
	/**
	 * 日志
	 */
	private final Logger log = LogUtil.getLogger();
	
	public ExcelResultRender(Object excelData) {
		this.excelData = excelData;
	}
	
	@Override
	public void render(RequestProcessorChain requestProcessorChain) throws Exception {
		HttpServletResponse response = requestProcessorChain.getResp();
		List<?> list;
		
		if (excelData == null) {
			log.info("export data is null");
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"导出数据表格失败！");
			return;
		} else if (excelData instanceof List<?>) {
			log.info("export data type:list");
			list=(List<?>)excelData;
			if(list.isEmpty()){
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"导出数据表格失败！");
				return;
			}
		} else {
			log.info("export data type:object");
			list = Collections.singletonList(excelData);
		}
		Object object = list.get(0);
		String name = object.getClass().getSimpleName()+ "-" + System.currentTimeMillis();
		//设置响应头
		response.setHeader("content-Type", "application/vnd.ms-excel");
		response.setHeader("Content-Disposition", "attachment;filename=" +
				new String(name.getBytes(StandardCharsets.UTF_8), "iso8859-1") + ".xls");
		Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams("name", "data"),
				object.getClass(), list);
		workbook.write(response.getOutputStream());
	}
}
