package top.hzwhzw.iwfileservice.service.impl;

import dto.CoverDTO;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import top.hzwhzw.iwapi.client.ArticleClient;
import top.hzwhzw.iwfileservice.service.FileService;
import top.hzwhzw.iwfileservice.utils.OssUtil;
import vo.CoverVO;
import vo.FileVO;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private final ArticleClient articleClient;
    @Resource
    private OssUtil ossUtil;
    @Override
    public FileVO upload(MultipartFile fileMultipartFile, String scene, String no) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(fileMultipartFile.getInputStream());
        if (bufferedImage == null) {
            throw new IllegalArgumentException("不支持的文件类型，请上传图片文件");
        }
        String url = ossUtil.upload(fileMultipartFile, scene);
        FileVO fileVO = new FileVO();
        fileVO.setUrl(url);
        fileVO.setFileName(fileMultipartFile.getOriginalFilename());
        fileVO.setFileType(scene);
        fileVO.setSize(fileMultipartFile.getSize());
        fileVO.setWidth(bufferedImage.getWidth());
        fileVO.setHeight(bufferedImage.getHeight());
        fileVO.setCreatedAt(LocalDateTime.now());
        if ("avatar".equals(scene)) {
            //TODO 插入avatar表
//            Integer userId = CurrentHolder.getCurrentId();
//            User user = userMapper.selectUserById(userId);
//            Avatar avatar = new Avatar();
//            avatar.setDocumentId(Document.setDocumentId());
//            avatar.setUrl(url);
//            avatar.setAvatarWidth(bufferedImage.getWidth());
//            avatar.setAvatarHeight(bufferedImage.getHeight());
//            avatar.setUserDocumentId(user.getDocumentId());
//            avatar.setUserId(userId);
//            fileMapper.insertAvatar(avatar);
//            imageFileVO.setDocumentId(avatar.getDocumentId());
        } else if ("cover".equals(scene)) {
            // 插入cover表
            CoverDTO cover = new CoverDTO();
            cover.setUrl(url);
            cover.setWidth(bufferedImage.getWidth());
            cover.setHeight(bufferedImage.getHeight());
            cover.setArticleNo(no);
            CoverVO coverVO = articleClient.insertCover(cover);
            fileVO.setFileNo(coverVO.getCoverNo());
        }
        return fileVO;
    }
}
