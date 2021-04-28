package unnet.weixin.netdisk.utils;

import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

public class JudgeImage {
    public static boolean checkImage(MultipartFile file){
        return Objects.requireNonNull(file.getContentType()).startsWith("image/");
    }
}
