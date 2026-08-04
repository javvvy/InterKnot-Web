package top.hzwhzw.iwfileservice.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ObjectMetadata;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import top.hzwhzw.iwfileservice.pojo.OssProperties;

import java.io.InputStream;
import java.util.UUID;

@Component
public class OssUtil {
    @Resource
    private OssProperties ossProperties;
    public String upload(MultipartFile file, String scene) {
        // 1. 文件空判断
        if (file.isEmpty()) {
            throw new RuntimeException("上传文件不能为空");
        }

        OSS ossClient = null;
        try (InputStream inputStream = file.getInputStream()) {
            // 2. 初始化OSS客户端
            ossClient = new OSSClientBuilder().build(
                    ossProperties.getEndpoint(),
                    ossProperties.getAccessKeyId(),
                    ossProperties.getAccessKeySecret()
            );

            // 3. 原始文件名 + 后缀
            String originalFilename = file.getOriginalFilename();
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

            // 4. 生成唯一文件名
            String uuid = UUID.randomUUID().toString().replace("-", "");
            // 拼接：场景/UUID.后缀
            String fileName = (scene == null ? "" : scene + "/") + uuid + suffix;

            // 5. 设置元数据（解决图片预览问题）
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(getContentType(suffix));
            metadata.setContentLength(file.getSize());

            // 6. 上传
            ossClient.putObject(
                    ossProperties.getBucketName(),
                    fileName,
                    inputStream,
                    metadata
            );

            // 7. 返回最终访问地址
            return ossProperties.getDomain() + "/" + fileName;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("文件上传失败：" + e.getMessage());
        } finally {
            // 关闭客户端
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
    private String getContentType(String fileName) {
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        switch (suffix.toLowerCase()) {
            case ".jpg": case ".jpeg": return "image/jpeg";
            case ".png": return "image/png";
            case ".gif": return "image/gif";
            default: return "application/octet-stream";
        }
    }
    /**
     * 删除OSS文件
     * @param fileName 文件名（不含域名）
     */
    public void delete(String fileName) {
        OSS ossClient = new OSSClientBuilder().build(
                ossProperties.getEndpoint(),
                ossProperties.getAccessKeyId(),
                ossProperties.getAccessKeySecret()
        );
        try {
            ossClient.deleteObject(ossProperties.getBucketName(), fileName);
        } finally {
            ossClient.shutdown();
        }
    }
}
