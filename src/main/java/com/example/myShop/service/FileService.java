package com.example.myShop.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FileService {

    /**
     * @param uploadPath
     * @param originalFileName
     * @param fileData
     * @return 저장된 파일 이름을 리턴한다. (UUID + 확장자)
     * @throws Exception
     */
    public String uploadFile(String uploadPath, String originalFileName, byte[] fileData)
            throws Exception {
        UUID uuid = UUID.randomUUID();
        String extension = originalFileName.substring(originalFileName.lastIndexOf('.'));
        String savedFileName = uuid.toString() + extension;
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists() && !uploadDir.mkdirs()) {
            throw new IOException("업로드 디렉터리를 생성할 수 없습니다. path=" + uploadPath);
        }
        String fileUploadFullUrl = uploadPath + "/" + savedFileName;
        FileOutputStream fos = new FileOutputStream(fileUploadFullUrl);
        fos.write(fileData);
        fos.close();
        return savedFileName;
    }

    public void deleteFile(String filePath) {
        File deleteFile = new File(filePath);
        if (deleteFile.exists()) {
            deleteFile.delete();
            log.info("파일을 삭제하였습니다.");
        } else {
            log.info("파일이 존재하지 않습니다.");
        }
    }
}
