package cn.philip.core.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @description: 文件上传服务
 * @author: pfliu
 * @time: 2020/6/27
 */
@Slf4j
@Service
public class FileService {

    /**
     * 保存文件到本地服务器
     *
     * @param fileName 文件名
     * @param fileType 文件类型
     * @param filePath 文件路径
     * @param request  request
     */
    public String saveLocalFile(String fileName, String fileType, String filePath, MultipartFile request) {
        InputStream sis = null;
        FileOutputStream fos = null;
        try {
            File file = new File(filePath + "/" + fileName + "." + fileType);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            sis = request.getInputStream();
            fos = new FileOutputStream(file);
            byte[] content = new byte[1024];
            int len = 0;
            while ((len = sis.read(content)) > -1) {
                fos.write(content, 0, len);
            }
            fos.flush();
            return "success";
        } catch (Exception ex) {
            log.error("saving file error", ex);
            return "fail";
        } finally {
            try {
                if (sis != null) {
                    sis.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                log.error("close os failed.", e);
            }
        }
    }
}
