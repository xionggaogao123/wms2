package com.huanhong.common.units.excel;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.hutool.core.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Map;

@Slf4j
public class ExportExcel {

    public static void exportExcel(String templatePath, String temDir, String fileName, Map<String, Object> params, HttpServletRequest request, HttpServletResponse response) {
        Assert.notNull(templatePath, "模板路径不能为空");
        Assert.notNull(temDir, "临时文件路径不能为空");
        Assert.notNull(fileName, "导出文件名不能为空");
        Assert.isTrue(fileName.endsWith(".xlsx"), "excel导出请使用xlsx格式");
        FileOutputStream fos = null;
        OutputStream out = null;
        if (!temDir.endsWith("/")) {
            temDir = temDir + File.separator;
        }
        File dir = new File(temDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try {
            if (request != null) {
                String userAgent = request.getHeader("user-agent").toLowerCase();
                if (userAgent.contains("msie") || userAgent.contains("like gecko")) {
                    fileName = URLEncoder.encode(fileName, "UTF-8");
                } else {
                    fileName = new String(fileName.getBytes("utf-8"), "ISO-8859-1");
                }
            }
            TemplateExportParams templateExportParams = new TemplateExportParams(
                    templatePath, true);
            templateExportParams.setColForEach(true);
            Workbook book = ExcelExportUtil.exportExcel(templateExportParams, params);
            File tempDir = new File(temDir);
            if (!tempDir.exists()) {
                tempDir.mkdirs();
            }
            String tmpPath = temDir + fileName;
            fos = new FileOutputStream(tmpPath);
            book.write(fos);
            if (response != null) {
                //设置强制下载不打开
                response.setContentType("application/force-download");
                //设置文件名
                response.addHeader("Content-Disposition", "attachment;fileName=" + fileName);
                out = response.getOutputStream();
                book.write(out);
            }

        } catch (Exception e) {
            log.error("excel 导出异常", e);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (fos != null) {
                    fos.close();
                }
                delFile(temDir, fileName);
            } catch (IOException e) {
                log.error("excel 导出关闭流异常", e);
            }
        }
    }

    /**
     * 删除临时生成的文件
     */
    public static void delFile(String filePath, String fileName) {
        File file = new File(filePath + fileName);
        File file1 = new File(filePath);
        file.delete();
        file1.delete();
    }


}
