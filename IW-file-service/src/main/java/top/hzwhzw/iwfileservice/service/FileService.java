package top.hzwhzw.iwfileservice.service;

import org.springframework.web.multipart.MultipartFile;
import vo.FileVO;

import java.io.IOException;

public interface FileService {
    FileVO upload(MultipartFile fileMultipartFile, String scene, String no) throws IOException;
}
